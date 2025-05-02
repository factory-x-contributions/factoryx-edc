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

import jakarta.json.JsonArrayBuilder;
import org.assertj.core.api.Assertions;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.spi.ValidationFailure;
import org.eclipse.edc.validator.spi.Violation;
import org.junit.jupiter.api.Test;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition.EDC_POLICY_DEFINITION_TYPE;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.CONTEXT;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.ID;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.TYPE;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VALUE;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.VOCAB;
import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;
import static org.factoryx.edc.validators.didselector.IncorrectDidPolicyValidator.DID_WEB;

class IncorrectDidPolicyValidatorTest {

    private final JsonObjectValidator validator = IncorrectDidPolicyValidator.instance();

    @Test
    void shouldPass_whenPolicyDefinitionIsCorrect() {

        var constraint = createObjectBuilder()
                .add("leftOperand", "BusinessPartnerDID")
                .add("operator", "eq")
                .add("rightOperand", "did:web:mycorp.com:mypath");

        var constraint2 = createObjectBuilder()
                .add("leftOperand", "foo")
                .add("operator", "eq")
                .add("rightOperand", "bar");

        var policyDefinition = createObjectBuilder()
                .add(ID, "validation-success")
                .add(TYPE, EDC_POLICY_DEFINITION_TYPE)
                .add(CONTEXT, createArrayBuilder().add(createObjectBuilder().add("http://www.w3.org/ns/odrl.jsonld", createObjectBuilder().add(VOCAB, EDC_NAMESPACE))))
                .add("policy", createArrayBuilder().add(createObjectBuilder().add("permission",
                        createArrayBuilder().add(createObjectBuilder().add("constraint", constraint2).add("action", value("USE"))).add(createObjectBuilder().add("constraint", constraint).add("action", value("USE")))).add(TYPE, "Set")))
                .build();

        var result = validator.validate(policyDefinition);

        assertThat(result).isSucceeded();
    }

    @Test
    void shouldFail_whenPolicyDefinitionIsIncorrect() {

        var constraint = createObjectBuilder()
                .add("leftOperand", "BusinessPartnerDID")
                .add("operator", "eq")
                .add("rightOperand", "some-arbitrary-id");

        var constraint2 = createObjectBuilder()
                .add("leftOperand", "foo")
                .add("operator", "eq")
                .add("rightOperand", "bar");

        var policyDefinition = createObjectBuilder()
                .add(ID, "validation-failure")
                .add(TYPE, EDC_POLICY_DEFINITION_TYPE)
                .add(CONTEXT, createArrayBuilder().add(createObjectBuilder().add("http://www.w3.org/ns/odrl.jsonld", createObjectBuilder().add(VOCAB, EDC_NAMESPACE))))
                .add("policy", createArrayBuilder().add(createObjectBuilder().add("permission",
                        createArrayBuilder().add(createObjectBuilder().add("constraint", constraint2).add("action", value("USE"))).add(createObjectBuilder().add("constraint", constraint).add("action", value("USE")))).add(TYPE, "Set")))
                .build();


        var result = validator.validate(policyDefinition);

        assertThat(result).isFailed().extracting(ValidationFailure::getViolations).asInstanceOf(list(Violation.class))
                .isNotEmpty()
                .anySatisfy(violation -> Assertions.assertThat(violation.message()).contains(DID_WEB));
    }

    /**
     * Set of tests from upstream PolicyDefinitionValidatorTest that still need to pass here.
     **/

    @Test
    void shouldFail_whenMandatoryFieldsAreMissing() {
        var policyDefinition = createObjectBuilder().build();

        var result = validator.validate(policyDefinition);

        assertThat(result).isFailed().extracting(ValidationFailure::getViolations).asInstanceOf(list(Violation.class))
                .isNotEmpty()
                .anySatisfy(violation -> Assertions.assertThat(violation.path()).isEqualTo(ID));
    }

    @Test
    void shouldFail_whenIdIsBlank() {
        var policyDefinition = createObjectBuilder()
                .add(ID, " ")
                .build();

        var result = validator.validate(policyDefinition);

        assertThat(result).isFailed().extracting(ValidationFailure::getViolations).asInstanceOf(list(Violation.class))
                .isNotEmpty()
                .filteredOn(it -> ID.equals(it.path()))
                .anySatisfy(violation -> Assertions.assertThat(violation.message()).contains("blank"));
    }

    private JsonArrayBuilder value(String value) {
        return createArrayBuilder().add(createObjectBuilder().add(VALUE, value));
    }
}