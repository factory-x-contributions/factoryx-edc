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

package org.factoryx.edc.mqtt.data.endpoint.parser;

import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.factoryx.edc.mqtt.data.endpoint.parser.spi.EndpointTypeParser;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType.AuthType;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType.DestinationType;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType.Protocol;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;

class EndpointTypeParserImplTest {

    private final EndpointTypeParser parser = new EndpointTypeParserImpl(new ConsoleMonitor());

    @Test
    void shouldExtractMqttTcpBasic() {
        var result = parser.parse("MQTT-TCP-BASIC");

        assertThat(result).isSucceeded().satisfies(type -> {
            assertThat(type.destinationType()).isEqualTo(DestinationType.MQTT);
            assertThat(type.protocol()).isEqualTo(Protocol.TCP);
            assertThat(type.authType()).isEqualTo(AuthType.BASIC);
        });
    }

    @Test
    void shouldExtractMqttTcpOauth2() {
        var result = parser.parse("mqtt-tcp-oauth2");

        assertThat(result).isSucceeded().satisfies(type -> {
            assertThat(type.destinationType()).isEqualTo(DestinationType.MQTT);
            assertThat(type.protocol()).isEqualTo(Protocol.TCP);
            assertThat(type.authType()).isEqualTo(AuthType.OAUTH2);
        });
    }

    @Test
    void shouldExtractMqttWssBasic() {
        var result = parser.parse("MQTT-WSS-BASIC");

        assertThat(result).isSucceeded().satisfies(type -> {
            assertThat(type.destinationType()).isEqualTo(DestinationType.MQTT);
            assertThat(type.protocol()).isEqualTo(Protocol.WSS);
            assertThat(type.authType()).isEqualTo(AuthType.BASIC);
        });
    }

    @Test
    void shouldExtractMqttWssOauth2() {
        var result = parser.parse("mqtt-wss-oauth2");

        assertThat(result).isSucceeded().satisfies(type -> {
            assertThat(type.destinationType()).isEqualTo(DestinationType.MQTT);
            assertThat(type.protocol()).isEqualTo(Protocol.WSS);
            assertThat(type.authType()).isEqualTo(AuthType.OAUTH2);
        });
    }

    @Test
    void shouldFailAuthTypeMissing() {
        var result = parser.parse("mqtt-TCP");

        assertThat(result).isFailed();
    }

    @Test
    void shouldFailInvalidDestinationType() {
        var result = parser.parse("KAFKA-TCP-BASIC");

        assertThat(result).isFailed();
    }

    @Test
    void shouldFailInvalidTokens() {
        var result = parser.parse("mqtt-http-oauth");

        assertThat(result).isFailed();
    }
}
