/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.api.did;

import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.edc.web.spi.configuration.ApiContext;
import org.eclipse.tractusx.edc.validation.businesspartner.spi.BusinessPartnerStore;
import org.factoryx.edc.api.did.v1.BusinessPartnerDidGroupApiV1Controller;
import org.factoryx.edc.api.did.validation.BusinessPartnerDidValidator;

import static org.factoryx.edc.api.did.BusinessPartnerDidSchema.BUSINESS_PARTNER_DID_TYPE;

@Extension(value = "Registers the Business Partner DID Group API")
public class BusinessPartnerDidGroupApiExtension implements ServiceExtension {

    @Inject
    private WebService webService;
    @Inject
    private JsonLd jsonLdService;
    @Inject
    private BusinessPartnerStore businessPartnerStore;
    @Inject
    JsonObjectValidatorRegistry validatorRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {

        validatorRegistry.register(BUSINESS_PARTNER_DID_TYPE, BusinessPartnerDidValidator.instance());
        webService.registerResource(ApiContext.MANAGEMENT, new BusinessPartnerDidGroupApiV1Controller(businessPartnerStore, validatorRegistry));
    }
}
