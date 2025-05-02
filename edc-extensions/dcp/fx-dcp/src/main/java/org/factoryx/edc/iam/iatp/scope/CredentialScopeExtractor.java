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
import org.eclipse.edc.connector.controlplane.contract.spi.types.negotiation.ContractRequestMessage;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.protocol.TransferRequestMessage;
import org.eclipse.edc.iam.identitytrust.spi.scope.ScopeExtractor;
import org.eclipse.edc.policy.context.request.spi.RequestPolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.spi.iam.RequestContext;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;
import org.factoryx.edc.edr.spi.CoreConstants;

import java.util.Optional;
import java.util.Set;

import static java.util.Collections.emptySet;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;
import static org.factoryx.edc.iam.iatp.constant.FxDcpConstants.CREDENTIAL_TYPE_NAMESPACE;


/**
 * Extract credentials from the policy constraints
 * <p>
 * Extract and map from the Credential DSL the required credential type
 * The left operand should be bound to the namespace {@link CoreConstants#FX_CREDENTIAL_NS}
 */
public class CredentialScopeExtractor implements ScopeExtractor {
    /**
     * Prefix to extract certification type
     */
    public static final String CERTIFICATION_TYPE_PREFIX = "CertificationType";
    public static final String SCOPE_FORMAT = "%s:%s:read";
    /**
     * Scopes ending with Credential
     */
    public static final String CREDENTIAL_FORMAT = "%sCredential";

    private static final Set<Class<? extends RemoteMessage>> SUPPORTED_MESSAGES = Set.of(CatalogRequestMessage.class, ContractRequestMessage.class, TransferRequestMessage.class);

    private final Monitor monitor;

    public CredentialScopeExtractor(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public Set<String> extractScopes(Object leftValue, Operator operator, Object rightValue, RequestPolicyContext context) {
        var requestContext = context.requestContext();

        if (requestContext != null) {

            if (leftValue instanceof String leftOperand && leftOperand.startsWith(FX_POLICY_NS) && isMessageSupported(requestContext)) {
                leftOperand = leftOperand.replace(FX_POLICY_NS, "");
                var credentialType = extractCredentialType(leftOperand, rightValue);
                return Set.of(SCOPE_FORMAT.formatted(CREDENTIAL_TYPE_NAMESPACE, CREDENTIAL_FORMAT.formatted(capitalize(credentialType))));
            }

        } else {
            monitor.warning("RequestContext not found in the PolicyContext: scope cannot be extracted from the policy. Defaulting to empty scopes");
        }

        return emptySet();
    }

    private boolean isMessageSupported(RequestContext ctx) {
        return Optional.ofNullable(ctx.getMessage())
                .map(RemoteMessage::getClass)
                .map(SUPPORTED_MESSAGES::contains)
                .orElse(false);
    }

    /**
     * Possible values for credential:
     * <ul>
     *     <li>CertificationType -> return "CertificationType"</li>
     *     <li>Membership -> return "Membership"</li>
     * </ul>
     */
    private String extractCredentialType(String leftOperand, Object rightValue) {
        var ix = leftOperand.indexOf(".");
        leftOperand = ix > 0 ? leftOperand.substring(0, ix) : leftOperand;
        return leftOperand;
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}
