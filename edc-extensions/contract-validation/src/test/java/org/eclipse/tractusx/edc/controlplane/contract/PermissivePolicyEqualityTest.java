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

package org.eclipse.tractusx.edc.controlplane.contract;

import org.eclipse.edc.json.JacksonTypeManager;
import org.eclipse.edc.policy.model.Action;
import org.eclipse.edc.policy.model.AndConstraint;
import org.eclipse.edc.policy.model.AtomicConstraint;
import org.eclipse.edc.policy.model.Constraint;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.OrConstraint;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator.AtomicConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator.MultiplicityConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.PermissivePolicyEquality;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PermissivePolicyEqualityTest {

    public static final String BUSINESS_PARTNER_GROUP = "BusinessPartnerGroup";
    public static final String BUSINESS_PARTNER_NUMBER = "BusinessPartnerNumber";
    public static final String MEMBERSHIP_CREDENTIAL = "Membership";
    public static final String GROUP_GOLD_PARTNERS = "gold-partners";
    public static final String BPN_1 = "BPNL0001";
    public static final String GROUP_SILVER_PARTNERS = "silver-partners";
    public static final String BPN_2 = "BPNL0002";

    private final TypeManager typeManager = new JacksonTypeManager();
    private final Monitor monitor = new ConsoleMonitor();
    private final MultiplicityConstraintComparator multiplicityConstraintComparator = new MultiplicityConstraintComparator(typeManager);
    private final ConstraintComparator constraintComparator = new ConstraintComparator(multiplicityConstraintComparator, new AtomicConstraintComparator());

    PermissivePolicyEquality policyEqualityV2;

    @BeforeEach
    void setup() {
        policyEqualityV2 = new PermissivePolicyEquality(typeManager, monitor, constraintComparator);
    }


    @Test
    void testSamePolicy() {

        AtomicConstraint ac = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        OrConstraint oc = createOrConstraint(ac);

        Permission p = createPermission(oc);
        Policy one = Policy.Builder.newInstance().permission(p).build();
        Policy two = Policy.Builder.newInstance().permission(p).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    @Test
    void testOrPolicyDifferentOrder() {

        AtomicConstraint ac1 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        AtomicConstraint ac2 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_1);

        OrConstraint oc1 = createOrConstraint(ac1, ac2);
        OrConstraint oc2 = createOrConstraint(ac2, ac1);

        Permission p1 = createPermission(oc1);
        Permission p2 = createPermission(oc2);

        Policy one = Policy.Builder.newInstance().permission(p1).build();
        Policy two = Policy.Builder.newInstance().permission(p2).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    @Test
    void testAndPolicyDifferentOrder() {

        AtomicConstraint ac1 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        AtomicConstraint ac2 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_1);

        AndConstraint oc1 = createAndConstraint(ac1, ac2);
        AndConstraint oc2 = createAndConstraint(ac2, ac1);

        Permission p1 = createPermission(oc1);
        Permission p2 = createPermission(oc2);

        Policy one = Policy.Builder.newInstance().permission(p1).build();
        Policy two = Policy.Builder.newInstance().permission(p2).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    @Test
    void testNestedAndOrPolicyDifferentOrder() {

        AtomicConstraint ac1 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        AtomicConstraint ac2 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_1);
        AtomicConstraint ac3 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_SILVER_PARTNERS);
        AtomicConstraint ac4 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_2);

        AndConstraint oc1 = createAndConstraint(ac1, ac2);
        AndConstraint oc2 = createAndConstraint(ac3, ac4);
        AndConstraint oc3 = createAndConstraint(ac2, ac1);
        AndConstraint oc4 = createAndConstraint(ac4, ac3);

        OrConstraint or1 = createOrConstraint(oc1, oc2);
        OrConstraint or2 = createOrConstraint(oc3, oc4);


        Permission p1 = createPermission(or1);
        Permission p2 = createPermission(or2);

        Policy one = Policy.Builder.newInstance().permission(p1).build();
        Policy two = Policy.Builder.newInstance().permission(p2).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    @Test
    void testMixedPolicyDifferentOrder() {

        AtomicConstraint ac1 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        AtomicConstraint ac2 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_1);
        AtomicConstraint ac3 = createAtomicConstraint(MEMBERSHIP_CREDENTIAL, Operator.EQ, "active");

        AndConstraint oc1 = createAndConstraint(ac1, ac2);
        AndConstraint oc2 = createAndConstraint(ac2, ac1);


        Permission p1 = createPermission(ac3, oc1);
        Permission p2 = createPermission(oc2, ac3);

        Policy one = Policy.Builder.newInstance().permission(p1).build();
        Policy two = Policy.Builder.newInstance().permission(p2).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    @Test
    void testWrappedSinglePolicy() {

        AtomicConstraint ac1 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        AtomicConstraint ac2 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_1);

        AndConstraint oc1 = createAndConstraint(ac1);
        OrConstraint oc2 = createOrConstraint(ac2);

        OrConstraint or1 = createOrConstraint(oc1, oc2);
        OrConstraint or2 = createOrConstraint(ac1, ac2);


        Permission p1 = createPermission(or1);
        Permission p2 = createPermission(or2);

        Policy one = Policy.Builder.newInstance().permission(p1).build();
        Policy two = Policy.Builder.newInstance().permission(p2).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    @Test
    void testTwoAndPolicyAtSameLevelCompareByHash() {

        AtomicConstraint ac1 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_GOLD_PARTNERS);
        AtomicConstraint ac2 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_1);
        AtomicConstraint ac3 = createAtomicConstraint(BUSINESS_PARTNER_GROUP, Operator.EQ, GROUP_SILVER_PARTNERS);
        AtomicConstraint ac4 = createAtomicConstraint(BUSINESS_PARTNER_NUMBER, Operator.EQ, BPN_2);

        AndConstraint oc1 = createAndConstraint(ac1, ac2);
        AndConstraint oc2 = createAndConstraint(ac3, ac4);

        OrConstraint or1 = createOrConstraint(oc1, oc2);
        OrConstraint or2 = createOrConstraint(oc2, oc1);


        Permission p1 = createPermission(or1);
        Permission p2 = createPermission(or2);

        Policy one = Policy.Builder.newInstance().permission(p1).build();
        Policy two = Policy.Builder.newInstance().permission(p2).build();


        assertTrue(policyEqualityV2.test(one, two));
    }

    private Permission createPermission(Constraint... constraints) {

        return Permission.Builder.newInstance()
                .action(Action.Builder.newInstance().type("USE").build())
                .constraints(List.of(constraints))
                .build();
    }

    private AtomicConstraint createAtomicConstraint(String leftExp, Operator op, String rightExp) {
        return AtomicConstraint.Builder.newInstance()
                .leftExpression(new LiteralExpression(leftExp))
                .operator(op)
                .rightExpression(new LiteralExpression(rightExp))
                .build();
    }

    private OrConstraint createOrConstraint(Constraint... constraints) {

        return OrConstraint.Builder.newInstance().constraints(List.of(constraints)).build();
    }

    private AndConstraint createAndConstraint(Constraint... constraints) {

        return AndConstraint.Builder.newInstance().constraints(List.of(constraints)).build();
    }
}
