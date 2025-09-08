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

package org.factoryx.edc.mqtt.data.params.provider;

import org.eclipse.edc.spi.EdcException;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.basic.BasicAuthMqttParamsDecorator;
import org.factoryx.edc.mqtt.data.params.spi.MqttConstants;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MqttParamsProviderImplTest {

    private static final String MQTT_BROKER_URL = "tcp://mqtt-broker:1883";
    private static final String MQTT_USER = "mqtt-user";
    private static final String MQTT_PASSWORD = "mqtt-password";
    private static final String MQTT_PASSWORD_ALIAS = "mqtt-password-alias";

    private MqttParamsProvider provider;
    private final BasicAuthMqttParamsDecorator basicAuthDecorator = mock();

    @BeforeEach
    void setup() {
        provider = new MqttParamsProviderImpl();
        provider.registerDecorator(basicAuthDecorator);
    }

    @Test
    void testBasicAuth() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .username(MQTT_USER)
                .passwordAlias(MQTT_PASSWORD_ALIAS)
                .build();

        when(basicAuthDecorator.decorate(eq(dataAddress), any(MqttParams.Builder.class)))
                .thenAnswer(invocation -> {
                    MqttParams.Builder arg = invocation.getArgument(1);
                    arg.username(MQTT_USER).password(MQTT_PASSWORD);
                    return arg;
                });

        var params = provider.provideParams(dataAddress);

        assertThat(params.getProperties())
                .containsEntry(MqttConstants.USERNAME, MQTT_USER)
                .containsEntry(MqttConstants.PASSWORD, MQTT_PASSWORD);

        verify(basicAuthDecorator).decorate(eq(dataAddress), any(MqttParams.Builder.class));
    }

    @Test
    void testNoBasicAuth() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .build();

        when(basicAuthDecorator.decorate(eq(dataAddress), any(MqttParams.Builder.class)))
                .thenAnswer(invocation -> invocation.getArgument(1));

        var params = provider.provideParams(dataAddress);

        assertThat(params.getProperties())
                .doesNotContainKey(MqttConstants.USERNAME)
                .doesNotContainKey(MqttConstants.PASSWORD);

        verify(basicAuthDecorator).decorate(eq(dataAddress), any(MqttParams.Builder.class));
    }

    @Test
    void testBasicAuthPasswordMissingFromVault() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .username(MQTT_USER)
                .passwordAlias(MQTT_PASSWORD_ALIAS)
                .build();

        when(basicAuthDecorator.decorate(eq(dataAddress), any(MqttParams.Builder.class)))
                .thenThrow(new EdcException("No secret found in vault with name %s".formatted(MQTT_PASSWORD_ALIAS)));

        assertThatThrownBy(() -> provider.provideParams(dataAddress))
                .isInstanceOf(EdcException.class)
                .hasMessageContaining("No secret found in vault with name %s", MQTT_PASSWORD_ALIAS);
        verify(basicAuthDecorator).decorate(eq(dataAddress), any(MqttParams.Builder.class));
    }
}
