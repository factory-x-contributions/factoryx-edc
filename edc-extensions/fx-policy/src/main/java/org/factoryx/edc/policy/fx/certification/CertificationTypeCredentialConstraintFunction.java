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

package org.factoryx.edc.policy.fx.certification;

import org.eclipse.edc.iam.verifiablecredentials.spi.model.VerifiableCredential;
import org.eclipse.edc.participant.spi.ParticipantAgent;
import org.eclipse.edc.participant.spi.ParticipantAgentPolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.factoryx.edc.edr.spi.CoreConstants;
import org.factoryx.edc.policy.fx.common.AbstractDynamicCredentialConstraintFunction;

import java.util.Arrays;

import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;

/**
 * Enforces a Certification Agreement constraint.
 * <p>
 * This function can parse "MyCertificate" constraints.
 * <pre>
 *     MyCertificate EQ subtype[:version]
 * </pre>
 * Either notation is converted into a set of predicates which are applied to the list of certificates. If the resulting filtered list is empty, the
 * policy is considered <strong>not fulfilled</strong>.
 */
public class CertificationTypeCredentialConstraintFunction<C extends ParticipantAgentPolicyContext> extends AbstractDynamicCredentialConstraintFunction<C> {
    /**
     * key of the certification type constraint
     */
    public static final String CERTIFICATION_LITERAL = "CertificationType";

    /**
     * Evaluates the constraint's left-operand and right-operand against a list of {@link CertificationType} objects.
     *
     * @param leftValue  the left-side expression for the constraint. Must be either {@code https://w3id.org/factoryx/policy/}.
     * @param operator   the operation Must be {@link Operator#EQ} or {@link Operator#NEQ}
     * @param rightValue the right-side expression for the constraint. Must be a string that is of type {@link CertificationType}.
     * @param rule       the rule associated with the constraint. Ignored by this function.
     * @param context    the policy context. Must contain the {@link ParticipantAgent}, which in turn must contain a list of {@link VerifiableCredential} stored
     *                   in its claims using the {@code "vc"} key.
     * @return true if at least one credential satisfied the requirement imposed by the constraint.
     */
    @Override
    public boolean evaluate(Object leftValue, Operator operator, Object rightValue, Permission rule, C context) {

        if (!checkOperator(operator, context, EQUALITY_OPERATORS)) {
            return false;
        }

        // we support only string.
        if (!(rightValue instanceof String)) {
            context.reportProblem("The right-operand must be of type String but was '%s'.".formatted(rightValue.getClass()));
            return false;
        }

        var participantAgent = extractParticipantAgent(context);
        if (participantAgent.failed()) {
            context.reportProblem(participantAgent.getFailureDetail());
            return false;
        }

        var rightOperand = rightValue.toString();

        if (!Arrays.stream(CertificationType.values()).map(Enum::name).toList().contains(rightOperand)) { // couldn't extract credential list from agent
            context.reportProblem("Certification type '%s' is not of a defined type.".formatted(rightOperand));
            return false;
        }

        return true;
    }

    /**
     * Returns {@code true} if the left-operand starts with {@link CoreConstants#FX_POLICY_NS}, {@code false} otherwise.
     */
    @Override
    public boolean canHandle(Object leftValue) {
        return leftValue instanceof String && leftValue.toString().startsWith(FX_POLICY_NS + CERTIFICATION_LITERAL);
    }


}