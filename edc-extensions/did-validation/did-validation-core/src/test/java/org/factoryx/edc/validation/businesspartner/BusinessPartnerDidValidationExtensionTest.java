/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2025 SAP SE
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
 ********************************************************************************/

package org.factoryx.edc.validation.businesspartner;

import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.factoryx.edc.validation.businesspartner.BusinessPartnerDidValidationExtension.BUSINESS_PARTNER_CONSTRAINT_KEY;
import static org.factoryx.edc.validation.businesspartner.BusinessPartnerDidValidationExtension.FX_BUSINESS_PARTNER_CONSTRAINT_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(DependencyInjectionExtension.class)
class BusinessPartnerDidValidationExtensionTest {

    private final PolicyEngine policyEngine = mock();
    private final RuleBindingRegistry ruleBindingRegistry = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(PolicyEngine.class, policyEngine);
        context.registerService(RuleBindingRegistry.class, ruleBindingRegistry);
    }

    @Test
    void testRegisterPermissionFunction(ServiceExtensionContext context, BusinessPartnerDidValidationExtension extension) {
        extension.initialize(context);

        verify(policyEngine, times(3))
                .registerFunction(
                        isA(Class.class),
                        eq(Permission.class),
                        eq(BUSINESS_PARTNER_CONSTRAINT_KEY),
                        any());
        verify(policyEngine, times(3))
                .registerFunction(
                        isA(Class.class),
                        eq(Permission.class),
                        eq(FX_BUSINESS_PARTNER_CONSTRAINT_KEY),
                        any());

        verify(ruleBindingRegistry, times(3))
                .bind(eq(BUSINESS_PARTNER_CONSTRAINT_KEY),
                        anyString());

        verify(ruleBindingRegistry, times(3))
                .bind(eq(FX_BUSINESS_PARTNER_CONSTRAINT_KEY),
                        anyString());
    }

}
