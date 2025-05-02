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

package org.factoryx.edc.policy.fx;


import org.eclipse.edc.iam.verifiablecredentials.spi.model.CredentialSubject;
import org.eclipse.edc.iam.verifiablecredentials.spi.model.Issuer;
import org.eclipse.edc.iam.verifiablecredentials.spi.model.VerifiableCredential;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.factoryx.edc.edr.spi.CoreConstants.FX_CREDENTIAL_NS;

public class CredentialFunctions {

    public static VerifiableCredential.Builder createCredential(String type, String version) {
        return VerifiableCredential.Builder.newInstance()
                .types(List.of(FX_CREDENTIAL_NS + "VerifiableCredential", FX_CREDENTIAL_NS + type))
                .id(UUID.randomUUID().toString())
                .issuer(new Issuer(UUID.randomUUID().toString(), Map.of("prop1", "val1")))
                .expirationDate(Instant.now().plus(365, ChronoUnit.DAYS))
                .issuanceDate(Instant.now())
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id("subject-id")
                        .claim(FX_CREDENTIAL_NS + "holderIdentifier", "did:web:holder")
                        .claim(FX_CREDENTIAL_NS + "contractVersion", version)
                        .claim(FX_CREDENTIAL_NS + "contractTemplate", "https://public.factory-x.org/contracts/pcf.v1.pdf")
                        .build());
    }

    public static VerifiableCredential.Builder createPlainFrameworkCredential(String type, String version) {
        return VerifiableCredential.Builder.newInstance()
                .types(List.of("VerifiableCredential", type))
                .id(UUID.randomUUID().toString())
                .issuer(new Issuer(UUID.randomUUID().toString(), Map.of("prop1", "val1")))
                .expirationDate(Instant.now().plus(365, ChronoUnit.DAYS))
                .issuanceDate(Instant.now())
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id("subject-id")
                        .claim("holderIdentifier", "did:web:holder")
                        .claim("contractVersion", version)
                        .claim("contractTemplate", "https://public.factory-x.org/contracts/pcf.v1.pdf")
                        .build());
    }

    public static VerifiableCredential.Builder createPcfCredential() {
        return createCredential("PcfCredential", "1.0.0");
    }

    public static VerifiableCredential.Builder createPlainPcfCredential() {
        return createPlainFrameworkCredential("PcfCredential", "1.0.0");
    }

    public static VerifiableCredential.Builder createPlainDismantlerCredential(Collection<String> brands, String... activityType) {
        var at = activityType.length == 1 ? activityType[0] : List.of(activityType);
        return VerifiableCredential.Builder.newInstance()
                .types(List.of("VerifiableCredential", "DismantlerCredential"))
                .id(UUID.randomUUID().toString())
                .issuer(new Issuer(UUID.randomUUID().toString(), Map.of("prop1", "val1")))
                .expirationDate(Instant.now().plus(365, ChronoUnit.DAYS))
                .issuanceDate(Instant.now())
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id("subject-id")
                        .claim("holderIdentifier", "did:web:holder")
                        .claim("allowedVehicleBrands", brands)
                        .claim("activityType", at)
                        .build());
    }

    public static VerifiableCredential.Builder createDismantlerCredential(Collection<String> brands, String... activityType) {
        var at = activityType.length == 1 ? activityType[0] : List.of(activityType);
        return VerifiableCredential.Builder.newInstance()
                .types(List.of("VerifiableCredential", FX_CREDENTIAL_NS + "DismantlerCredential"))
                .id(UUID.randomUUID().toString())
                .issuer(new Issuer(UUID.randomUUID().toString(), Map.of("prop1", "val1")))
                .expirationDate(Instant.now().plus(365, ChronoUnit.DAYS))
                .issuanceDate(Instant.now())
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id("subject-id")
                        .claim(FX_CREDENTIAL_NS + "holderIdentifier", "did:web:holder")
                        .claim(FX_CREDENTIAL_NS + "allowedVehicleBrands", brands)
                        .claim(FX_CREDENTIAL_NS + "activityType", at)
                        .build());
    }

    public static VerifiableCredential.Builder createMembershipCredential() {
        return VerifiableCredential.Builder.newInstance()
                .types(List.of(FX_CREDENTIAL_NS + "VerifiableCredential", FX_CREDENTIAL_NS + "MembershipCredential"))
                .id(UUID.randomUUID().toString())
                .issuer(new Issuer(UUID.randomUUID().toString(), Map.of("prop1", "val1")))
                .expirationDate(Instant.now().plus(365, ChronoUnit.DAYS))
                .issuanceDate(Instant.now())
                .credentialSubject(CredentialSubject.Builder.newInstance()
                        .id("subject-id")
                        .claim(FX_CREDENTIAL_NS + "holderIdentifier", "did:web:holder")
                        .build());
    }

}
