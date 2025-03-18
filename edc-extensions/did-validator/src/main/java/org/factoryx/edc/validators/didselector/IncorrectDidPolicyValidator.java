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

import org.eclipse.edc.spi.query.CriterionOperatorRegistry;
import org.eclipse.edc.validator.jsonobject.JsonObjectValidator;
import org.eclipse.edc.validator.jsonobject.validators.OptionalIdNotBlank;
import org.eclipse.edc.validator.jsonobject.validators.model.CriterionValidator;
import org.factoryx.edc.validators.didselector.validator.IdStartsWith;

import static org.eclipse.edc.connector.controlplane.policy.spi.PolicyDefinition.EDC_POLICY_DEFINITION_PRIVATE_PROPERTIES;

public class IncorrectDidPolicyValidator {

    public static final String DID_WEB = "did:web:";

    public static JsonObjectValidator instance(CriterionOperatorRegistry criterionOperatorRegistry) {
        return JsonObjectValidator.newValidator()
                .verifyId(OptionalIdNotBlank::new)
                .verifyId((path) -> new IdStartsWith(path, DID_WEB))
                .verifyArrayItem(EDC_POLICY_DEFINITION_PRIVATE_PROPERTIES, path -> CriterionValidator.instance(path, criterionOperatorRegistry))
                .build();
    }
}
