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

package org.factoryx.edc.mqtt.data.params;

import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.factoryx.edc.mqtt.data.params.basic.BasicAuthMqttParamsDecorator;
import org.factoryx.edc.mqtt.data.params.oauth2.Oauth2MqttParamsDecorator;
import org.factoryx.edc.mqtt.data.params.provider.MqttParamsProviderImpl;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;


@ExtendWith(DependencyInjectionExtension.class)
class MqttDataParamsExtensionTest {

    private final Vault vault = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(Vault.class, vault);
    }

    @Test
    void testInitialize(ServiceExtensionContext context, MqttDataParamsExtension extension) {

        try (MockedConstruction<MqttParamsProviderImpl> providerMock = mockConstruction(MqttParamsProviderImpl.class)) {

            extension.initialize(context);

            MqttParamsProvider provider = providerMock.constructed().get(0);

            verify(context).registerService(eq(MqttParamsProvider.class), any(MqttParamsProvider.class));
            verify(provider).registerDecorator(any(BasicAuthMqttParamsDecorator.class));
            verify(provider).registerDecorator(any(Oauth2MqttParamsDecorator.class));
        }
    }
}
