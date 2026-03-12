/*
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.json.JsonObject;
import org.eclipse.edc.web.spi.ApiErrorDetail;

import java.util.Set;

import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;

/**
 * Business Partner DID Group API version 3.
 * Provides endpoints for managing DID to group mappings and querying groups.
 */
@OpenAPIDefinition(info = @Info(description = "With this API clients can create, read, update and delete BusinessPartnerDID groups. It allows the assigning of DIDs to groups.", title = "Business Partner DID Group API"))
@Tag(name = "Business Partner DID Group")
public interface BusinessPartnerDidGroupApiV3 {


    /**
     * Resolves all groups for a particular DID.
     *
     * @param did the business partner DID
     * @return JSON object containing the DID and its groups
     */
    @Operation(description = "Resolves all groups for a particular DID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "An object containing an array with the assigned groups"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    JsonObject resolveV3(@Parameter(name = "did", description = "The business partner did") String did);

    /**
     * Resolves all DIDs for a particular DID group.
     *
     * @param group the business partner DID group
     * @return JSON object containing the group and its associated DIDs
     */
    @Operation(description = "Resolves all DIDs for a particular DID group",
            responses = {
                    @ApiResponse(responseCode = "200", description = "An object containing an array with the dids assigned to the group"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID group was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    JsonObject resolveGroupV3(@Parameter(name = "group", description = "The business partner did group") String group);

    /**
     * Resolves all DID Groups.
     *
     * @return JSON object containing all DID groups
     */
    @Operation(description = "Resolves all DID Groups",
            responses = {
                    @ApiResponse(responseCode = "200", description = "An object containing an array with the all DID groups"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    JsonObject resolveGroupsV3();

    /**
     * Deletes the entry for a particular DID.
     *
     * @param did the business partner DID
     */
    @Operation(description = "Deletes the entry for a particular DID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "The object was successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    void deleteEntryV3(@Parameter(name = "did", description = "The business partner did") String did);

    /**
     * Updates the entry for a particular DID.
     *
     * @param object JSON object containing the DID and its groups
     */
    @Operation(description = "Updates the entry for a particular DID",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ListSchema.class))),

            responses = {
                    @ApiResponse(responseCode = "204", description = "The object was successfully updated"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    void updateEntryV3(JsonObject object);

    /**
     * Creates an entry for a particular DID.
     *
     * @param entry JSON object containing the DID and its groups
     */
    @Operation(description = "Creates an entry for a particular DID",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ListSchema.class))),

            responses = {
                    @ApiResponse(responseCode = "204", description = "The object was successfully created"),
                    @ApiResponse(responseCode = "409", description = "An entry already exists for that DID"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    void createEntryV3(JsonObject entry);


    /**
     * Schema for list representation in API responses.
     *
     * @param id the DID identifier
     * @param groups the set of groups associated with the DID
     */
    @Schema(name = "List", example = ListSchema.EXAMPLE)
    record ListSchema(
            @Schema(name = ID) String id,
            Set<String> groups
    ) {
        /**
         * Example JSON representation of a ListSchema.
         */
        public static final String EXAMPLE = """
                {
                    "@context": {
                        "fx": "https://w3id.org/factoryx/v0.0.1/ns/"
                    },
                    "@id": "did:web:example.com:participantA",
                    "fx:groups": ["group1", "group2", "group3"]
                }
                """;
    }
}
