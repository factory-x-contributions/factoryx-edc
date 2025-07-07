/*
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
 */

package org.factoryx.edc.api.did.v3;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.json.Json;
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
import org.eclipse.edc.web.spi.exception.ObjectNotFoundException;
import org.eclipse.tractusx.edc.validation.businesspartner.spi.BusinessPartnerStore;
import org.factoryx.edc.api.did.BaseBusinessPartnerDidGroupApiController;

import java.util.List;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;


@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
@Path("/v3/business-partner-groups")
public class BusinessPartnerDidGroupApiV3Controller extends BaseBusinessPartnerDidGroupApiController implements BusinessPartnerDidGroupApiV3 {

    public BusinessPartnerDidGroupApiV3Controller(BusinessPartnerStore businessPartnerService, JsonObjectValidatorRegistry validator) {
        super(businessPartnerService, validator);
    }

    @GET
    @Path("/{did}")
    @Override
    public JsonObject resolveV3(@PathParam("did") String did) {
        return super.resolve(did);
    }

    @GET
    @Path("/group/{group}")
    @Override
    public JsonObject resolveGroupV3(@PathParam("group") String group) {
        return businessPartnerService.resolveForBpnGroup(group)
                .map(result -> Json.createObjectBuilder()
                        .add(ID, group)
                        .add(FX_POLICY_NS + "dids", Json.createArrayBuilder(result))
                        .build())
                .orElseThrow(failure -> new ObjectNotFoundException(List.class, failure.getFailureDetail()));
    }

    @GET
    @Path("/groups")
    @Override
    public JsonObject resolveGroupsV3() {
        return businessPartnerService.resolveForBpnGroups()
                .map(result -> Json.createObjectBuilder()
                        .add(FX_POLICY_NS + "groups", Json.createArrayBuilder(result))
                        .build())
                .orElseThrow(failure -> new ObjectNotFoundException(List.class, failure.getFailureDetail()));
    }

    @DELETE
    @Path("/{did}")
    @Override
    public void deleteEntryV3(@PathParam("did") String did) {
        super.deleteEntry(did);
    }

    @PUT
    @Override
    public void updateEntryV3(@RequestBody JsonObject object) {
        super.updateEntry(object);
    }

    @POST
    @Override
    public void createEntryV3(@RequestBody JsonObject object) {
        super.createEntry(object);
    }

}
