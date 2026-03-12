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
 * Business Partner DID Group API version 1.
 * Provides endpoints for managing DID to group mappings.
 *
 * @deprecated since 0.1.0, use {@link org.factoryx.edc.api.did.v3.BusinessPartnerDidGroupApiV3} instead
 */
@OpenAPIDefinition(info = @Info(description = "With this API clients can create, read, update and delete BusinessPartnerDID groups. It allows the assigning of DIDs to groups.", title = "Business Partner DID Group API"))
@Tag(name = "Business Partner DID Group")
@Deprecated(since = "0.1.0")
public interface BusinessPartnerDidGroupApiV1 {


    /**
     * Resolves all groups for a particular DID.
     *
     * @param did the business partner DID
     * @return JSON object containing the DID and its groups
     * @deprecated since 0.1.0, use v3 API instead
     */
    @Operation(description = "Resolves all groups for a particular DID",
            deprecated = true,
            responses = {
                    @ApiResponse(responseCode = "200", description = "An object containing an array with the assigned groups"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    JsonObject resolveV1(@Parameter(name = "did", description = "The business partner did") String did);

    /**
     * Deletes the entry for a particular DID.
     *
     * @param did the business partner DID
     * @deprecated since 0.1.0, use v3 API instead
     */
    @Operation(description = "Deletes the entry for a particular DID",
            deprecated = true,
            responses = {
                    @ApiResponse(responseCode = "204", description = "The object was successfully deleted"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    void deleteEntryV1(@Parameter(name = "did", description = "The business partner did") String did);

    /**
     * Updates the entry for a particular DID.
     *
     * @param object JSON object containing the DID and its groups
     * @deprecated since 0.1.0, use v3 API instead
     */
    @Operation(description = "Updates the entry for a particular DID",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ListSchema.class))),
            deprecated = true,
            responses = {
                    @ApiResponse(responseCode = "204", description = "The object was successfully updated"),
                    @ApiResponse(responseCode = "404", description = "No entry for the given DID was found"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    void updateEntryV1(JsonObject object);

    /**
     * Creates an entry for a particular DID.
     *
     * @param entry JSON object containing the DID and its groups
     * @deprecated since 0.1.0, use v3 API instead
     */
    @Operation(description = "Creates an entry for a particular DID",
            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = ListSchema.class))),
            deprecated = true,
            responses = {
                    @ApiResponse(responseCode = "204", description = "The object was successfully created"),
                    @ApiResponse(responseCode = "409", description = "An entry already exists for that DID"),
                    @ApiResponse(responseCode = "400", description = "Request body was malformed",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ApiErrorDetail.class))))
            })
    void createEntryV1(JsonObject entry);


    /**
     * Schema for list representation in API responses.
     *
     * @param id the DID identifier
     * @param groups the set of groups associated with the DID
     * @deprecated since 0.1.0, use v3 API instead
     */
    @Schema(name = "List", example = ListSchema.EXAMPLE, deprecated = true)
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
