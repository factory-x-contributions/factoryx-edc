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

import io.restassured.specification.RequestSpecification;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.edc.jsonld.TitaniumJsonLd;
import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.spi.result.StoreResult;
import org.eclipse.edc.web.jersey.testfixtures.RestControllerTestBase;
import org.eclipse.tractusx.edc.validation.businesspartner.spi.BusinessPartnerStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.http.ContentType.JSON;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_PREFIX;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public abstract class BaseBusinessPartnerDidGroupApiControllerTest extends RestControllerTestBase {

    private final JsonLd jsonLdService = new TitaniumJsonLd(mock());
    protected final BusinessPartnerStore businessPartnerStore = mock();

    @BeforeEach
    void setUp() {
        jsonLdService.registerNamespace("edc", EDC_NAMESPACE);
        jsonLdService.registerNamespace(FX_POLICY_PREFIX, FX_POLICY_NS);

    }

    @Test
    void resolve() {
        when(businessPartnerStore.resolveForBpn(any())).thenReturn(StoreResult.success(List.of("group1", "group2")));
        baseRequest()
                .get("/test-did")
                .then()
                .statusCode(200)
                .body(notNullValue());
    }

    @Test
    void resolve_exists_noGroups() {
        when(businessPartnerStore.resolveForBpn(any())).thenReturn(StoreResult.success(List.of()));
        baseRequest()
                .get("/test-did")
                .then()
                .statusCode(200);
    }

    @Test
    void resolve_notExists_returns404() {
        when(businessPartnerStore.resolveForBpn(any())).thenReturn(StoreResult.notFound("test-message"));
        baseRequest()
                .get("/test-did")
                .then()
                .statusCode(404);
    }

    @Test
    void deleteEntry() {
        when(businessPartnerStore.delete(anyString())).thenReturn(StoreResult.success());
        baseRequest()
                .delete("/test-did")
                .then()
                .statusCode(204);
    }

    @Test
    void deleteEntry_notExists_returns404() {
        when(businessPartnerStore.delete(anyString())).thenReturn(StoreResult.notFound("test-message"));
        baseRequest()
                .delete("/test-did")
                .then()
                .statusCode(404);
    }

    @Test
    void updateEntry() {
        when(businessPartnerStore.update(anyString(), any())).thenReturn(StoreResult.success());
        baseRequest()
                .contentType(JSON)
                .body(createJsonObject())
                .put()
                .then()
                .statusCode(204);
    }


    @Test
    void updateEntry_notExists_returns404() {
        when(businessPartnerStore.update(anyString(), any())).thenReturn(StoreResult.notFound("test-message"));
        baseRequest()
                .contentType(JSON)
                .body(createJsonObject())
                .put()
                .then()
                .statusCode(404);
    }

    @Test
    void updateEntry_invalidBody_returns400() {
        baseRequest()
                .contentType(JSON)
                .body("{\"invalid-key\": \"invalid-value\"}")
                .put()
                .then()
                .statusCode(400);
    }

    @Test
    void createEntry() {
        when(businessPartnerStore.save(anyString(), any())).thenReturn(StoreResult.success());
        baseRequest()
                .contentType(JSON)
                .body(createJsonObject())
                .post()
                .then()
                .statusCode(204);
    }

    @Test
    void createEntry_alreadyExists_returns409() {
        when(businessPartnerStore.save(anyString(), any())).thenReturn(StoreResult.alreadyExists("test-message"));
        baseRequest()
                .contentType(JSON)
                .body(createJsonObject())
                .post()
                .then()
                .statusCode(409);
    }

    @Test
    void createEntry_invalidBody_returns400() {
        baseRequest()
                .contentType(JSON)
                .body("{\"invalid-key\": \"invalid-value\"}")
                .post()
                .then()
                .statusCode(400);
    }

    @Test
    void createEntry_invalidDid_returns400() {
        baseRequest()
                .contentType(JSON)
                .body(createInvalidDidJsonObject())
                .post()
                .then()
                .statusCode(400);
    }

    protected abstract RequestSpecification baseRequest();

    private JsonObject createJsonObject() {
        return jsonLdService.expand(Json.createObjectBuilder()
                .add(ID, "did:web:example.com:participantA")
                .add(CONTEXT, Json.createObjectBuilder().add(FX_POLICY_PREFIX, FX_POLICY_NS).build())
                .add(FX_POLICY_NS + "groups", String.join(",", "group1", "group2", "group3"))
                .build()).orElseThrow(f -> new RuntimeException(f.getFailureDetail()));
    }

    private JsonObject createInvalidDidJsonObject() {
        return jsonLdService.expand(Json.createObjectBuilder()
                .add(ID, "invalid-did")
                .add(CONTEXT, Json.createObjectBuilder().add(FX_POLICY_PREFIX, FX_POLICY_NS).build())
                .add(FX_POLICY_NS + "groups", String.join(",", "group1", "group2", "group3"))
                .build()).orElseThrow(f -> new RuntimeException(f.getFailureDetail()));
    }

}
