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

package org.factoryx.edc.mqtt.data.address;

import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.factoryx.edc.mqtt.data.address.validator.MqttDataAddressValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_ADDRESS_TYPE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(DependencyInjectionExtension.class)
class MqttDataAddressExtensionTest {

    DataAddressValidatorRegistry dataAddressValidatorRegistry = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(DataAddressValidatorRegistry.class, dataAddressValidatorRegistry);
    }

    @Test
    void testInitialize(ServiceExtensionContext context, MqttDataAddressExtension extension) {

        extension.initialize(context);

        verify(dataAddressValidatorRegistry).registerSourceValidator(eq(MQTT_DATA_ADDRESS_TYPE), any(MqttDataAddressValidator.class));
        verify(dataAddressValidatorRegistry).registerDestinationValidator(eq(MQTT_DATA_ADDRESS_TYPE), any(MqttDataAddressValidator.class));
    }
}
