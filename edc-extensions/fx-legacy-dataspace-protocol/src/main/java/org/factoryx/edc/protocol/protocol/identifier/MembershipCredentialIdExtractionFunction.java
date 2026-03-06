/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2025 Cofinity-X GmbH
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

package org.factoryx.edc.protocol.protocol.identifier;

import org.eclipse.edc.iam.verifiablecredentials.spi.model.VerifiableCredential;
import org.eclipse.edc.protocol.spi.ParticipantIdExtractionFunction;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.ClaimToken;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.tractusx.edc.core.utils.credentials.CredentialTypePredicate;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static org.factoryx.edc.edr.spi.CoreConstants.FX_CREDENTIAL_NS;

/**
 * Base class for participant id extraction from a {@link ClaimToken} based on the MembershipCredential.
 */
public abstract class MembershipCredentialIdExtractionFunction implements ParticipantIdExtractionFunction {

    private static final String VC_CLAIM = "vc";
    /**
     * key of the membership credential constraint
     *
     * @deprecated Use {@value FX_IDENTITY_CREDENTIAL} instead.
     */
    @Deprecated(since = "0.1.4", forRemoval = true)
    public static final String IDENTITY_CREDENTIAL = "MembershipCredential";
    public static final String FX_IDENTITY_CREDENTIAL = "FxMembershipCredential";

    private final Predicate<VerifiableCredential> typePredicate = new CredentialTypePredicate(FX_CREDENTIAL_NS, IDENTITY_CREDENTIAL).or(new CredentialTypePredicate(FX_CREDENTIAL_NS, FX_IDENTITY_CREDENTIAL));

    @Override
    public String apply(ClaimToken claimToken) {
        var credentials = getCredentialList(claimToken)
                .orElseThrow(failure -> new EdcException("Failed to fetch credentials from the claim token: %s".formatted(failure.getFailureDetail())));

        return credentials.stream()
                .filter(typePredicate)
                .findFirst()
                .flatMap(this::getIdentifier)
                .orElseThrow(() -> new EdcException("Required credential type '%s' or '%s' not present in ClaimToken, cannot extract property '%s'".formatted(IDENTITY_CREDENTIAL, FX_IDENTITY_CREDENTIAL, identityProperty())));
    }

    @SuppressWarnings("unchecked")
    private Result<List<VerifiableCredential>> getCredentialList(ClaimToken claimToken) {
        var vcListClaim = claimToken.getClaims().get(VC_CLAIM);

        if (vcListClaim == null) {
            return Result.failure("ClaimToken did not contain a '%s' claim.".formatted(VC_CLAIM));
        }
        if (!(vcListClaim instanceof List)) {
            return Result.failure("ClaimToken contains a '%s' claim, but the type is incorrect. Expected %s, got %s.".formatted(VC_CLAIM, List.class.getName(), vcListClaim.getClass().getName()));
        }
        var vcList = (List<VerifiableCredential>) vcListClaim;
        if (vcList.isEmpty()) {
            return Result.failure("ClaimToken contains a '%s' claim but it did not contain any VerifiableCredentials.".formatted(VC_CLAIM));
        }
        return Result.success(vcList);
    }

    abstract String identityProperty();

    protected abstract Optional<String> getIdentifier(VerifiableCredential vc);

}
