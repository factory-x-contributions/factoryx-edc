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

package org.factoryx.edc.data.plane.http.tls.pipeline;

import okhttp3.HttpUrl;
import org.eclipse.edc.connector.dataplane.http.params.HttpRequestFactory;
import org.eclipse.edc.connector.dataplane.http.pipeline.HttpDataSource;
import org.eclipse.edc.connector.dataplane.http.spi.HttpDataAddress;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParamsProvider;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSource;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataSourceFactory;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;
import org.factoryx.edc.http.tls.data.address.HttpTlsDataAddressSchema;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Instantiates {@link org.eclipse.edc.connector.dataplane.http.pipeline.HttpDataSource}s for requests whose source data type is {@link HttpTlsDataAddressSchema#HTTP_TLS_DATA_TYPE}.
 */
public class HttpTlsDataSourceFactory implements DataSourceFactory {

    private final HttpTlsClientRegistry httpTlsClientRegistry;
    private final HttpRequestParamsProvider requestParamsProvider;
    private final Monitor monitor;
    private final HttpRequestFactory requestFactory;

    public HttpTlsDataSourceFactory(HttpTlsClientRegistry httpTlsClientRegistry, HttpRequestParamsProvider requestParamsProvider, Monitor monitor, HttpRequestFactory requestFactory) {
        this.httpTlsClientRegistry = httpTlsClientRegistry;
        this.requestParamsProvider = requestParamsProvider;
        this.monitor = monitor;
        this.requestFactory = requestFactory;
    }

    @Override
    public String supportedType() {
        return HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE;
    }

    @Override
    public @NotNull Result<Void> validateRequest(DataFlowStartMessage request) {
        try {
            createSource(request);
        } catch (Exception e) {
            return Result.failure("Failed to build HttpTlsDataSource: " + e.getMessage());
        }
        return Result.success();
    }

    @Override
    public DataSource createSource(DataFlowStartMessage request) {
        var dataAddress = HttpDataAddress.Builder.newInstance()
                .copyFrom(request.getSourceDataAddress())
                .build();
        var tlsHost = Optional.ofNullable(dataAddress.getBaseUrl())
                .map(HttpUrl::parse)
                .map(HttpUrl::host)
                .orElseThrow(() -> new EdcException("'baseUrl' property is missing in HttpDataAddress"));
        return HttpDataSource.Builder.newInstance()
                .httpClient(httpTlsClientRegistry.clientFor(tlsHost))
                .monitor(monitor)
                .requestId(request.getId())
                .name(dataAddress.getName())
                .params(requestParamsProvider.provideSourceParams(request))
                .requestFactory(requestFactory)
                .build();
    }
}
