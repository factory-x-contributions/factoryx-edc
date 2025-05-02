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

import org.eclipse.edc.connector.dataplane.http.params.HttpRequestFactory;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParamsProvider;
import org.eclipse.edc.connector.dataplane.spi.Endpoint;
import org.eclipse.edc.connector.dataplane.spi.iam.PublicEndpointGeneratorService;
import org.eclipse.edc.connector.dataplane.spi.pipeline.DataTransferExecutorServiceContainer;
import org.eclipse.edc.connector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.edc.runtime.metamodel.annotation.Configuration;
import org.eclipse.edc.runtime.metamodel.annotation.Extension;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.runtime.metamodel.annotation.Setting;
import org.eclipse.edc.runtime.metamodel.annotation.Settings;
import org.eclipse.edc.spi.system.Hostname;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.factoryx.edc.data.plane.http.tls.pipeline.HttpTlsDataSinkFactory;
import org.factoryx.edc.data.plane.http.tls.pipeline.HttpTlsDataSourceFactory;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;

import static org.factoryx.edc.data.plane.http.tls.DataPlaneHttpTlsExtension.NAME;
import static org.factoryx.edc.http.tls.data.address.HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE;

@Extension(value = NAME)
public class DataPlaneHttpTlsExtension implements ServiceExtension {
    public static final String NAME = "Data Plane HTTP TLS";
    private static final int DEFAULT_PARTITION_SIZE = 5;

    @Configuration
    private PublicApiConfiguration apiConfiguration;
    @Setting(description = "Base url of the public API endpoint without the trailing slash. This should point to the public endpoint configured.",
            required = false,
            key = "edc.dataplane.api.public.baseurl", warnOnMissingConfig = true)
    private String publicBaseUrl;
    @Setting(description = "Optional base url of the response channel endpoint without the trailing slash. A common practice is to use <PUBLIC_ENDPOINT>/responseChannel", key = "edc.dataplane.api.public.response.baseurl", required = false)
    private String publicApiResponseUrl;
    @Setting(description = "Number of partitions for parallel message push in the HttpDataSink", defaultValue = DEFAULT_PARTITION_SIZE + "", key = "edc.dataplane.http.sink.partition.size")
    private int partitionSize;

    @Inject
    private HttpRequestParamsProvider paramsProvider;

    @Inject
    private HttpTlsClientRegistry httpTlsClientRegistry;

    @Inject
    private PipelineService pipelineService;

    @Inject
    private DataTransferExecutorServiceContainer executorContainer;

    @Inject
    private PublicEndpointGeneratorService generatorService;

    @Inject
    private Hostname hostname;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void initialize(ServiceExtensionContext context) {
        var monitor = context.getMonitor();
        var httpRequestFactory = new HttpRequestFactory();

        var sourceFactory = new HttpTlsDataSourceFactory(httpTlsClientRegistry, paramsProvider, monitor, httpRequestFactory);
        pipelineService.registerFactory(sourceFactory);

        var sinkFactory = new HttpTlsDataSinkFactory(httpTlsClientRegistry, executorContainer.getExecutorService(), partitionSize, monitor, paramsProvider, httpRequestFactory);
        pipelineService.registerFactory(sinkFactory);

        // gene
        if (publicBaseUrl == null) {
            publicBaseUrl = "http://%s:%d%s".formatted(hostname.get(), apiConfiguration.port(), apiConfiguration.path());
            context.getMonitor().warning("The public API endpoint was not explicitly configured, the default '%s' will be used.".formatted(publicBaseUrl));
        }
        var endpoint = Endpoint.url(publicBaseUrl);
        generatorService.addGeneratorFunction(HTTP_TLS_DATA_TYPE, dataAddress -> endpoint);

        if (publicApiResponseUrl != null) {
            generatorService.addGeneratorFunction(HTTP_TLS_DATA_TYPE, () -> Endpoint.url(publicApiResponseUrl));
        }
    }

    @Settings
    record PublicApiConfiguration(
            @Setting(key = "web.http.public.port", description = "Port for public api context", defaultValue = "8185")
            int port,
            @Setting(key = "web.http.public.path", description = "Path for public api context", defaultValue = "/api/public")
            String path
    ) {

    }

}
