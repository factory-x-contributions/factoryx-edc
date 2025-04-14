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

import org.assertj.core.api.Assertions;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.spi.ValidationFailure;
import org.eclipse.edc.validator.spi.Violation;
import org.junit.jupiter.api.Test;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;

class AssetTypeValidatorTest {

    private final JsonObjectValidator validator = AssetTypeValidator.instance();

    @Test
    void shouldPass_whenAssetDefinitionIsCorrect() {

        var assetDefinition = createObjectBuilder()
                .add(ID, "asset-id")
                .add(AssetTypeValidator.TYPE, createArrayBuilder().add(createObjectBuilder().add(ID, "http://www.w3.org/ns/odrl.jsonld")))
                .build();

        System.out.println(assetDefinition.toString());

        var result = validator.validate(assetDefinition);

        assertThat(result).isSucceeded();
    }

    @Test
    void shouldFail_whenMandatoryFieldsAreMissing() {
        var assetDefinition = createObjectBuilder().build();

        var result = validator.validate(assetDefinition);

        assertThat(result).isFailed().extracting(ValidationFailure::getViolations).asInstanceOf(list(Violation.class))
                .isNotEmpty()
                .anySatisfy(violation -> Assertions.assertThat(violation.path()).isEqualTo(ID));
    }

    @Test
    void shouldFail_whenIdIsBlank() {
        var assetDefinition = createObjectBuilder()
                .add(ID, " ")
                .build();

        var result = validator.validate(assetDefinition);

        assertThat(result).isFailed().extracting(ValidationFailure::getViolations).asInstanceOf(list(Violation.class))
                .isNotEmpty()
                .filteredOn(it -> ID.equals(it.path()))
                .anySatisfy(violation -> Assertions.assertThat(violation.message()).contains("blank"));
    }

}