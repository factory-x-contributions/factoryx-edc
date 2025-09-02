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

package org.factoryx.edc.mqtt.data.address.spi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.eclipse.edc.spi.types.domain.DataAddress;

import java.util.Optional;

import static java.util.Collections.emptyMap;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.BASE_URL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_ADDRESS_TYPE;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.OAUTH2_CLIENT_ID;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.OAUTH2_CLIENT_SECRET_ALIAS;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.OAUTH2_TOKEN_URL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.PASSWORD_ALIAS;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.USERNAME;

@JsonTypeName()
@JsonDeserialize(builder = DataAddress.Builder.class)
public class MqttDataAddress extends DataAddress {

    private MqttDataAddress() {
        super();
        this.setType(MQTT_DATA_ADDRESS_TYPE);
    }

    @JsonIgnore
    public String getBaseUrl() {
        return getStringProperty(BASE_URL);
    }

    @JsonIgnore
    public String getOauth2TokenUrl() {
        return getStringProperty(OAUTH2_TOKEN_URL);
    }

    @JsonIgnore
    public String getOauth2ClientId() {
        return getStringProperty(OAUTH2_CLIENT_ID);
    }

    @JsonIgnore
    public String getOauth2ClientSecretAlias() {
        return getStringProperty(OAUTH2_CLIENT_SECRET_ALIAS);
    }

    @JsonIgnore
    public String getUsername() {
        return getStringProperty(USERNAME);
    }

    @JsonIgnore
    public String getPasswordAlias() {
        return getStringProperty(PASSWORD_ALIAS);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static final class Builder extends DataAddress.Builder<MqttDataAddress, Builder> {

        private Builder() {
            super(new MqttDataAddress());
        }

        @JsonCreator
        public static Builder newInstance() {
            return new Builder();
        }

        public Builder baseUrl(String name) {
            this.property(BASE_URL, name);
            return this;
        }

        @JsonProperty(OAUTH2_TOKEN_URL)
        public Builder oauth2TokenUrl(String path) {
            this.property(OAUTH2_TOKEN_URL, path);
            return this;
        }

        @JsonProperty(OAUTH2_CLIENT_ID)
        public Builder oauth2ClientId(String queryParams) {
            this.property(OAUTH2_CLIENT_ID, queryParams);
            return this;
        }

        @JsonProperty(OAUTH2_CLIENT_SECRET_ALIAS)
        public Builder oauth2ClientSecretAlias(String authCode) {
            this.property(OAUTH2_CLIENT_SECRET_ALIAS, authCode);
            return this;
        }

        public Builder username(String username) {
            this.property(USERNAME, username);
            return this;
        }

        public Builder passwordAlias(String proxyQueryParams) {
            this.property(PASSWORD_ALIAS, proxyQueryParams);
            return this;
        }

        public Builder copyFrom(DataAddress other) {
            Optional.ofNullable(other).map(DataAddress::getProperties).orElse(emptyMap()).forEach(this::property);
            return this;
        }

        @Override
        public MqttDataAddress build() {
            this.type(MQTT_DATA_ADDRESS_TYPE);
            return address;
        }
    }
}
