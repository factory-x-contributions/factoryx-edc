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

package org.factoryx.edc.iam.iatp.scope;

import org.eclipse.edc.connector.controlplane.catalog.spi.CatalogRequestMessage;
import org.eclipse.edc.connector.controlplane.contract.spi.types.agreement.ContractAgreementMessage;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractNegotiationTerminationMessage;
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractRequestMessage;
import org.eclipse.edc.connector.controlplane.contract.spi.types.offer.ContractOffer;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.protocol.TransferRequestMessage;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.protocol.TransferTerminationMessage;
import org.eclipse.edc.policy.context.request.spi.RequestPolicyContext;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.iam.RequestContext;
import org.eclipse.edc.spi.iam.RequestScope;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;
import org.factoryx.edc.edr.spi.CoreConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.factoryx.edc.iam.iatp.constant.FxDcpConstants.CREDENTIAL_TYPE_NAMESPACE;
import static org.factoryx.edc.iam.iatp.scope.CredentialScopeExtractor.CERTIFICATION_TYPE_PREFIX;
import static org.mockito.Mockito.mock;

public class CredentialScopeExtractorTest {

    private final Monitor monitor = mock();
    private CredentialScopeExtractor extractor;

    @BeforeEach
    void setup() {
        extractor = new CredentialScopeExtractor(monitor);
    }

    @DisplayName("Scope extractor with supported messages")
    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(SupportedMessages.class)
    void verify_extractScopes(RemoteMessage message) {
        var requestContext = RequestContext.Builder.newInstance().message(message).direction(RequestContext.Direction.Egress).build();
        var ctx = new TestRequestPolicyContext(requestContext, null);

        var scopes = extractor.extractScopes(CoreConstants.FX_POLICY_NS + CERTIFICATION_TYPE_PREFIX + ".pfc", null, null, ctx);
        System.out.println(scopes);
        assertThat(scopes).contains(CREDENTIAL_TYPE_NAMESPACE + ":CertificationTypeCredential:read");
    }

    @DisplayName("Scope extractor with not supported messages")
    @ParameterizedTest(name = "{1}")
    @ArgumentsSource(NotSupportedMessages.class)
    void verify_extractScopes_isEmpty_whenNotSupportedMessages(RemoteMessage message) {
        var requestContext = RequestContext.Builder.newInstance().message(message).direction(RequestContext.Direction.Egress).build();
        var ctx = new TestRequestPolicyContext(requestContext, null);

        var scopes = extractor.extractScopes(CoreConstants.FX_POLICY_NS + CERTIFICATION_TYPE_PREFIX + ".pfc", null, null, ctx);

        assertThat(scopes).isEmpty();
    }

    @Test
    void verify_extractScope_Empty() {
        var ctx = new TestRequestPolicyContext(null, null);

        var scopes = extractor.extractScopes("wrong", null, null, ctx);

        assertThat(scopes).isEmpty();
    }

    private static class SupportedMessages implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            var offer = ContractOffer.Builder.newInstance().id("id").assetId("assetId").policy(Policy.Builder.newInstance().build()).build();
            return Stream.of(
                    Arguments.of(CatalogRequestMessage.Builder.newInstance().build()),
                    Arguments.of(ContractRequestMessage.Builder.newInstance().contractOffer(offer).callbackAddress("cb").build()),
                    Arguments.of(TransferRequestMessage.Builder.newInstance().callbackAddress("cb").build())
            );
        }
    }

    private static class NotSupportedMessages implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(ContractNegotiationTerminationMessage.Builder.newInstance().build()),
                    Arguments.of(ContractAgreementMessage.Builder.newInstance().counterPartyAddress("cb").contractAgreement(mock()).build()),
                    Arguments.of(TransferTerminationMessage.Builder.newInstance().counterPartyAddress("pd").build())
            );
        }
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
