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


import org.eclipse.edc.spi.types.domain.transfer.FlowType;

import static org.eclipse.edc.dataaddress.httpdata.spi.HttpDataAddressSchema.HTTP_DATA_TYPE;

public interface MqttDataAddressSchema {

    /**
     * DataAddress type
     */
    String MQTT_DATA_ADDRESS_TYPE = "Mqtt";
    String MQTT_DATA_PULL = "%s-%s".formatted(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL);
    String HTTP_DATA_PULL = "%s-%s".formatted(HTTP_DATA_TYPE, FlowType.PULL);

    String BASE_URL = "baseUrl";
    String MQTT_ENDPOINT_TYPE = "mqttEndpointType";
    String OAUTH2_TOKEN_URL = "oauth2:tokenUrl";
    String OAUTH2_CLIENT_ID = "oauth2:clientId";
    String OAUTH2_CLIENT_SECRET_ALIAS = "oauth2:clientSecretKeyAlias";
    String USERNAME = "username";
    String PASSWORD_ALIAS = "passwordAlias";
}
