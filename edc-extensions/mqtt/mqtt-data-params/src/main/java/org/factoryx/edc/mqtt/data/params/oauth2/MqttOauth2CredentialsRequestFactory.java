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

import org.eclipse.edc.iam.oauth2.spi.client.Oauth2CredentialsRequest;
import org.eclipse.edc.iam.oauth2.spi.client.SharedSecretOauth2CredentialsRequest;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.security.Vault;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;

import java.util.Optional;

public class MqttOauth2CredentialsRequestFactory {

    public static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";

    private final Vault vault;

    public MqttOauth2CredentialsRequestFactory(Vault vault) {
        this.vault = vault;
    }

    public Result<Oauth2CredentialsRequest> create(MqttDataAddress dataAddress) {

        return Optional.of(dataAddress)
                .map(MqttDataAddress::getOauth2ClientSecretAlias)
                .map(vault::resolveSecret)
                .map(clientSecret -> buildRequest(clientSecret, dataAddress))
                .map(Result::success)
                .orElseGet(() -> Result.failure("Cannot resolve client secret from the vault: " + dataAddress.getOauth2ClientSecretAlias()));
    }

    private Oauth2CredentialsRequest buildRequest(String clientSecret, MqttDataAddress dataAddress) {
        return SharedSecretOauth2CredentialsRequest.Builder.newInstance()
                .url(dataAddress.getOauth2TokenUrl())
                .grantType(GRANT_CLIENT_CREDENTIALS)
                .clientId(dataAddress.getOauth2ClientId())
                .clientSecret(clientSecret)
                .build();
    }
}
