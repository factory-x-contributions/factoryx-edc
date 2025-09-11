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

import org.eclipse.edc.iam.oauth2.spi.client.Oauth2Client;
import org.eclipse.edc.iam.oauth2.spi.client.Oauth2CredentialsRequest;
import org.eclipse.edc.iam.oauth2.spi.client.SharedSecretOauth2CredentialsRequest;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.eclipse.edc.spi.result.Result;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.spi.MqttConstants;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.factoryx.edc.mqtt.data.params.oauth2.MqttOauth2CredentialsRequestFactory.GRANT_CLIENT_CREDENTIALS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class Oauth2MqttParamsDecoratorTest {

    private static final String OAUTH2_TOKEN_URL = "http://keycloak:8080/realms/demo/token";
    private static final String OAUTH2_CLIENT_ID = "mqtt-client-id";
    private static final String OAUTH2_CLIENT_SECRET_ALIAS = "mqtt-password-alias";

    private static final String AUTHORIZATION_TOKEN = "eyJraWQiOiJwdWJsaWMta2V5LWFsaWFzIiwiYWxnIjoiUlMyNTYifQ";

    private final MqttOauth2CredentialsRequestFactory requestFactory = mock();
    private final Oauth2Client oauth2Client = mock();
    private Oauth2MqttParamsDecorator decorator;

    @BeforeEach
    void setup() {
        decorator = new Oauth2MqttParamsDecorator(requestFactory, oauth2Client);
    }

    @Test
    void testOauth2() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .oauth2TokenUrl(OAUTH2_TOKEN_URL)
                .oauth2ClientId(OAUTH2_CLIENT_ID)
                .oauth2ClientSecretAlias(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        Oauth2CredentialsRequest credentialsRequest = SharedSecretOauth2CredentialsRequest.Builder.newInstance()
                .url(OAUTH2_TOKEN_URL)
                .grantType(GRANT_CLIENT_CREDENTIALS)
                .clientId(OAUTH2_CLIENT_ID)
                .clientSecret(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        when(requestFactory.create(dataAddress)).thenReturn(Result.success(credentialsRequest));
        when(oauth2Client.requestToken(credentialsRequest)).thenReturn(Result.success(TokenRepresentation.Builder.newInstance().token(AUTHORIZATION_TOKEN).expiresIn(300L).build()));

        var params = decorator.decorate(dataAddress, MqttParams.Builder.newInstance()).build();

        assertThat(params.getProperties())
                .containsEntry(MqttConstants.AUTHORIZATION, AUTHORIZATION_TOKEN)
                .containsEntry(MqttConstants.EXPIRES_IN, "300");
    }

    @Test
    void testOauth2Missing() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .oauth2TokenUrl(OAUTH2_TOKEN_URL)
                .build();

        var params = decorator.decorate(dataAddress, MqttParams.Builder.newInstance()).build();

        assertThat(params.getProperties())
                .doesNotContainKey(MqttConstants.AUTHORIZATION)
                .doesNotContainKey(MqttConstants.EXPIRES_IN);

        verify(requestFactory, never()).create(any());
        verify(oauth2Client, never()).requestToken(any());
    }

    @Test
    void testOauthCredentialRequestFailure() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .oauth2TokenUrl(OAUTH2_TOKEN_URL)
                .oauth2ClientId(OAUTH2_CLIENT_ID)
                .oauth2ClientSecretAlias(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        when(requestFactory.create(dataAddress)).thenReturn(Result.failure("Request Failure"));

        assertThatThrownBy(() -> decorator.decorate(dataAddress, MqttParams.Builder.newInstance()))
                .isInstanceOf(EdcException.class)
                .hasMessageContaining("Cannot Decorate Mqtt through OAuth2: Request Failure");
        verify(oauth2Client, never()).requestToken(any());
    }

    @Test
    void testOauth2Oauth2TokenFailure() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .oauth2TokenUrl(OAUTH2_TOKEN_URL)
                .oauth2ClientId(OAUTH2_CLIENT_ID)
                .oauth2ClientSecretAlias(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        Oauth2CredentialsRequest credentialsRequest = SharedSecretOauth2CredentialsRequest.Builder.newInstance()
                .url(OAUTH2_TOKEN_URL)
                .grantType(GRANT_CLIENT_CREDENTIALS)
                .clientId(OAUTH2_CLIENT_ID)
                .clientSecret(OAUTH2_CLIENT_SECRET_ALIAS)
                .build();

        when(requestFactory.create(dataAddress)).thenReturn(Result.success(credentialsRequest));
        when(oauth2Client.requestToken(credentialsRequest)).thenReturn(Result.failure("Oauth2 Token Request Failure"));

        assertThatThrownBy(() -> decorator.decorate(dataAddress, MqttParams.Builder.newInstance()))
                .isInstanceOf(EdcException.class)
                .hasMessageContaining("Cannot Decorate Mqtt through OAuth2: Oauth2 Token Request Failure");
    }
}
