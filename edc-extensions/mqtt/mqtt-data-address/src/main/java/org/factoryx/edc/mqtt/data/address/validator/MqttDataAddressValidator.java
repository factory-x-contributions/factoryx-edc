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

import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.factoryx.edc.core.spi.StringUtils;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema;

import java.net.URI;

import static org.eclipse.edc.validator.spi.Violation.violation;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.BASE_URL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_ADDRESS_TYPE;

/**
 * Class to validate a @{@link MqttDataAddress} type
 */
public class MqttDataAddressValidator implements Validator<DataAddress> {

    @Override
    public ValidationResult validate(DataAddress dataAddress) {

        return validateBaseUrl(dataAddress).merge(validateAuth(dataAddress));
    }

    protected ValidationResult validateBaseUrl(DataAddress dataAddress) {
        var baseUrl = dataAddress.getStringProperty(BASE_URL);
        return validateUrl(BASE_URL, baseUrl);
    }

    /**
     * Only one set of authentication should be allowed either oauth2 or basic
     *
     * @param dataAddress data address to be validated
     * @return validation result with success or failure
     */
    protected ValidationResult validateAuth(DataAddress dataAddress) {

        MqttDataAddress mqttDataAddress = MqttDataAddress.Builder.newInstance().copyFrom(dataAddress).build();
        boolean oauth2 = mqttDataAddress.hasOauth2();
        boolean basicAuth = mqttDataAddress.hasBasicAuth();

        // either one should be true, not both true, not both false
        if (oauth2 ^ basicAuth) {
            if (oauth2) {
                return validateUrl(MqttDataAddressSchema.OAUTH2_TOKEN_URL, mqttDataAddress.getOauth2TokenUrl());
            }
            return ValidationResult.success();
        } else {
            var violation = violation("DataAddress of type '%s' must contain exactly one set of auth 'oauth2' or 'basic'".formatted(MQTT_DATA_ADDRESS_TYPE), null);
            return ValidationResult.failure(violation);
        }
    }

    protected ValidationResult validateUrl(String propertyName, String url) {
        try {
            URI uri = new URI(url);
            if (StringUtils.isAnyBlank(uri.getScheme(), uri.getHost())) {
                throw new IllegalArgumentException("scheme / host missing from the url");
            }
            return ValidationResult.success();
        } catch (Exception e) {
            var violation = violation("DataAddress of type '%s' must contain a valid url with property '%s'".formatted(MQTT_DATA_ADDRESS_TYPE, propertyName), propertyName, url);
            return ValidationResult.failure(violation);
        }
    }
}
