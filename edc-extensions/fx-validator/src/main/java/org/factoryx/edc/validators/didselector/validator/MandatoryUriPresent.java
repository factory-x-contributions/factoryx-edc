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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class MandatoryUriPresent implements Validator<JsonString> {
    private final JsonLdPath path;

    public MandatoryUriPresent(JsonLdPath path) {
        this.path = path;
    }

    public ValidationResult validate(JsonString id) {
        return id != null && !id.getString().isBlank() && isValidUrl(id.getString()) ? ValidationResult.success() : ValidationResult.failure(Violation.violation(String.format("%s should be a correct URI", this.path), this.path.toString()));
    }

    boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}