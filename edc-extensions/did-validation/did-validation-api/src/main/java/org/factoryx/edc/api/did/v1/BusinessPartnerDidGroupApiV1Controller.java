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

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.tractusx.edc.validation.businesspartner.spi.BusinessPartnerStore;
import org.factoryx.edc.api.did.BaseBusinessPartnerDidGroupApiController;


@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
@Path("/v1/business-partner-did-groups")
public class BusinessPartnerDidGroupApiV1Controller extends BaseBusinessPartnerDidGroupApiController implements BusinessPartnerDidGroupApiV1 {

    public BusinessPartnerDidGroupApiV1Controller(BusinessPartnerStore businessPartnerService, JsonObjectValidatorRegistry validator) {
        super(businessPartnerService, validator);
    }

    @GET
    @Path("/{did}")
    @Override
    public JsonObject resolve(@PathParam("did") String did) {
        return super.resolve(did);
    }

    @DELETE
    @Path("/{did}")
    @Override
    public void deleteEntry(@PathParam("did") String did) {
        super.deleteEntry(did);
    }

    @PUT
    @Override
    public void updateEntry(@RequestBody JsonObject object) {
        super.updateEntry(object);
    }

    @POST
    @Override
    public void createEntry(@RequestBody JsonObject object) {
        super.createEntry(object);
    }

}
