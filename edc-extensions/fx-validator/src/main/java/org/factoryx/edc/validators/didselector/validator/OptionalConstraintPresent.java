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

package org.factoryx.edc.validators.didselector.validator;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import org.eclipse.edc.validator.jsonobject.JsonLdPath;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.eclipse.edc.validator.spi.Violation;

import java.util.ArrayList;
import java.util.List;

import static org.factoryx.edc.validators.didselector.IncorrectDidPolicyValidator.DID_WEB;

public class OptionalConstraintPresent implements Validator<JsonObject> {
    private final JsonLdPath path;
    private final String constraintLeftOperand;

    public OptionalConstraintPresent(JsonLdPath path, String constraintLeftOperand) {
        this.path = path;
        this.constraintLeftOperand = constraintLeftOperand;
    }

    @Override
    public ValidationResult validate(JsonObject input) {
        JsonArray permissions = input.getJsonArray("permission");
        List<Violation> violation = new ArrayList<>();
        for (JsonValue permission : permissions) {
            JsonObject constraint = permission.asJsonObject().get("constraint").asJsonObject();
            if (constraint.getString("leftOperand").equalsIgnoreCase(constraintLeftOperand)) {
                return new StartsWith(path.append("constraint").append(constraintLeftOperand), DID_WEB).validate(constraint.getJsonString("rightOperand"));
            }
            if (violation.isEmpty()) {
                violation.add(Violation.violation(String.format("No constraint with left Operand as %s", constraintLeftOperand), path.toString()));
            }
        }
        if (violation.isEmpty()) {
            violation.add(Violation.violation("No permission constraints added", path.toString()));
        }
        return ValidationResult.failure(violation);
    }
}
