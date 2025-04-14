/********************************************************************************
 * Copyright (c) 2025 T-Systems International GmbH
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

package org.factoryx.edc.validators.didselector;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;

import static org.eclipse.edc.connector.controlplane.asset.spi.domain.Asset.EDC_ASSET_TYPE;
import static org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition.EDC_POLICY_DEFINITION_TYPE;

@Extension(value = FxValidatorExtension.NAME)
public class FxValidatorExtension implements ServiceExtension {

    public static final String NAME = "Incorrect DID Policy Selector Blocker extension";

    private static final String BLOCKER_ENABLED = "true";

    @Setting(description = "Block management APIs with from being created/updated with an incorrect DID selector.", defaultValue = BLOCKER_ENABLED, key = "fx.edc.validator.incorrect-did-selector")
    private boolean blockerEnabled;

    @Inject
    JsonObjectValidatorRegistry validatorRegistry;

    @Inject
    Monitor monitor;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void prepare() {
        if (blockerEnabled) {
            monitor.info("Validator that blocks incorrect DID selector has been enabled");
            validatorRegistry.register(EDC_POLICY_DEFINITION_TYPE, IncorrectDidPolicyValidator.instance());
            validatorRegistry.register(EDC_ASSET_TYPE, AssetTypeValidator.instance());
        }
    }

}
