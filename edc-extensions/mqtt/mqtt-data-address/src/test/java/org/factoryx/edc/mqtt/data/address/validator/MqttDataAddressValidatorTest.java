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

package org.factoryx.edc.mqtt.data.address.validator;

import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.factoryx.edc.mqtt.data.endpoint.parser.spi.EndpointTypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.BASE_URL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_ADDRESS_TYPE;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_ENDPOINT_TYPE;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.OAUTH2_CLIENT_ID;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.OAUTH2_CLIENT_SECRET_ALIAS;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.OAUTH2_TOKEN_URL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.PASSWORD_ALIAS;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.USERNAME;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MqttDataAddressValidatorTest {

    private final EndpointTypeParser endpointTypeParser = mock();

    private MqttDataAddressValidator validator;

    @BeforeEach
    void setup() {
        validator = new MqttDataAddressValidator(endpointTypeParser);
    }

    @Test
    void testMqttAddressOauth2() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "wss://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .property(OAUTH2_TOKEN_URL, "http://keycloak:8080/realms/demo/token")
                .property(OAUTH2_CLIENT_ID, "mqtt-client-id")
                .property(OAUTH2_CLIENT_SECRET_ALIAS, "mqtt-client-secret-alias")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isSucceeded();
    }

    @Test
    void testMqttAddressBasic() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .property(USERNAME, "mqtt-user")
                .property(PASSWORD_ALIAS, "mqtt-password-alias")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isSucceeded();
    }

    @Test
    void testMqttAddressOauth2AndBasic() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .property(OAUTH2_TOKEN_URL, "http://keycloak:8080/realms/demo/token")
                .property(OAUTH2_CLIENT_ID, "mqtt-client-id")
                .property(OAUTH2_CLIENT_SECRET_ALIAS, "mqtt-client-secret-alias")
                .property(USERNAME, "mqtt-user")
                .property(PASSWORD_ALIAS, "mqtt-password-alias")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isFailed().satisfies(failure -> {
            assertTrue(failure.getMessages().stream().anyMatch(s -> s.contains("must contain exactly one set of auth 'oauth2' or 'basic'")));
        });
    }

    @Test
    void testMqttAddressNoAuth() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isFailed().satisfies(failure -> {
            assertTrue(failure.getMessages().stream().anyMatch(s -> s.contains("must contain exactly one set of auth 'oauth2' or 'basic'")));
        });
    }

    @Test
    void testMqttAddressInvalidBaseUrl() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isFailed().satisfies(failure -> {
            assertTrue(failure.getMessages().stream().anyMatch(s -> s.contains("must contain a valid url with property '%s'".formatted(BASE_URL))));
        });
    }

    @Test
    void testMqttAddressInvalidTokenUrl() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .property(OAUTH2_TOKEN_URL, "invalid-token-url")
                .property(OAUTH2_CLIENT_ID, "mqtt-client-id")
                .property(OAUTH2_CLIENT_SECRET_ALIAS, "mqtt-client-secret-alias")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isFailed().satisfies(failure -> {
            assertTrue(failure.getMessages().stream().anyMatch(s -> s.contains("must contain a valid url with property '%s'".formatted(OAUTH2_TOKEN_URL))));
        });
    }

    @Test
    void testMqttAddressOauth2WithPartialBasic() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .property(OAUTH2_TOKEN_URL, "http://keycloak:8080/realms/demo/token")
                .property(OAUTH2_CLIENT_ID, "mqtt-client-id")
                .property(OAUTH2_CLIENT_SECRET_ALIAS, "mqtt-client-secret-alias")
                .property(USERNAME, "mqtt-user")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isSucceeded();
    }

    @Test
    void testMqttAddressBasicWithPartialOauth2() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-basic")
                .property(OAUTH2_CLIENT_ID, "mqtt-client-id")
                .property(OAUTH2_CLIENT_SECRET_ALIAS, "mqtt-client-secret-alias")
                .property(USERNAME, "mqtt-user")
                .property(PASSWORD_ALIAS, "mqtt-password-alias")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.success(null));

        var result = validator.validate(dataAddress);

        assertThat(result).isSucceeded();
    }

    @Test
    void testMqttAddressInvalidMqttType() {
        var dataAddress = DataAddress.Builder.newInstance()
                .property("type", MQTT_DATA_ADDRESS_TYPE)
                .property(BASE_URL, "tcp://mqtt-broker:1883")
                .property(MQTT_ENDPOINT_TYPE, "mqtt-wss-invalid")
                .property(USERNAME, "mqtt-user")
                .property(PASSWORD_ALIAS, "mqtt-password-alias")
                .build();

        when(endpointTypeParser.parse(anyString())).thenReturn(Result.failure("invalid type"));

        var result = validator.validate(dataAddress);

        assertThat(result).isFailed().satisfies(failure -> {
            assertTrue(failure.getMessages().stream().anyMatch(s -> s.contains("DataAddress of type '%s' must contain valid property '%s'".formatted(MQTT_DATA_ADDRESS_TYPE, MQTT_ENDPOINT_TYPE))));
        });
    }
}
