package org.factoryx.edc.validators.didselector.validator;

import jakarta.json.JsonString;
import org.eclipse.edc.validator.jsonobject.JsonLdPath;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.eclipse.edc.validator.spi.Violation;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MandatoryURIPresent implements Validator<JsonString> {
    private final JsonLdPath path;

    public MandatoryURIPresent(JsonLdPath path) {
        this.path = path;
    }

    public ValidationResult validate(JsonString id) {
        return id != null && !id.getString().isBlank() && isValidURL(id.getString()) ? ValidationResult.success() : ValidationResult.failure(Violation.violation(String.format("%s should be a correct URI", this.path), this.path.toString()));
    }

    boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}