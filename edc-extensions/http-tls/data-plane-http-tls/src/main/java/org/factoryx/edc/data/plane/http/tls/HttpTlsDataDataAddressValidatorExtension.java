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

package org.factoryx.edc.data.plane.http.tls;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.validator.dataaddress.httpdata.HttpDataDataAddressValidator;
import org.eclipse.edc.validator.spi.DataAddressValidatorRegistry;
import org.factoryx.edc.http.tls.data.address.HttpTlsDataAddressSchema;

import static org.factoryx.edc.data.plane.http.tls.HttpTlsDataDataAddressValidatorExtension.NAME;

@Extension(NAME)
public class HttpTlsDataDataAddressValidatorExtension implements ServiceExtension {
    public static final String NAME = "HttpTlsData DataAddress Validator";

    @Inject
    private DataAddressValidatorRegistry dataAddressValidatorRegistry;

    @Override
    public void initialize(ServiceExtensionContext context) {
        var validator = new HttpDataDataAddressValidator();
        dataAddressValidatorRegistry.registerSourceValidator(HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE, validator);
        dataAddressValidatorRegistry.registerDestinationValidator(HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE, validator);
    }
}
