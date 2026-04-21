/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.iam.iatp;

import org.eclipse.edc.connector.controlplane.catalog.spi.CatalogRequestMessage;
import org.eclipse.edc.iam.decentralizedclaims.spi.scope.ScopeExtractor;
import org.eclipse.edc.iam.decentralizedclaims.spi.scope.ScopeExtractorRegistry;
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.policy.context.request.spi.RequestPolicyContext;
import org.eclipse.edc.spi.iam.RequestContext;
import org.eclipse.edc.spi.iam.RequestScope;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.factoryx.edc.edr.spi.CoreConstants;
import org.factoryx.edc.iam.iatp.scope.CredentialScopeExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

import static org.factoryx.edc.iam.iatp.IatpScopeExtractorExtension.TX_IATP_SCOPE_ALIAS;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(DependencyInjectionExtension.class)
public class IatpScopeExtractorExtensionTest {

    private final ScopeExtractorRegistry extractorRegistry = mock();
    private final Monitor monitor = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(ScopeExtractorRegistry.class, extractorRegistry);
        context.registerService(Monitor.class, monitor);
    }

    @Test
    void initialize(ServiceExtensionContext context, IatpScopeExtractorExtension extension) {
        extension.initialize(context);

        verify(extractorRegistry).registerScopeExtractor(isA(CredentialScopeExtractor.class));
    }

    @Test
    void initialize_withConfiguredScopeAlias(ServiceExtensionContext context, IatpScopeExtractorExtension extension) {
        when(context.getSetting(eq(TX_IATP_SCOPE_ALIAS), eq("org.factoryx.vc.type"))).thenReturn("org.eclipse.tractusx.vc.type");

        extension.initialize(context);

        verify(extractorRegistry)
                .registerScopeExtractor(argThat(extractor -> extractMembershipScope(extractor)
                        .contains("org.eclipse.tractusx.vc.type:MembershipCredential:read")));
    }

    private Set<String> extractMembershipScope(ScopeExtractor extractor) {
        var requestContext = RequestContext.Builder.newInstance()
                .message(CatalogRequestMessage.Builder.newInstance().build())
                .direction(RequestContext.Direction.Egress)
                .build();

        return extractor.extractScopes(CoreConstants.FX_POLICY_NS + "Membership", null, null,
                new TestRequestPolicyContext(requestContext, null));
    }

    private static class TestRequestPolicyContext extends RequestPolicyContext {

        protected TestRequestPolicyContext(RequestContext requestContext, RequestScope.Builder requestScopeBuilder) {
            super(requestContext, requestScopeBuilder);
        }

        @Override
        public String scope() {
            return "request.any";
        }
    }
}
