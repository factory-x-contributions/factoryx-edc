/********************************************************************************
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

package org.factoryx.edc.mqtt.data.params.oauth2;

import org.assertj.core.api.Assertions;
import org.eclipse.edc.spi.security.Vault;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MqttOauth2CredentialsRequestFactoryTest {

    private static final String OAUTH2_TOKEN_URL = "http://keycloak:8080/realms/demo/token";
    private static final String OAUTH2_CLIENT_ID = "mqtt-client-id";
    private static final String OAUTH2_CLIENT_SECRET = "mqtt-client-secret";
    private static final String OAUTH2_CLIENT_SECRET_ALIAS = "mqtt-password-alias";

    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_SECRET_KEY = "client_secret";

    private final Vault vault = mock();
    private MqttOauth2CredentialsRequestFactory requestFactory;

    @BeforeEach
    void setup() {
        requestFactory = new MqttOauth2CredentialsRequestFactory(vault);
    }

    @Test
    void testOauthRequest() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .oauth2TokenUrl(OAUTH2_TOKEN_URL)
                .oauth2ClientId(OAUTH2_CLIENT_ID)
                .oauth2ClientSecretAlias(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        when(vault.resolveSecret(OAUTH2_CLIENT_SECRET_ALIAS)).thenReturn(OAUTH2_CLIENT_SECRET);

        var result = requestFactory.create(dataAddress);

        assertThat(result).isSucceeded().satisfies(request -> {
            Assertions.assertThat(request.getGrantType()).isEqualTo(MqttOauth2CredentialsRequestFactory.GRANT_CLIENT_CREDENTIALS);
            Assertions.assertThat(request.getUrl()).isEqualTo(OAUTH2_TOKEN_URL);
            Assertions.assertThat(request.getParams()).containsEntry(CLIENT_ID_KEY, OAUTH2_CLIENT_ID).containsEntry(CLIENT_SECRET_KEY, OAUTH2_CLIENT_SECRET);
        });
        verify(vault).resolveSecret(OAUTH2_CLIENT_SECRET_ALIAS);
    }

    @Test
    void testOauthRequestSecretMissingFromVault() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .oauth2TokenUrl(OAUTH2_TOKEN_URL)
                .oauth2ClientId(OAUTH2_CLIENT_ID)
                .oauth2ClientSecretAlias(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        when(vault.resolveSecret(OAUTH2_CLIENT_SECRET_ALIAS)).thenReturn(null);

        var result = requestFactory.create(dataAddress);

        assertThat(result).isFailed().satisfies(failure -> {
            Assertions.assertThat(failure.getMessages())
                    .contains("Cannot resolve client secret from the vault: " + OAUTH2_CLIENT_SECRET_ALIAS);
        });
        verify(vault).resolveSecret(OAUTH2_CLIENT_SECRET_ALIAS);
    }
}
