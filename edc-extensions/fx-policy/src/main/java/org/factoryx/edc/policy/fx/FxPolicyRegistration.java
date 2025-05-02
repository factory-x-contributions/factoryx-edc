/*
 * Copyright (c) 2024 T-Systems International GmbH
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

package org.factoryx.edc.policy.fx;

import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.factoryx.edc.policy.fx.certification.CertificationTypeCredentialConstraintFunction;
import org.factoryx.edc.policy.fx.membership.MembershipCredentialConstraintFunction;

import java.util.Set;
import java.util.stream.Stream;

import static org.eclipse.edc.policy.model.OdrlNamespace.ODRL_SCHEMA;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;
import static org.factoryx.edc.policy.fx.certification.CertificationTypeCredentialConstraintFunction.CERTIFICATION_LITERAL;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.CATALOG_REQUEST_SCOPE;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.CATALOG_SCOPE;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.CATALOG_SCOPE_CLASS;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.NEGOTIATION_REQUEST_SCOPE;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.NEGOTIATION_SCOPE;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.NEGOTIATION_SCOPE_CLASS;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.TRANSFER_PROCESS_REQUEST_SCOPE;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.TRANSFER_PROCESS_SCOPE;
import static org.factoryx.edc.policy.fx.common.PolicyScopes.TRANSFER_PROCESS_SCOPE_CLASS;
import static org.factoryx.edc.policy.fx.membership.MembershipCredentialConstraintFunction.MEMBERSHIP_LITERAL;

/**
 * Registers FX policy constraints to the EDC
 * This helps in adding custom constraints to EDC to enact Factory-X's own policies.
 */
public class FxPolicyRegistration {
    /**
     * List of functional scopes in EDC where the FX-policy constraints are validated
     */
    private static final Set<Class> FUNCTION_SCOPES_CLASSES = Set.of(CATALOG_SCOPE_CLASS, NEGOTIATION_SCOPE_CLASS, TRANSFER_PROCESS_SCOPE_CLASS);

    /**
     * List of Rules to which FX-policy constraints are bound by.
     */
    private static final Set<String> RULE_SCOPES = Set.of(CATALOG_REQUEST_SCOPE, NEGOTIATION_REQUEST_SCOPE, TRANSFER_PROCESS_REQUEST_SCOPE, CATALOG_SCOPE, NEGOTIATION_SCOPE, TRANSFER_PROCESS_SCOPE);

    /**
     * Registers FX-policies to EDC's policy engine.
     *
     * @param engine holds all policies that EDC follows.
     */
    public static void registerFunctions(PolicyEngine engine) {
        FUNCTION_SCOPES_CLASSES.forEach(scope -> {
            engine.registerFunction(scope, Permission.class, new MembershipCredentialConstraintFunction<>());
            engine.registerFunction(scope, Permission.class, new CertificationTypeCredentialConstraintFunction<>());
        });
    }

    /**
     * Registers bindings to the registry so that the FX-policy rules are triggered when a rule is reached
     *
     * @param registry is a registry of rules (functions) which are triggered when EDC reaches a rule.
     */
    public static void registerBindings(RuleBindingRegistry registry) {
        registry.dynamicBind(s -> {
            if (Stream.of(CERTIFICATION_LITERAL, MEMBERSHIP_LITERAL).anyMatch(postfix -> s.startsWith(FX_POLICY_NS + postfix))) {
                return RULE_SCOPES;
            }
            return Set.of();
        });

        registry.bind(ODRL_SCHEMA + "use", CATALOG_SCOPE);
        registry.bind(ODRL_SCHEMA + "use", NEGOTIATION_SCOPE);
        registry.bind(ODRL_SCHEMA + "use", TRANSFER_PROCESS_SCOPE);
    }
}
