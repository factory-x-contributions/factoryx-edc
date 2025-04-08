/********************************************************************************
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

package org.factoryx.edc.api.did.validation;

import jakarta.json.JsonString;
import org.eclipse.edc.validator.jsonobject.JsonLdPath;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.MandatoryArray;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.eclipse.edc.validator.spi.Violation;

import static org.factoryx.edc.api.did.BusinessPartnerDidSchema.DID_GROUP_TYPE;


public class BusinessPartnerDidValidator {

    public static JsonObjectValidator instance() {
        return JsonObjectValidator.newValidator()
                .verifyId(DidValidator::new)
                .verify(DID_GROUP_TYPE, MandatoryArray.min(1))
                .build();
    }

    public static class DidValidator implements Validator<JsonString> {

        private final JsonLdPath path;

        public DidValidator(JsonLdPath path) {
            this.path = path;
        }

        @Override
        public ValidationResult validate(JsonString input) {
            return input != null && isValidDid(input.getString()) ? ValidationResult.success() : ValidationResult.failure(Violation.violation("%s should be a valid did".formatted(input), this.path.toString()));
        }

        private boolean isValidDid(String did) {
            return !isBlank(did) && startsWithDidWeb(did) && !endsWithColon(did);
        }

        private boolean isBlank(String did) {
            return did == null || did.isBlank();
        }

        private boolean endsWithColon(String did) {
            return did.endsWith(":");
        }

        private boolean startsWithDidWeb(String did) {
            return did.startsWith("did:web:");
        }
    }
}
