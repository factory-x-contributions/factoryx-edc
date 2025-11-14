/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.e2e.tests.participant;

import org.eclipse.edc.connector.controlplane.test.system.utils.LazySupplier;
import org.eclipse.edc.connector.controlplane.test.system.utils.Participant;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static java.time.Duration.ofSeconds;
import static org.eclipse.edc.util.io.Ports.getFreePort;


/**
 * Base class for doing E2E tests with participants.
 */
public abstract class TractusxParticipantBase extends IdentityParticipant {

    public static final String MANAGEMENT_API_KEY = "testkey";
    public static final Duration ASYNC_TIMEOUT = ofSeconds(120);
    private static final String CONSUMER_PROXY_API_KEY = "consumerProxyKey";
    private static final String API_KEY_HEADER_NAME = "x-api-key";
    protected final LazySupplier<URI> dataPlaneProxy = new LazySupplier<>(() -> URI.create("http://localhost:" + getFreePort()));
    private final LazySupplier<URI> dataPlanePublic = new LazySupplier<>(() -> URI.create("http://localhost:" + getFreePort() + "/public"));
    private final LazySupplier<URI> federatedCatalog = new LazySupplier<>(() -> URI.create("http://localhost:" + getFreePort() + "/api/catalog"));

    protected String did;

    public void createAsset(String id) {
        createAsset(id, new HashMap<>(), Map.of("type", "test-type"));
    }

    @NotNull
    public String getDid() {
        return did;
    }

    /**
     * Allows overriding the participant id, as for DSP 0.8 tests the provider's BPN has to be used.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    public Config getConfig() {
        var settings = new HashMap<String, String>() {
            {
                put("edc.runtime.id", name);
                put("edc.participant.id", getDid());
                put("web.http.port", String.valueOf(getFreePort()));
                put("web.http.path", "/api");
                put("web.http.protocol.port", String.valueOf(controlPlaneProtocol.get().getPort()));
                put("web.http.protocol.path", controlPlaneProtocol.get().getPath());
                put("web.http.management.port", String.valueOf(controlPlaneManagement.get().getPort()));
                put("web.http.management.path", controlPlaneManagement.get().getPath());
                put("web.http.management.auth.key", MANAGEMENT_API_KEY);
                put("web.http.control.port", String.valueOf(getFreePort()));
                put("web.http.control.path", "/control");
                put("web.http.version.port", String.valueOf(getFreePort()));
                put("web.http.version.path", "/version");
                put("web.http.catalog.port", String.valueOf(federatedCatalog.get().getPort()));
                put("web.http.catalog.path", federatedCatalog.get().getPath());
                put("web.http.catalog.auth.type", "tokenbased");
                put("web.http.catalog.auth.key", MANAGEMENT_API_KEY);
                put("edc.dsp.callback.address", controlPlaneProtocol.get().toString());
                put("web.http.public.path", dataPlanePublic.get().getPath());
                put("web.http.public.port", String.valueOf(dataPlanePublic.get().getPort()));
                put("edc.transfer.proxy.token.signer.privatekey.alias", getPrivateKeyAlias());
                put("edc.transfer.proxy.token.verifier.publickey.alias", getFullKeyId());
                put("edc.transfer.send.retry.limit", "1");
                put("edc.transfer.send.retry.base-delay.ms", "100");
                put("tx.edc.dpf.consumer.proxy.port", String.valueOf(dataPlaneProxy.get().getPort()));
                put("tx.edc.dpf.consumer.proxy.auth.apikey", CONSUMER_PROXY_API_KEY);
                put("tractusx.businesspartnervalidation.log.agreement.validation", "true");
                put("edc.agent.identity.key", "BusinessPartnerNumber");
                put("edc.iam.issuer.id", getDid());
                put("edc.iam.sts.oauth.token.url", "http://sts.example.com/token");
                put("edc.iam.sts.oauth.client.id", "test-clientid");
                put("edc.iam.sts.oauth.client.secret.alias", "test-clientid-alias");
                put("edc.dataplane.api.public.baseurl", "%s/v2/data".formatted(dataPlanePublic.get()));
                put("edc.catalog.cache.execution.delay.seconds", "2");
                put("edc.catalog.cache.execution.period.seconds", "2");
                put("edc.policy.validation.enabled", "true");
            }
        };

        return ConfigFactory.fromMap(settings);
    }

    @Override
    public String getFullKeyId() {
        return getDid() + "#" + getKeyId();
    }

    public static class Builder<P extends TractusxParticipantBase, B extends Builder<P, B>> extends Participant.Builder<P, B> {
        protected Builder(P participant) {
            super(participant);
        }

        public B protocolVersionPath(String path) {
            this.participant.protocolVersionPath = path;
            return self();
        }

        @Override
        public P build() {
            participant.did = participant.id;

            participant.enrichManagementRequest = requestSpecification -> requestSpecification.headers(Map.of(API_KEY_HEADER_NAME, MANAGEMENT_API_KEY));
            super.timeout(ASYNC_TIMEOUT);
            super.build();

            return participant;
        }
    }

}
