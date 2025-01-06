/*
 * Copyright (c) 2024 SAP SE
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
 */

package org.eclipse.tractusx.edc.controlplane.contract;

import org.eclipse.edc.connector.controlplane.asset.spi.index.AssetIndex;
import org.eclipse.edc.connector.controlplane.contract.spi.validation.ContractValidationService;
import org.eclipse.edc.connector.controlplane.contract.validation.ContractValidationServiceImpl;
import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator.AtomicConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.ConstraintComparator.MultiplicityConstraintComparator;
import org.eclipse.tractusx.edc.controlplane.contract.validation.PermissivePolicyEquality;

/**
 * Extension that provides contract validation functionality by registering a ContractValidationService implementation.
 * This service validates contracts against assets and policies using a permissive policy equality comparison approach.
 */
@Provides({ContractValidationService.class})
@Extension(value = ContractValidationExtension.NAME)
public class ContractValidationExtension implements ServiceExtension {

    /**
     * The name of the extension.
     */
    public static final String NAME = "Contract Validation";

    /**
     * The asset index service.
     */
    @Inject
    private AssetIndex assetIndex;

    /**
     * The policy engine service.
     */
    @Inject
    private PolicyEngine policyEngine;

    /**
     * The type manager service.
     */
    @Inject
    private TypeManager typeManager;

    /**
     * The monitor service.
     */
    @Inject
    private Monitor monitor;

    /**
     * The name of the extension.
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * Initializes the extension by registering the ContractValidationService implementation.
     * This implementation uses a permissive policy equality comparison approach.
     *
     * @param context The service extension context.
     */
    @Override
    public void initialize(ServiceExtensionContext context) {

        MultiplicityConstraintComparator multiplicityConstraintComparator = new MultiplicityConstraintComparator(typeManager);
        ConstraintComparator constraintComparator = new ConstraintComparator(multiplicityConstraintComparator, new AtomicConstraintComparator());
        var policyEqualityV2 = new PermissivePolicyEquality(typeManager, monitor, constraintComparator);
        var validationServiceV2 = new ContractValidationServiceImpl(assetIndex, policyEngine, policyEqualityV2);
        context.registerService(ContractValidationService.class, validationServiceV2);
    }
}
