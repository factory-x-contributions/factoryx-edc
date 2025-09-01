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

package org.factoryx.edc.mqtt.data.endpoint.parser;

import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.factoryx.edc.mqtt.data.endpoint.parser.spi.EndpointTypeParser;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType.AuthType;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType.DestinationType;
import org.factoryx.edc.mqtt.data.endpoint.spi.EndpointType.Protocol;

import java.util.Arrays;
import java.util.Optional;

public class EndpointTypeParserImpl implements EndpointTypeParser {

    private final Monitor monitor;

    public EndpointTypeParserImpl(Monitor monitor) {
        this.monitor = monitor;
    }

    @Override
    public Result<EndpointType> parse(String endpointType) {
        Optional<Result<EndpointType>> parsed = Optional.ofNullable(endpointType)
                .map(type -> type.split("-"))
                .filter(tokens -> tokens.length == 3)
                .map(this::parseEndpointType);

        return parsed.orElse(Result.failure("Failed to parse endpoint type %s".formatted(endpointType)));
    }

    protected Result<EndpointType> parseEndpointType(String[] tokens) {

        try {
            DestinationType destinationType = DestinationType.valueOf(tokens[0].toUpperCase());
            Protocol protocol = Protocol.valueOf(tokens[1].toUpperCase());
            AuthType authType = AuthType.valueOf(tokens[2].toUpperCase());
            return Result.success(new EndpointType(destinationType, protocol, authType));
        } catch (Exception ex) {
            String msg = "Failed to parse endpoint type %s".formatted(Arrays.toString(tokens));
            monitor.warning(msg, ex);
            return Result.failure(msg);
        }
    }
}
