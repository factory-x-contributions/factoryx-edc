package org.factoryx.edc.validators.didselector;

import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryIdNotBlank;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryObject;
import org.factoryx.edc.validators.didselector.validator.MandatoryURIPresent;

public class AssetTypeValidator {

    public static final String TYPE = "http://purl.org/dc/terms/type";

    public static JsonObjectValidator instance() {
        return JsonObjectValidator.newValidator()
                .verifyId(MandatoryIdNotBlank::new)
                .verify(TYPE, MandatoryObject::new)
                .verifyObject(TYPE, v -> v.verifyId(MandatoryURIPresent::new))
                .build();
    }
}
