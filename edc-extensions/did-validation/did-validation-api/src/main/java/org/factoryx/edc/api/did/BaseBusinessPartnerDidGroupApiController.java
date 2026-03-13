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

package org.factoryx.edc.api.did;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.ws.rs.PathParam;
import org.eclipse.edc.validator.spi.JsonObjectValidatorRegistry;
import org.eclipse.edc.web.spi.exception.InvalidRequestException;
import org.eclipse.edc.web.spi.exception.ObjectConflictException;
import org.eclipse.edc.web.spi.exception.ObjectNotFoundException;
import org.eclipse.edc.web.spi.exception.ValidationFailureException;
import org.eclipse.tractusx.edc.validation.businesspartner.spi.store.BusinessPartnerStore;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VALUE;
import static org.factoryx.edc.api.did.BusinessPartnerDidSchema.BUSINESS_PARTNER_DID_TYPE;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;

/**
 * Base controller for Business Partner DID Group API operations.
 * Provides common CRUD operations for managing DID to group mappings.
 */
public abstract class BaseBusinessPartnerDidGroupApiController {

    /**
     * Store for business partner DID data.
     */
    protected final BusinessPartnerStore businessPartnerService;
    private final JsonObjectValidatorRegistry validator;

    /**
     * Constructs a new BaseBusinessPartnerDidGroupApiController.
     *
     * @param businessPartnerService the business partner store
     * @param validator the JSON object validator registry
     */
    public BaseBusinessPartnerDidGroupApiController(BusinessPartnerStore businessPartnerService, JsonObjectValidatorRegistry validator) {
        this.businessPartnerService = businessPartnerService;
        this.validator = validator;
    }

    /**
     * Resolves the groups associated with a specific DID.
     *
     * @param did the DID to resolve groups for
     * @return JSON object containing the DID and its associated groups
     * @throws ObjectNotFoundException if the DID is not found
     */
    public JsonObject resolve(String did) {

        // StoreResult does not support the .map() operator, because it does not override newInstance()
        var result = businessPartnerService.resolveForBpn(did);
        if (result.succeeded()) {
            return createObject(did, result.getContent());
        }

        throw new ObjectNotFoundException(List.class, result.getFailureDetail());
    }

    /**
     * Deletes a DID entry from the store.
     *
     * @param did the DID to delete
     * @throws ObjectNotFoundException if the DID is not found
     */
    public void deleteEntry(@PathParam("did") String did) {
        businessPartnerService.delete(did)
                .orElseThrow(f -> new ObjectNotFoundException(List.class, f.getFailureDetail()));
    }

    /**
     * Updates an existing DID entry with new group assignments.
     *
     * @param object JSON object containing the DID and its groups
     * @throws ValidationFailureException if the object fails validation
     * @throws ObjectNotFoundException if the DID is not found
     */
    public void updateEntry(@RequestBody JsonObject object) {
        validator.validate(BUSINESS_PARTNER_DID_TYPE, object).orElseThrow(ValidationFailureException::new);

        var did = getDid(object);
        var groups = getGroups(object);
        businessPartnerService.update(did, groups)
                .orElseThrow(f -> new ObjectNotFoundException(List.class, f.getFailureDetail()));
    }

    /**
     * Creates a new DID entry with group assignments.
     *
     * @param object JSON object containing the DID and its groups
     * @throws ValidationFailureException if the object fails validation
     * @throws ObjectConflictException if the DID already exists
     */
    public void createEntry(@RequestBody JsonObject object) {
        validator.validate(BUSINESS_PARTNER_DID_TYPE, object).orElseThrow(ValidationFailureException::new);

        var did = getDid(object);
        var groups = getGroups(object);
        businessPartnerService.save(did, groups)
                .orElseThrow(f -> new ObjectConflictException(f.getFailureDetail()));
    }

    private JsonObject createObject(String did, List<String> list) {
        return Json.createObjectBuilder()
                .add(ID, did)
                .add(FX_POLICY_NS + "groups", Json.createArrayBuilder(list))
                .build();
    }

    private String getDid(JsonObject object) {
        try {
            return object.getString(ID);
        } catch (Exception ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }

    @NotNull
    private List<String> getGroups(JsonObject object) {
        try {
            return object.getJsonArray(FX_POLICY_NS + "groups")
                    .stream()
                    .map(jv -> ((JsonString) jv.asJsonObject().get(VALUE)).getString())
                    .toList();
        } catch (Exception ex) {
            throw new InvalidRequestException(ex.getMessage());
        }
    }
}
