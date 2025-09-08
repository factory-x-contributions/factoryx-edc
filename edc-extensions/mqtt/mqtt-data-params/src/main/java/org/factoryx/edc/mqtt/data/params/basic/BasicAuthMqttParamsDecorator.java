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

package org.factoryx.edc.mqtt.data.params.basic;

import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.security.Vault;
import org.factoryx.edc.mqtt.data.address.spi.MqttDataAddress;
import org.factoryx.edc.mqtt.data.params.spi.MqttParams;
import org.factoryx.edc.mqtt.data.params.spi.MqttParamsDecorator;

import java.util.Optional;

public class BasicAuthMqttParamsDecorator implements MqttParamsDecorator {

    private final Vault vault;

    public BasicAuthMqttParamsDecorator(Vault vault) {
        this.vault = vault;
    }

    @Override
    public MqttParams.Builder decorate(MqttDataAddress address, MqttParams.Builder params) {

        if (address.hasBasicAuth()) {
            params.username(address.getUsername());

            Optional.ofNullable(vault.resolveSecret(address.getPasswordAlias()))
                    .map(params::password)
                    .orElseThrow(() -> new EdcException("No secret found in vault with name %s".formatted(address.getPasswordAlias())));
        }
        return params;
    }
}
