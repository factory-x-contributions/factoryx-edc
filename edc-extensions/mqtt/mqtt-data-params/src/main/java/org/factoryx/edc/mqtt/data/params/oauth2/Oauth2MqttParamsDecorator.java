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
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.iam.TokenRepresentation;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsDecorator;

public class Oauth2MqttParamsDecorator implements MqttParamsDecorator {

    private final MqttOauth2CredentialsRequestFactory requestFactory;
    private final Oauth2Client oauth2Client;

    public Oauth2MqttParamsDecorator(MqttOauth2CredentialsRequestFactory requestFactory, Oauth2Client oauth2Client) {
        this.requestFactory = requestFactory;
        this.oauth2Client = oauth2Client;
    }

    @Override
    public MqttParams.Builder decorate(MqttDataAddress address, MqttParams.Builder params) {

        if (address.hasOauth2()) {
            return requestFactory.create(address)
                    .compose(oauth2Client::requestToken)
                    .map(tokenRepresentation -> decorateParamFromToken(tokenRepresentation, params))
                    .orElseThrow(failure -> new EdcException("Cannot Decorate Mqtt through OAuth2: " + failure.getFailureDetail()));
        }
        return params;
    }

    protected MqttParams.Builder decorateParamFromToken(TokenRepresentation tokenRepresentation, MqttParams.Builder params) {
        return params.authorization(tokenRepresentation.getToken())
                .expiresIn(Long.toString(tokenRepresentation.getExpiresIn()));
    }
}
