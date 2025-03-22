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

import jakarta.json.JsonString;
import org.eclipse.edc.validator.jsonobject.JsonLdPath;
import org.eclipse.edc.validator.spi.ValidationResult;
import org.eclipse.edc.validator.spi.Validator;
import org.eclipse.edc.validator.spi.Violation;

public class StartsWith implements Validator<JsonString> {
    private final JsonLdPath path;
    private final String startsWith;

    public StartsWith(JsonLdPath path, String startsWith) {
        this.path = path;
        this.startsWith = startsWith;
    }

    @Override
    public ValidationResult validate(JsonString jsonObject) {
        return jsonObject.getString().startsWith(startsWith) ? ValidationResult.success() : ValidationResult.failure(Violation.violation(String.format("%s should start with %s", this.path, this.startsWith), this.path.toString()));
    }
}

