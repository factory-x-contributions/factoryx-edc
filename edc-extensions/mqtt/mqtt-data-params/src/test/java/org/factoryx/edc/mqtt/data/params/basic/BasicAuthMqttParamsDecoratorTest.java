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

package org.factoryx.edc.mqtt.data.params.basic;

import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.spi.MqttConstants;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BasicAuthMqttParamsDecoratorTest {

    private static final String MQTT_BROKER_URL = "tcp://mqtt-broker:1883";
    private static final String MQTT_USER = "mqtt-user";
    private static final String MQTT_PASSWORD = "mqtt-password";
    private static final String MQTT_PASSWORD_ALIAS = "mqtt-password-alias";

    private final Vault vault = mock();
    private BasicAuthMqttParamsDecorator decorator;

    @BeforeEach
    void setup() {
        decorator = new BasicAuthMqttParamsDecorator(vault);
    }

    @Test
    void testBasicAuth() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .username(MQTT_USER)
                .passwordAlias(MQTT_PASSWORD_ALIAS)
                .build();

        when(vault.resolveSecret(MQTT_PASSWORD_ALIAS)).thenReturn(MQTT_PASSWORD);

        var params = decorator.decorate(dataAddress, MqttParams.Builder.newInstance()).build();

        assertThat(params.getProperties())
                .containsEntry(MqttConstants.USERNAME, MQTT_USER)
                .containsEntry(MqttConstants.PASSWORD, MQTT_PASSWORD);
        verify(vault).resolveSecret(MQTT_PASSWORD_ALIAS);
    }

    @Test
    void testNoBasicAuth() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .build();

        var params = decorator.decorate(dataAddress, MqttParams.Builder.newInstance()).build();

        assertThat(params.getProperties())
                .doesNotContainKey(MqttConstants.USERNAME)
                .doesNotContainKey(MqttConstants.PASSWORD);
        verify(vault, never()).resolveSecret(MQTT_PASSWORD_ALIAS);
    }

    @Test
    void testBasicAuthPasswordMissingFromVault() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .username(MQTT_USER)
                .passwordAlias(MQTT_PASSWORD_ALIAS)
                .build();

        when(vault.resolveSecret(MQTT_PASSWORD_ALIAS)).thenReturn(null);

        assertThatThrownBy(() -> decorator.decorate(dataAddress, MqttParams.Builder.newInstance()))
                .isInstanceOf(EdcException.class)
                .hasMessageContaining("No secret found in vault with name %s", MQTT_PASSWORD_ALIAS);
        verify(vault).resolveSecret(MQTT_PASSWORD_ALIAS);
    }
}
