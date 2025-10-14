/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2025 Fraunhofer-Gesellschaft zur Förderung der angewandten Forschung e.V.
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

package org.factoryx.edc.e2e.tests.catalog;

import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.junit.annotations.EndToEndTest;
import org.eclipse.edc.junit.extensions.RuntimeExtension;
import org.factoryx.edc.e2e.tests.participant.TransferParticipant;
import org.factoryx.edc.e2e.tests.runtimes.PostgresExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.connector.controlplane.test.system.utils.PolicyFixtures.noConstraintPolicy;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.CONSUMER_BPN;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.CONSUMER_DID;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.CONSUMER_NAME;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.DSP_08;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.PROVIDER_BPN;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.PROVIDER_DID;
import static org.factoryx.edc.e2e.tests.TestRuntimeConfiguration.PROVIDER_NAME;
import static org.factoryx.edc.e2e.tests.runtimes.Runtimes.pgRuntime;

@EndToEndTest
public class CatalogTest {

    private static final TransferParticipant CONSUMER = TransferParticipant.Builder.newInstance()
            .name(CONSUMER_NAME)
            .id(CONSUMER_DID)
            .bpn(CONSUMER_BPN)
            .protocol(DSP_08)
            .build();


    private static final TransferParticipant PROVIDER = TransferParticipant.Builder.newInstance()
            .name(PROVIDER_NAME)
            .id(PROVIDER_DID)
            .bpn(PROVIDER_BPN)
            .protocol(DSP_08)
            .build();

    @RegisterExtension
    @Order(0)
    private static final PostgresExtension POSTGRES = new PostgresExtension(CONSUMER.getName(), PROVIDER.getName());

    @RegisterExtension
    private static final RuntimeExtension CONSUMER_RUNTIME = pgRuntime(CONSUMER, POSTGRES);

    @RegisterExtension
    private static final RuntimeExtension PROVIDER_RUNTIME = pgRuntime(PROVIDER, POSTGRES);

    @BeforeEach
    void setup() {
        CONSUMER.setJsonLd(CONSUMER_RUNTIME.getService(JsonLd.class));
    }

    @Test
    @DisplayName("Consumer gets catalog from the provider. No constraints.")
    void requestCatalog_fulfillsPolicy_shouldReturnOffer() {
        // arrange
        PROVIDER.createAsset("test-asset");
        var ap = PROVIDER.createPolicyDefinition(noConstraintPolicy());
        var cp = PROVIDER.createPolicyDefinition(noConstraintPolicy());
        PROVIDER.createContractDefinition("test-asset", "test-def", ap, cp);

        // act
        var catalog = CONSUMER.getCatalogDatasets(PROVIDER);

        // assert
        assertThat(catalog).isNotEmpty()
                .hasSize(1)
                .allSatisfy(co -> {
                    assertThat(co.asJsonObject().getString(ID)).isEqualTo("test-asset");
                });

    }
}
