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
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.DataFlowPropertiesProvider;
import org.eclipse.edc.connector.controlplane.transfer.spi.flow.TransferTypeParser;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.connector.dataplane.selector.spi.DataPlaneSelectorService;
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClient;
import org.eclipse.edc.connector.dataplane.selector.spi.client.DataPlaneClientFactory;
import org.eclipse.edc.connector.dataplane.selector.spi.instance.DataPlaneInstance;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.ResponseStatus;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.result.ServiceResult;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowProvisionMessage;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowResponseMessage;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.eclipse.edc.spi.types.domain.transfer.FlowType;
import org.eclipse.edc.spi.types.domain.transfer.TransferType;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.eclipse.edc.validator.spi.Violation.violation;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_ADDRESS_TYPE;
import static org.factoryx.edc.mqtt.data.address.spi.MqttDataAddressSchema.MQTT_DATA_PULL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MqttDataFlowControllerTest {

    private final DataPlaneClient dataPlaneClient = mock();
    private final DataPlaneClientFactory dataPlaneClientFactory = mock();
    private final DataPlaneSelectorService selectorService = mock();
    private final DataFlowPropertiesProvider propertiesProvider = mock();
    private final TransferTypeParser transferTypeParser = mock();
    private final DataAddressValidatorRegistry addressValidatorRegistry = mock();
    private final MqttParamsProvider paramsProvider = mock();

    private MqttDataFlowController flowController;

    @BeforeEach
    void setup() {
        flowController = new MqttDataFlowController(
                () -> URI.create("http://localhost"), selectorService, dataPlaneClientFactory, propertiesProvider,
                "random", transferTypeParser, addressValidatorRegistry, paramsProvider);
    }

    @Test
    void testCanHandle() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));

        var result = flowController.canHandle(transferProcess);

        assertThat(result).isTrue();
    }

    @Test
    void testCanHandleTransferParseFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.failure("Transfer Type Parse Failed"));

        var result = flowController.canHandle(transferProcess);

        assertThat(result).isFalse();
    }

    @Test
    void testCanHandleInvalidTransferType() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType("Kafka-PULL")
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        when(transferTypeParser.parse("Kafka-PULL")).thenReturn(Result.success(new TransferType("Kafka", FlowType.PULL)));

        var result = flowController.canHandle(transferProcess);

        assertThat(result).isFalse();
    }

    @Test
    void testProvisionSuccess() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.success());
        when(paramsProvider.provideParams(any(MqttDataAddress.class))).thenReturn(MqttParams.Builder.newInstance().build());
        when(dataPlaneClientFactory.createClient(dataPlaneInstance)).thenReturn(dataPlaneClient);
        when(dataPlaneClient.provision(any(DataFlowProvisionMessage.class))).thenReturn(StatusResult.success(DataFlowResponseMessage.Builder.newInstance().provisioning(true).build()));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).isSucceeded();
    }

    @Test
    void testProvisionSelectorClientFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.notFound("Not Found"));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).withFailMessage("Not Found").isFailed();
    }

    @Test
    void testProvisionTransferTypeParseFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.failure("Transfer Type Parse Failed"));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).withFailMessage("Transfer Type Parse Failed").isFailed();

    }

    @Test
    void testProvisionPropertiesProviderFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.failure(ResponseStatus.FATAL_ERROR, "Property Provider failed"));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).withFailMessage("Property Provider failed").isFailed();
    }

    @Test
    void testProvisionContentDataAddressMissing() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).withFailMessage("Content Data Address is missing, can't find mqtt properties").isFailed();
    }

    @Test
    void testProvisionContentDataAddressValidationFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.failure(violation("data address validation failed", null)));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).withFailMessage("data address validation failed").isFailed();
    }

    @Test
    void testProvisionCallbackUrlMissing() {

        flowController = new MqttDataFlowController(
                null, selectorService, dataPlaneClientFactory, propertiesProvider,
                "random", transferTypeParser, addressValidatorRegistry, paramsProvider);

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.success());
        when(paramsProvider.provideParams(any(MqttDataAddress.class))).thenReturn(MqttParams.Builder.newInstance().build());
        when(dataPlaneClientFactory.createClient(dataPlaneInstance)).thenReturn(dataPlaneClient);
        when(dataPlaneClient.provision(any(DataFlowProvisionMessage.class))).thenReturn(StatusResult.success(DataFlowResponseMessage.Builder.newInstance().provisioning(true).build()));

        var result = flowController.provision(transferProcess, policy);

        assertThat(result).isSucceeded();
    }

    @Test
    void testStartSuccess() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.success());
        when(paramsProvider.provideParams(any(MqttDataAddress.class))).thenReturn(MqttParams.Builder.newInstance().build());
        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(dataPlaneClientFactory.createClient(dataPlaneInstance)).thenReturn(dataPlaneClient);
        when(dataPlaneClient.start(any(DataFlowStartMessage.class))).thenReturn(StatusResult.success(DataFlowResponseMessage.Builder.newInstance().build()));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).isSucceeded();
    }

    @Test
    void testStartTransferTypeParseFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.failure("Transfer Type Parse Failed"));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).withFailMessage("Transfer Type Parse Failed").isFailed();
    }

    @Test
    void testStartPropertiesProviderFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.failure(ResponseStatus.FATAL_ERROR, "Property Provider failed"));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).withFailMessage("Property Provider failed").isFailed();
    }

    @Test
    void testStartContentDataAddressMissing() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .build();

        var policy = Policy.Builder.newInstance().build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).withFailMessage("Content Data Address is missing, can't find mqtt properties").isFailed();
    }

    @Test
    void testStartContentDataAddressValidationFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.failure(violation("data address validation failed", null)));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).withFailMessage("data address validation failed").isFailed();
    }

    @Test
    void testStartSelectorClientFailure() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.success());
        when(paramsProvider.provideParams(any(MqttDataAddress.class))).thenReturn(MqttParams.Builder.newInstance().build());
        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.notFound("Not Found"));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).withFailMessage("Not Found").isFailed();
    }

    @Test
    void testStartCallBackUrlMissing() {

        flowController = new MqttDataFlowController(
                null, selectorService, dataPlaneClientFactory, propertiesProvider,
                "random", transferTypeParser, addressValidatorRegistry, paramsProvider);

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var policy = Policy.Builder.newInstance().build();
        var dataPlaneInstance = DataPlaneInstance.Builder.newInstance().url("http://any").build();

        when(transferTypeParser.parse(MQTT_DATA_PULL)).thenReturn(Result.success(new TransferType(MQTT_DATA_ADDRESS_TYPE, FlowType.PULL)));
        when(propertiesProvider.propertiesFor(transferProcess, policy)).thenReturn(StatusResult.success(Map.of()));
        when(addressValidatorRegistry.validateSource(any(DataAddress.class))).thenReturn(ValidationResult.success());
        when(paramsProvider.provideParams(any(MqttDataAddress.class))).thenReturn(MqttParams.Builder.newInstance().build());
        when(selectorService.select(anyString(), any())).thenReturn(ServiceResult.success(dataPlaneInstance));
        when(dataPlaneClientFactory.createClient(dataPlaneInstance)).thenReturn(dataPlaneClient);
        when(dataPlaneClient.start(any(DataFlowStartMessage.class))).thenReturn(StatusResult.success(DataFlowResponseMessage.Builder.newInstance().build()));

        var result = flowController.start(transferProcess, policy);

        assertThat(result).isSucceeded();
    }

    @Test
    void testSuspend() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var result = flowController.suspend(transferProcess);

        assertThat(result).isSucceeded();
    }

    @Test
    void testTerminate() {

        var transferProcess = TransferProcess.Builder.newInstance()
                .transferType(MQTT_DATA_PULL)
                .contentDataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var result = flowController.terminate(transferProcess);

        assertThat(result).isSucceeded();
    }

    @Test
    void testTransferTypesFor() {

        var asset = Asset.Builder.newInstance()
                .dataAddress(DataAddress.Builder.newInstance().type(MQTT_DATA_ADDRESS_TYPE).build())
                .build();

        var result = flowController.transferTypesFor(asset);
        assertThat(result).contains(MQTT_DATA_PULL);
    }

    @Test
    void testTransferTypesForInvalidAssetType() {

        var asset = Asset.Builder.newInstance()
                .dataAddress(DataAddress.Builder.newInstance().type("Kafka").build())
                .build();

        var result = flowController.transferTypesFor(asset);
        assertThat(result).isEmpty();
    }
}
