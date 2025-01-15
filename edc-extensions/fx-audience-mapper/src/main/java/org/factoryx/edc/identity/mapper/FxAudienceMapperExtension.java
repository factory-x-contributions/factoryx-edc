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

package org.factoryx.edc.identity.mapper;

import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Provider;
import org.eclipse.edc.spi.iam.AudienceResolver;
import org.eclipse.edc.spi.system.ServiceExtension;

import static org.factoryx.edc.identity.mapper.FxAudienceMapperExtension.NAME;


@Extension(value = NAME)
public class FxAudienceMapperExtension implements ServiceExtension {

    public static final String NAME = "FX Audience Mapper Extension";

    @Override
    public String name() {
        return NAME;
    }

    @Provider
    public AudienceResolver fxAudienceResolver() {
        return new FxAudienceMapper();
    }

}
