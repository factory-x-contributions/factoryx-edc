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

package org.factoryx.edc.policy.fx.businesspartnerdid;

import org.eclipse.edc.iam.verifiablecredentials.spi.model.VerifiableCredential;
import org.eclipse.edc.participant.spi.ParticipantAgent;
import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.tractusx.edc.core.utils.credentials.CredentialTypePredicate;
import org.factoryx.edc.policy.fx.common.AbstractDynamicCredentialConstraintFunction;

import static org.factoryx.edc.edr.spi.CoreConstants.FX_CREDENTIAL_NS;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;


/**
 * This constraint function checks that a BusinessPartnerDidCredential is present in a list of {@link VerifiableCredential}
 * objects extracted from a {@link ParticipantAgent} which is expected to be present on the {@link ParticipantAgentPolicyContext}.
 */
public class BusinessPartnerDidConstraintFunction<C extends ParticipantAgentPolicyContext> extends AbstractDynamicCredentialConstraintFunction<C> {
    /**
     * key of the Business Partner Did credential constraint
     */
    public static final String BUSINESS_PARTNER_DID_LITERAL = "BusinessPartnerDid";

    @Override
    public boolean evaluate(Object leftOperand, Operator operator, Object rightOperand, Permission permission, C context) {

        if (!checkOperator(operator, context, EQUALITY_OPERATORS)) {
            return false;
        }

        // we support only string.
        if (!(rightOperand instanceof String)) {
            context.reportProblem("The right-operand must be of type String but was '%s'.".formatted(rightOperand.getClass()));
            return false;
        }

        if (!(rightOperand.toString().toLowerCase()).startsWith("did:web")) {
            context.reportProblem("The right-operand must start with did:web, but was '%s'".formatted(rightOperand));
            return false;
        }

        var participantAgent = extractParticipantAgent(context);
        if (participantAgent.failed()) {
            context.reportProblem(participantAgent.getFailureDetail());
            return false;
        }

        var credentialResult = getCredentialList(participantAgent.getContent());
        if (credentialResult.failed()) {
            context.reportProblem(credentialResult.getFailureDetail());
            return false;
        }
        return credentialResult.getContent()
                .stream()
                .anyMatch(new CredentialTypePredicate(FX_CREDENTIAL_NS, BUSINESS_PARTNER_DID_LITERAL + CREDENTIAL_LITERAL));
    }

    @Override
    public boolean canHandle(Object leftOperand) {
        return leftOperand instanceof String && (FX_POLICY_NS + BUSINESS_PARTNER_DID_LITERAL).equalsIgnoreCase(leftOperand.toString());
    }
}
