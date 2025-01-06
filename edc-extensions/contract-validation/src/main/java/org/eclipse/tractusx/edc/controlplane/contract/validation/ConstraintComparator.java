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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.MultiplicityConstraint;
import org.eclipse.edc.spi.types.TypeManager;

import java.util.Comparator;
import java.util.Objects;


public class ConstraintComparator implements Comparator<Constraint> {

    private final Comparator<MultiplicityConstraint> multiplicityConstraintComparator;
    private final Comparator<AtomicConstraint> atomicConstraintComparator;

    public ConstraintComparator(Comparator<MultiplicityConstraint> multiplicityConstraintComparator, Comparator<AtomicConstraint> atomicConstraintComparator) {
        this.multiplicityConstraintComparator = multiplicityConstraintComparator;
        this.atomicConstraintComparator = atomicConstraintComparator;
    }

    @Override
    public int compare(Constraint c1, Constraint c2) {

        if (c1 instanceof MultiplicityConstraint mc1 && c2 instanceof MultiplicityConstraint mc2) {
            return Objects.compare(mc1, mc2, multiplicityConstraintComparator);
        }

        if (c1 instanceof AtomicConstraint ac1 && c2 instanceof AtomicConstraint ac2) {
            return Objects.compare(ac1, ac2, atomicConstraintComparator);
        }

        // one of them is MultiplicityConstraint and other is AtomicConstraint
        // sort MultiplicityConstraint < AtomicConstraint
        if (c1 instanceof MultiplicityConstraint) {
            return -1;
        } else {
            return 1;
        }
    }

    public static class AtomicConstraintComparator implements Expression.Visitor<Object>, Comparator<AtomicConstraint> {

        @Override
        public int compare(AtomicConstraint o1, AtomicConstraint o2) {


            return Comparator.comparing((AtomicConstraint o) -> expressionAsString(o.getLeftExpression()))
                    .thenComparing(AtomicConstraint::getOperator)
                    .thenComparing((AtomicConstraint o) -> expressionAsString(o.getRightExpression()))
                    .compare(o1, o2);
        }

        private String expressionAsString(Expression expression) {
            return String.valueOf(expression.accept(this));
        }

        @Override
        public Object visitLiteralExpression(LiteralExpression expression) {
            return expression.getValue();
        }
    }

    public static class MultiplicityConstraintComparator implements Comparator<MultiplicityConstraint> {

        private final ObjectMapper mapper;

        public MultiplicityConstraintComparator(TypeManager typeManager) {
            this.mapper = typeManager.getMapper();
        }

        @Override
        public int compare(MultiplicityConstraint o1, MultiplicityConstraint o2) {

            return Comparator.comparing((MultiplicityConstraint o) -> o.getClass().getName())
                    .thenComparing(this::compareByHash)
                    .compare(o1, o2);
        }

        /**
         * For a give json structure, a hashcode is always unique.
         *
         * @param constraint {@link MultiplicityConstraint}
         * @return hashcode of the constraint
         */
        private int compareByHash(MultiplicityConstraint constraint) {

            JsonNode jsonNode = mapper.valueToTree(constraint);
            return jsonNode.hashCode();
        }
    }
}
