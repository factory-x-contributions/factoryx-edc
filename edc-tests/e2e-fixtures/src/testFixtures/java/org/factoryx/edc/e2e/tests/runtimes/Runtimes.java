/********************************************************************************
 * Copyright (c) 2025 Cofinity-X GmbH
 * Copyright (c) 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.e2e.tests.runtimes;

import org.eclipse.edc.junit.extensions.EmbeddedRuntime;
import org.eclipse.edc.junit.extensions.RuntimeExtension;
import org.eclipse.edc.spi.iam.AudienceResolver;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.factoryx.edc.e2e.tests.MockVcIdentityService;
import org.factoryx.edc.e2e.tests.participant.TractusxParticipantBase;

import java.util.function.Supplier;


public interface Runtimes {

    static RuntimeExtension pgRuntime(TractusxParticipantBase participant, PostgresExtension postgres) {
        return pgRuntime(participant, postgres, ConfigFactory::empty);
    }

    static RuntimeExtension pgRuntime(TractusxParticipantBase participant, PostgresExtension postgres, Supplier<Config> configurationProvider) {

        return new ParticipantRuntimeExtension(
                new EmbeddedRuntime(participant.getName(), ":edc-tests:runtime:runtime-postgresql")
                        .configurationProvider(() -> participant.getConfig().merge(postgres.getConfig(participant.getName())))
                        .configurationProvider(configurationProvider)
                        .registerServiceMock(IdentityService.class, new MockVcIdentityService(participant.getDid()))
                        .registerServiceMock(AudienceResolver.class, remoteMessage -> Result.success(remoteMessage.getCounterPartyAddress()))
        );
    }
}
