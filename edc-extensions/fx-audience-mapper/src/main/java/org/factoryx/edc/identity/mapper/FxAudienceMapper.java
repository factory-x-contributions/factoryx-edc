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

import org.eclipse.edc.spi.iam.AudienceResolver;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

import java.util.Optional;

/**
 * An incoming {@link RemoteMessage} is mapped to a DID by calling {@link RemoteMessage#getCounterPartyId()}
 */
class FxAudienceMapper implements AudienceResolver {

    @Override
    public Result<String> resolve(RemoteMessage remoteMessage) {
        return Result.from(Optional.ofNullable(remoteMessage.getCounterPartyId()));
    }

}
