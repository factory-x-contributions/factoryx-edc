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

package org.factoryx.edc.flow;

import org.eclipse.edc.connector.controlplane.transfer.spi.flow.DataFlowPropertiesProvider;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;
import org.eclipse.edc.spi.response.ResponseStatus;
import org.eclipse.edc.spi.response.StatusResult;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;

import java.util.Map;

import static org.eclipse.tractusx.edc.edr.spi.CoreConstants.AUDIENCE_PROPERTY;

/**
 * Extension of {@link DataFlowPropertiesProvider} which provides additional properties in the {@link DataFlowStartMessage}
 * like the DID of the counter-party BPN.
 */
public class FxDataFlowPropertiesProvider implements DataFlowPropertiesProvider {

    @Override
    public StatusResult<Map<String, String>> propertiesFor(TransferProcess transferProcess, Policy policy) {
        var did = policy.getAssignee();
        if (did != null) {
            return StatusResult.success(Map.of(AUDIENCE_PROPERTY, did));
        } else {
            return StatusResult.failure(ResponseStatus.FATAL_ERROR, "Policy Assignee (DID) is missing");
        }
    }
}
