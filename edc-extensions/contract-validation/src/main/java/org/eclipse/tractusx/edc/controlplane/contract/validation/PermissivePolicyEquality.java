/*
 * Copyright (c) 2024 SAP SE
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.tractusx.edc.controlplane.contract.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.connector.controlplane.contract.policy.PolicyEquality;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Duty;
import org.eclipse.edc.policy.model.MultiplicityConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.policy.model.Prohibition;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;

public class PermissivePolicyEquality extends PolicyEquality implements BiPredicate<Policy, Policy> {

    private final Monitor monitor;
    private final ObjectMapper mapper;

    private final Comparator<Constraint> constraintComparator;

    public PermissivePolicyEquality(TypeManager typeManager, Monitor monitor, Comparator<Constraint> constraintComparator) {
        super(typeManager);
        this.monitor = monitor;
        this.mapper = typeManager.getMapper();
        this.constraintComparator = constraintComparator;
    }

    @Override
    public boolean test(Policy one, Policy two) {

        if (super.test(one, two)) {
            return true;
        }

        // sort the policy and test again
        Policy oneSorted = sortPolicy(one);
        Policy twoSorted = sortPolicy(two);

        monitor.debug("First Policy Constraints sorted before equality check: original %s sorted %s".formatted(mapper.valueToTree(one), mapper.valueToTree(oneSorted)));
        monitor.debug("Second Policy Constraints sorted before equality check: original %s sorted %s".formatted(mapper.valueToTree(two), mapper.valueToTree(twoSorted)));

        return super.test(oneSorted, twoSorted);
    }

    protected Policy sortPolicy(Policy policy) {

        // make a copy policy, don't change existing policy
        Policy copy = policy.toBuilder().build();

        // toBuilder doesn't allow overriding a field of type List, instead it adds all
        copy.getPermissions().clear();
        copy.getProhibitions().clear();
        copy.getObligations().clear();

        return copy.toBuilder()
                .permissions(sortPermission(policy.getPermissions()))
                .prohibitions(sortProhibition(policy.getProhibitions()))
                .duties(sortObligation(policy.getObligations()))
                .build();
    }

    protected List<Permission> sortPermission(List<Permission> permissions) {

        return permissions.stream()
                .map(p -> toBuilder(p).constraints(sortConstraint(p.getConstraints())).build())
                .toList();
    }

    protected List<Prohibition> sortProhibition(List<Prohibition> prohibitions) {
        return prohibitions.stream()
                .map(p -> toBuilder(p).constraints(sortConstraint(p.getConstraints())).build())
                .toList();
    }

    protected List<Duty> sortObligation(List<Duty> obligations) {
        return obligations.stream()
                .map(p -> toBuilder(p).constraints(sortConstraint(p.getConstraints())).build())
                .toList();
    }

    protected List<Constraint> sortConstraint(List<Constraint> constraints) {

        return constraints.stream()
                .map(this::unwrapMultiplicityConstraint)
                .map(this::sortMultiplicityConstraint)
                .sorted(constraintComparator)
                .toList();
    }

    /**
     * If a {@link MultiplicityConstraint} has just one constraint,
     * unwrap it, because it is logically equal to wrapped constraint.
     * AND(CONSTRAINT_A) === CONSTRAINT_A
     * OR(CONSTRAINT_B) === CONSTRAINT_B
     * XONE(CONSTRAINT_C) === CONSTRAINT_C
     * AND(OR(CONSTRAINT_D)) === CONSTRAINT_D
     * AND(OR(AND(CONSTRAINT_E))) === CONSTRAINT_E
     *
     * @param constraint input constraint
     * @return constraint updated constraint or original
     */
    protected Constraint unwrapMultiplicityConstraint(Constraint constraint) {

        if (constraint instanceof MultiplicityConstraint mc && mc.getConstraints().size() == 1) {

            return unwrapMultiplicityConstraint(mc.getConstraints().get(0));
        }
        return constraint;
    }

    protected Constraint sortMultiplicityConstraint(Constraint constraint) {

        if (constraint instanceof MultiplicityConstraint mc) {
            return mc.create(sortConstraint(mc.getConstraints()));
        }
        return constraint;
    }

    protected Permission.Builder toBuilder(Permission permission) {
        return Permission.Builder.newInstance()
                .duties(permission.getDuties())
                .action(permission.getAction());
    }

    protected Prohibition.Builder toBuilder(Prohibition prohibition) {
        return Prohibition.Builder.newInstance()
                .remedies(prohibition.getRemedies())
                .action(prohibition.getAction());
    }

    protected Duty.Builder toBuilder(Duty obligation) {
        return Duty.Builder.newInstance()
                .consequences(obligation.getConsequences())
                .action(obligation.getAction());
    }
}
