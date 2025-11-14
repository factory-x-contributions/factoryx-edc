/********************************************************************************
 * Copyright (c) 2025 Cofinity-X GmbH
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

package org.factoryx.edc.protocol.protocol;

import org.eclipse.edc.protocol.dsp.http.spi.api.DspBaseWebhookAddress;
import org.eclipse.edc.protocol.spi.DataspaceProfileContext;
import org.eclipse.edc.protocol.spi.DataspaceProfileContextRegistry;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.factoryx.edc.protocol.protocol.identifier.DidExtractionFunction;

import static org.eclipse.edc.protocol.dsp.http.spi.types.HttpMessageProtocol.DATASPACE_PROTOCOL_HTTP;
import static org.eclipse.edc.protocol.dsp.spi.type.Dsp08Constants.V_08;
import static org.eclipse.edc.protocol.dsp.spi.type.Dsp2025Constants.DATASPACE_PROTOCOL_HTTP_V_2025_1;
import static org.eclipse.edc.protocol.dsp.spi.type.Dsp2025Constants.V_2025_1;
import static org.eclipse.edc.protocol.dsp.spi.type.Dsp2025Constants.V_2025_1_PATH;

public class DataspaceProtocolExtension implements ServiceExtension {

    @Inject
    private DataspaceProfileContextRegistry contextRegistry;
    @Inject
    private DspBaseWebhookAddress dspWebhookAddress;

    @Override
    public void initialize(ServiceExtensionContext context) {
        contextRegistry.register(new DataspaceProfileContext(DATASPACE_PROTOCOL_HTTP, V_08, () -> dspWebhookAddress.get(), context.getParticipantId(), new DidExtractionFunction()));
        contextRegistry.register(new DataspaceProfileContext(DATASPACE_PROTOCOL_HTTP_V_2025_1, V_2025_1, () -> dspWebhookAddress.get() + V_2025_1_PATH, context.getParticipantId(), new DidExtractionFunction()));
    }
}
