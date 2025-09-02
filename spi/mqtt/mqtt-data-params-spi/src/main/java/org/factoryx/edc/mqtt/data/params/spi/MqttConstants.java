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

public interface MqttConstants {

    String DSP_MQTT_PREFIX = "dsp-mqtt";
    String DSP_MQTT_NS = "https://w3id.org/dspace/2025/1/mqtt-pull/";

    String USERNAME = DSP_MQTT_NS + "username";
    String PASSWORD = DSP_MQTT_NS + "password";

    String REFRESH_ENDPOINT = DSP_MQTT_NS + "refreshEndpoint";
    String REFRESH_TOKEN = DSP_MQTT_NS + "refreshToken";
    String EXPIRES_IN = DSP_MQTT_NS + "expiresIn";
    String AUTHORIZATION = DSP_MQTT_NS + "authorization";
    String REFRESH_AUDIENCE = DSP_MQTT_NS + "refreshAudience";
}
