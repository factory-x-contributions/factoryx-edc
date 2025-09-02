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

import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;

/**
 * Define how to decorate the {@link MqttParams} builder.
 */
@FunctionalInterface
public interface MqttParamsDecorator {

    /**
     * Decorate params with information coming from the request and the data address. Return the param object.
     */
    MqttParams.Builder decorate(MqttDataAddress address, MqttParams.Builder params);
}
