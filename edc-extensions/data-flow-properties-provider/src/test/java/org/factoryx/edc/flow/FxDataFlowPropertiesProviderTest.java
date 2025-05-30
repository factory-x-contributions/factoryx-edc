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

import org.assertj.core.api.Assertions;
import org.eclipse.edc.connector.controlplane.transfer.spi.types.TransferProcess;
import org.eclipse.edc.policy.model.Policy;
import org.junit.jupiter.api.Test;

import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.eclipse.tractusx.edc.edr.spi.CoreConstants.AUDIENCE_PROPERTY;

public class FxDataFlowPropertiesProviderTest {

    private final FxDataFlowPropertiesProvider provider = new FxDataFlowPropertiesProvider();


    @Test
    void propertiesFor() {

        var did = "did:web:example.com";

        var result = provider.propertiesFor(TransferProcess.Builder.newInstance().build(), Policy.Builder.newInstance().assignee(did).build());

        assertThat(result).isSucceeded().satisfies(properties -> {
            Assertions.assertThat(properties).containsEntry(AUDIENCE_PROPERTY, did);
        });
    }

    @Test
    void propertiesFor_fails_whenResolutionIsNull() {

        String did = null;

        var result = provider.propertiesFor(TransferProcess.Builder.newInstance().build(), Policy.Builder.newInstance().assignee(did).build());

        assertThat(result).isFailed().withFailMessage("Policy Assignee (DID) is missing");
    }
}