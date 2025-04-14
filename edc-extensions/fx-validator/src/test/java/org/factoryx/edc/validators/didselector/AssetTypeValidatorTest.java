package org.factoryx.edc.validators.didselector;

import jakarta.json.JsonArrayBuilder;
import org.assertj.core.api.Assertions;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.spi.ValidationFailure;
import org.eclipse.edc.validator.spi.Violation;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static jakarta.json.Json.createArrayBuilder;
import static jakarta.json.Json.createObjectBuilder;
import static org.assertj.core.api.InstanceOfAssertFactories.list;
import static org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition.EDC_POLICY_DEFINITION_TYPE;
import static org.eclipse.edc.jsonld.spi.JsonLdKeywords.*;
import static org.eclipse.edc.junit.assertions.AbstractResultAssert.assertThat;
import static org.eclipse.edc.spi.constants.CoreConstants.EDC_NAMESPACE;
import static org.junit.jupiter.api.Assertions.*;

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