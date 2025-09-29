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

package org.factoryx.edc.mqtt.data.flow.controller;

import org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.DataFlowController;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.DataFlowPropertiesProvider;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.TransferTypeParser;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.DataFlowResponse;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClientFactory;
import org.eclipse.edc.connector.dataplane.selector.spi.instance.DataPlaneInstance;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowProvisionMessage;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowResponseMessage;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.eclipse.edc.spi.types.domain.transfer.TransferType;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.eclipse.edc.web.spi.configuration.context.ControlApiUrl;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess.Type.CONSUMER;
import static org.eclipse.edc.dataaddress.httpdata.spi.HttpDataAddressSchema.HTTP_DATA_TYPE;
import static org.eclipse.edc.spi.response.ResponseStatus.FATAL_ERROR;
import static org.eclipse.edc.spi.types.domain.transfer.FlowType.PULL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.HTTP_DATA_PULL;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_ADDRESS_TYPE;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_PULL;

/**
 * A {@link DataFlowController} implementation to handle data of type: {@value MqttDataAddressSchema#MQTT_DATA_ADDRESS_TYPE}
 * Few points to note:
 * <ul>
 *   <li>This controller gets executed at both consumer / provider control-plane.</li>
 * <li>In case of consumer, source asset data address  {@link TransferProcess#getContentDataAddress()} is null,
 *   since only provider has access to it.</li>
 * <li>When a data flow is started via sending a {@link DataFlowStartMessage} to data-plane,
 *   it always contains transferType: {@code HttpData-PULL} and a destination of type {@code HttpProxy},
 *   because There is no data plane instance which can handle {@value MqttDataAddressSchema#MQTT_DATA_ADDRESS_TYPE} Data type.</li>
 * <li>In case of EDR, we don't actually directly initiate a transfer,
 *   but it gets automatically initiated when a contract negotiation is finalized via {@code org.eclipse.tractusx.edc.callback.ContractNegotiationCallback}
 *   and it also hard-codes the transferType as {@code HttpData-PULL} and data destination of type {@code HttpProxy}.</li>
 * </ul>
 *
 */
public class MqttDataFlowController implements DataFlowController {

    private static final DataAddress HTTP_DATA_ADDRESS = DataAddress.Builder.newInstance().type(HTTP_DATA_TYPE).build();

    private final ControlApiUrl callbackUrl;
    private final DataPlaneSelectorService selectorClient;
    private final DataPlaneClientFactory clientFactory;
    private final DataFlowPropertiesProvider propertiesProvider;
    private final String selectionStrategy;
    private final TransferTypeParser transferTypeParser;
    private final DataAddressValidatorRegistry addressValidatorRegistry;
    private final MqttParamsProvider mqttParamsProvider;

    public MqttDataFlowController(ControlApiUrl callbackUrl, DataPlaneSelectorService selectorClient,
                                  DataPlaneClientFactory clientFactory, DataFlowPropertiesProvider propertiesProvider, String selectionStrategy,
                                  TransferTypeParser transferTypeParser, DataAddressValidatorRegistry addressValidatorRegistry, MqttParamsProvider mqttParamsProvider) {
        this.callbackUrl = callbackUrl;
        this.selectorClient = selectorClient;
        this.clientFactory = clientFactory;
        this.propertiesProvider = propertiesProvider;
        this.selectionStrategy = selectionStrategy;
        this.transferTypeParser = transferTypeParser;
        this.addressValidatorRegistry = addressValidatorRegistry;
        this.mqttParamsProvider = mqttParamsProvider;
    }

    @Override
    public boolean canHandle(TransferProcess transferProcess) {

        Result<TransferType> transferType = transferTypeParser.parse(transferProcess.getTransferType());
        if  (transferProcess.getType() == CONSUMER) {
            // transferProcess#getContentDataAddress() is null for consumer
            return transferType.succeeded();
        } else {
            return transferType.succeeded() &&
                    MQTT_DATA_ADDRESS_TYPE.equals(transferProcess.getContentDataAddress().getType());
        }
    }

    @Override
    public StatusResult<DataFlowResponse> provision(TransferProcess transferProcess, Policy policy) {

        var selection = selectorClient.select(selectionStrategy, dataPlane -> dataPlane.canProvisionDestination(transferProcess.getDataDestination()));
        if (selection.failed()) {
            return StatusResult.failure(FATAL_ERROR, selection.getFailureDetail());
        }

        var transferTypeParse = transferTypeParser.parse(transferProcess.getTransferType());
        if (transferTypeParse.failed()) {
            return StatusResult.failure(FATAL_ERROR, transferTypeParse.getFailureDetail());
        }

        var propertiesResult = propertiesProvider.propertiesFor(transferProcess, policy);
        if (propertiesResult.failed()) {
            return StatusResult.failure(FATAL_ERROR, propertiesResult.getFailureDetail());
        }

        var contentDataAddress = transferProcess.getContentDataAddress();
        if (contentDataAddress == null) {
            return StatusResult.failure(FATAL_ERROR, "Content Data Address is missing, can't find mqtt properties");
        }

        var contentDataAddressResult = addressValidatorRegistry.validateSource(contentDataAddress);
        if (contentDataAddressResult.failed()) {
            return StatusResult.failure(FATAL_ERROR, contentDataAddressResult.getFailureDetail());
        }

        var mqttContentDataAddress = MqttDataAddress.Builder.newInstance().copyFrom(contentDataAddress).build();
        var mqttParams = mqttParamsProvider.provideParams(mqttContentDataAddress);

        var properties = mergeProperties(propertiesResult.getContent(), mqttParams.getProperties());

        var dataFlowRequest = DataFlowProvisionMessage.Builder.newInstance()
                .processId(transferProcess.getId())
                .destination(transferProcess.getDataDestination())
                .participantId(policy.getAssignee())
                .agreementId(transferProcess.getContractId())
                .assetId(transferProcess.getAssetId())
                .transferType(transferTypeParse.getContent())
                .callbackAddress(callbackUrl != null ? callbackUrl.get() : null)
                .properties(properties)
                .build();

        var dataPlaneInstance = selection.getContent();
        return clientFactory.createClient(dataPlaneInstance)
                .provision(dataFlowRequest)
                .map(it -> toResponse(it, dataPlaneInstance));
    }

    @Override
    public @NotNull StatusResult<DataFlowResponse> start(TransferProcess transferProcess, Policy policy) {
        var transferTypeParse = transferTypeParser.parse(transferProcess.getTransferType());
        if (transferTypeParse.failed()) {
            return StatusResult.failure(FATAL_ERROR, transferTypeParse.getFailureDetail());
        }

        var propertiesResult = propertiesProvider.propertiesFor(transferProcess, policy);
        if (propertiesResult.failed()) {
            return StatusResult.failure(FATAL_ERROR, propertiesResult.getFailureDetail());
        }

        var contentDataAddress = transferProcess.getContentDataAddress();
        if (contentDataAddress == null) {
            return StatusResult.failure(FATAL_ERROR, "Content Data Address is missing, can't find mqtt properties");
        }

        var contentDataAddressResult = addressValidatorRegistry.validateSource(contentDataAddress);
        if (contentDataAddressResult.failed()) {
            return StatusResult.failure(FATAL_ERROR, contentDataAddressResult.getFailureDetail());
        }

        var mqttContentDataAddress = MqttDataAddress.Builder.newInstance().copyFrom(contentDataAddress).build();
        var mqttParams = mqttParamsProvider.provideParams(mqttContentDataAddress);

        var properties = mergeProperties(propertiesResult.getContent(), mqttParams.getProperties());

        var dataFlowRequest = DataFlowStartMessage.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .processId(transferProcess.getId())
                .sourceDataAddress(contentDataAddress)
                .destinationDataAddress(transferProcess.getDataDestination())
                .participantId(policy.getAssignee())
                .agreementId(transferProcess.getContractId())
                .assetId(transferProcess.getAssetId())
                .transferType(new TransferType(HTTP_DATA_TYPE, PULL))
                .callbackAddress(callbackUrl != null ? callbackUrl.get() : null)
                .properties(properties)
                .build();

        var selection = selectorClient.select(selectionStrategy, dataPlane -> dataPlane.canHandle(HTTP_DATA_ADDRESS, HTTP_DATA_PULL));
        if (!selection.succeeded()) {
            return StatusResult.failure(FATAL_ERROR, selection.getFailureDetail());
        }

        var dataPlaneInstance = selection.getContent();
        return clientFactory.createClient(dataPlaneInstance)
                .start(dataFlowRequest)
                .map(it -> toResponse(it, dataPlaneInstance));
    }

    @Override
    public StatusResult<Void> suspend(TransferProcess transferProcess) {
        // TODO if a revokeUrl is present in data address, auth token should be revoked here
        return StatusResult.success();
    }

    @Override
    public StatusResult<Void> terminate(TransferProcess transferProcess) {
        // TODO if a revokeUrl is present in data address, auth token should be revoked here
        return StatusResult.success();
    }

    @Override
    public Set<String> transferTypesFor(Asset asset) {
        if (MQTT_DATA_ADDRESS_TYPE.equals(asset.getDataAddress().getType())) {
            return Set.of(MQTT_DATA_PULL, HTTP_DATA_PULL);
        }
        return Set.of();
    }

    private DataFlowResponse toResponse(DataFlowResponseMessage it, DataPlaneInstance dataPlaneInstance) {
        return DataFlowResponse.Builder.newInstance()
                .dataAddress(it.getDataAddress())
                .dataPlaneId(dataPlaneInstance.getId())
                .provisioning(it.isProvisioning())
                .build();
    }

    private Map<String, String> mergeProperties(Map<String, String> content, Map<String, String> properties) {
        var merged = new HashMap<>(content);
        merged.putAll(properties);
        return merged;
    }
}
