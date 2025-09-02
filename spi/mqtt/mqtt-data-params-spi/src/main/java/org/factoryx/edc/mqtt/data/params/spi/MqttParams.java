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

package org.factoryx.edc.mqtt.data.params.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.factoryx.edc.mqtt.data.params.spi.MqttConstants.AUTHORIZATION;
import static org.factoryx.edc.mqtt.data.params.spi.MqttConstants.EXPIRES_IN;
import static org.factoryx.edc.mqtt.data.params.spi.MqttConstants.PASSWORD;
import static org.factoryx.edc.mqtt.data.params.spi.MqttConstants.REFRESH_AUDIENCE;
import static org.factoryx.edc.mqtt.data.params.spi.MqttConstants.REFRESH_TOKEN;
import static org.factoryx.edc.mqtt.data.params.spi.MqttConstants.USERNAME;

public class MqttParams {

    private final Map<String, Object> properties = new HashMap<>();

    public Map<String, Object> getProperties() {
        return properties;
    }

    public static class Builder {
        private final MqttParams params;

        public static MqttParams.Builder newInstance() {
            return new MqttParams.Builder();
        }

        private Builder() {
            params = new MqttParams();
        }

        public MqttParams.Builder username(String username) {
            return this.property(USERNAME, username);
        }

        public MqttParams.Builder password(String password) {
            return this.property(PASSWORD, password);
        }

        public MqttParams.Builder refreshEndpoint(String refreshEndpoint) {
            return this.property(REFRESH_TOKEN, refreshEndpoint);
        }

        public MqttParams.Builder refreshToken(String refreshToken) {
            return this.property(REFRESH_TOKEN, refreshToken);
        }

        public MqttParams.Builder expiresIn(String expiresIn) {
            return this.property(EXPIRES_IN, expiresIn);
        }

        public MqttParams.Builder authorization(String authorization) {
            return this.property(AUTHORIZATION, authorization);
        }

        public MqttParams.Builder refreshAudience(String refreshAudience) {
            return this.property(REFRESH_AUDIENCE, refreshAudience);
        }

        public MqttParams.Builder property(String property, Object value) {
            params.properties.put(property, value);
            return this;
        }

        public MqttParams build() {
            params.properties.forEach((s, s2) -> Objects.requireNonNull(s2, "value for property: " + s));
            return params;
        }
    }
}
