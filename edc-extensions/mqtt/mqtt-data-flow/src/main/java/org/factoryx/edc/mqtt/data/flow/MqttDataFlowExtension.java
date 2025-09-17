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

package org.factoryx.edc.mqtt.data.flow;

import org.eclipse.edc.connector.controlplane.transfer.spi.flow.DataFlowManager;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.DataFlowPropertiesProvider;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.TransferTypeParser;
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClientFactory;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.eclipse.edc.web.spi.configuration.context.ControlApiUrl;
import org.factoryx.edc.mqtt.data.flow.controller.MqttDataFlowController;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsProvider;


public class MqttDataFlowExtension implements ServiceExtension {

    private static final String DEFAULT_DATAPLANE_SELECTOR_STRATEGY = "random";

    @Setting(description = "Defines strategy for Data Plane instance selection in case Data Plane is not embedded in current runtime", defaultValue = DEFAULT_DATAPLANE_SELECTOR_STRATEGY, key = "edc.dataplane.client.selector.strategy")
    private String selectionStrategy;

    @Inject
    private DataFlowManager dataFlowManager;

    @Inject(required = false)
    private ControlApiUrl callbackUrl;

    @Inject
    private DataPlaneSelectorService selectorService;

    @Inject
    private DataPlaneClientFactory clientFactory;

    @Inject
    private DataFlowPropertiesProvider propertiesProvider;

    @Inject
    private TransferTypeParser transferTypeParser;

    @Inject
    private DataAddressValidatorRegistry addressValidatorRegistry;

    @Inject
    private MqttParamsProvider mqttParamsProvider;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var controller = new MqttDataFlowController(callbackUrl, selectorService,
                clientFactory, propertiesProvider, selectionStrategy, transferTypeParser,
                addressValidatorRegistry, mqttParamsProvider);
        dataFlowManager.register(controller);
    }
}
