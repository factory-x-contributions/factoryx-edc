/********************************************************************************
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.api.did.v1;

import io.restassured.specification.RequestSpecification;
import org.eclipse.edc.junit.annotations.ApiTest;
import org.eclipse.edc.runtime.core.validator.JsonObjectValidatorRegistryImpl;
import org.factoryx.edc.api.did.BaseBusinessPartnerDidGroupApiControllerTest;
import org.factoryx.edc.api.did.validation.BusinessPartnerDidValidator;

import static io.restassured.RestAssured.given;
import static org.factoryx.edc.api.did.BusinessPartnerDidSchema.BUSINESS_PARTNER_DID_TYPE;

@ApiTest
class BusinessPartnerDidDidGroupApiV1ControllerTest extends BaseBusinessPartnerDidGroupApiControllerTest {

    @Override
    protected Object controller() {
        JsonObjectValidatorRegistryImpl validator = new JsonObjectValidatorRegistryImpl();
        validator.register(BUSINESS_PARTNER_DID_TYPE, BusinessPartnerDidValidator.instance());
        return new BusinessPartnerDidGroupApiV1Controller(businessPartnerStore, validator, monitor);
    }

    @Override
    protected RequestSpecification baseRequest() {
        return given()
                .baseUri("http://localhost:" + port)
                .basePath("/v1/business-partner-did-groups")
                .when();
    }

}
