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

import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.factoryx.edc.mqtt.data.params.basic.BasicAuthMqttParamsDecorator;
import org.factoryx.edc.mqtt.data.params.provider.MqttParamsProviderImpl;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsProvider;

@Provides(MqttParamsProvider.class)
public class MqttDataParamsExtension implements ServiceExtension {

    @Inject
    private Vault vault;

    @Override
    public void initialize(ServiceExtensionContext context) {

        var provider = new MqttParamsProviderImpl();

        var basicAuthMqttParamsDecorator = new BasicAuthMqttParamsDecorator(vault);
        provider.registerDecorator(basicAuthMqttParamsDecorator);

        context.registerService(MqttParamsProvider.class, provider);
    }
}
