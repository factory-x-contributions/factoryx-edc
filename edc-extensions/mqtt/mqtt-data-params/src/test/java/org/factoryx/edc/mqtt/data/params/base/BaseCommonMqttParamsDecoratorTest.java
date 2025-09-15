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

package org.factoryx.edc.mqtt.data.params.base;

import org.eclipse.edc.spi.EdcException;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.BASE_URL;

class BaseCommonMqttParamsDecoratorTest {

    private static final String MQTT_BROKER_URL = "tcp://mqtt-broker:1883";
    private static final String MQTT_USER = "mqtt-user";
    private static final String MQTT_PASSWORD_ALIAS = "mqtt-password-alias";

    private BaseCommonMqttParamsDecorator decorator;

    @BeforeEach
    void setup() {
        decorator = new BaseCommonMqttParamsDecorator();
    }

    @Test
    void testBaseUrl() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .baseUrl(MQTT_BROKER_URL)
                .username(MQTT_USER)
                .passwordAlias(MQTT_PASSWORD_ALIAS)
                .build();

        var params = decorator.decorate(dataAddress, MqttParams.Builder.newInstance()).build();

        assertThat(params.getProperties())
                .containsEntry(BASE_URL, MQTT_BROKER_URL);
    }

    @Test
    void testBaseUrlMissing() {

        var dataAddress = MqttDataAddress.Builder.newInstance()
                .username(MQTT_USER)
                .passwordAlias(MQTT_PASSWORD_ALIAS)
                .build();

        assertThatThrownBy(() -> decorator.decorate(dataAddress, MqttParams.Builder.newInstance()))
                .isInstanceOf(EdcException.class)
                .hasMessageContaining("baseUrl is missing from the mqtt data address");
    }
}
