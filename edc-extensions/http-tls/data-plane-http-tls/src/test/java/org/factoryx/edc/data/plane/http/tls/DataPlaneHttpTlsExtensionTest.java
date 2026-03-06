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

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParamsProvider;
import org.eclipse.edc.connector.dataplane.spi.pipeline.PipelineService;
import org.eclipse.edc.connector.dataplane.spi.pipeline.StreamResult;
import org.eclipse.edc.connector.dataplane.spi.port.TransferProcessApiClient;
import org.eclipse.edc.dataaddress.httpdata.spi.HttpDataAddressSchema;
import org.eclipse.edc.junit.annotations.ComponentTest;
import org.eclipse.edc.junit.extensions.RuntimeExtension;
import org.eclipse.edc.junit.extensions.RuntimePerClassExtension;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.factoryx.edc.http.tls.data.address.HttpTlsDataAddressSchema;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static java.util.Collections.emptyMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.util.io.Ports.getFreePort;
import static org.mockito.Mockito.mock;

@ComponentTest
public class DataPlaneHttpTlsExtensionTest {

    private static WireMockServer sourceServer;
    private static WireMockServer destinationServer;
    private static final int SOURCE_PORT = getFreePort();
    private static final int DESTINATION_PORT = getFreePort();

    @RegisterExtension
    private static final RuntimeExtension RUNTIME = new RuntimePerClassExtension()
            .registerServiceMock(TransferProcessApiClient.class, mock());

    @BeforeAll
    public static void setUp() {
        sourceServer = new WireMockServer(WireMockConfiguration.options().port(SOURCE_PORT));
        destinationServer = new WireMockServer(WireMockConfiguration.options().port(DESTINATION_PORT));
        sourceServer.start();
        destinationServer.start();
        WireMock.configureFor("localhost", SOURCE_PORT);
        WireMock.configureFor("localhost", DESTINATION_PORT);
    }

    @AfterAll
    public static void tearDown() {
        sourceServer.stop();
        destinationServer.stop();
    }

    @Test
    void transferSourceToDestination(PipelineService pipelineService) {
        var source = DataAddress.Builder.newInstance()
                .type(HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE)
                .property(HttpDataAddressSchema.BASE_URL, "http://localhost:" + SOURCE_PORT)
                .build();
        var destination = DataAddress.Builder.newInstance()
                .type(HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE)
                .property(HttpDataAddressSchema.BASE_URL, "http://localhost:" + DESTINATION_PORT)
                .build();
        sourceServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withStatus(200)));
        destinationServer.stubFor(post(urlMatching(".*")).willReturn(aResponse().withStatus(200)));

        var request = DataFlowStartMessage.Builder.newInstance()
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .destinationDataAddress(destination)
                .traceContext(emptyMap())
                .build();

        var future = pipelineService.transfer(request);

        assertThat(future).succeedsWithin(10, SECONDS)
                .matches(StreamResult::succeeded);
        sourceServer.verify(getRequestedFor(urlMatching(".*")));
        destinationServer.verify(postRequestedFor(urlMatching(".*")));
    }

    @Test
    void transferSourceToDestinationAddHeaders(PipelineService pipelineService, HttpRequestParamsProvider paramsProvider) {
        paramsProvider.registerSourceDecorator((request, address, builder) -> builder.header("customSourceHeader", "customValue"));
        paramsProvider.registerSinkDecorator((request, address, builder) -> builder.header("customSinkHeader", "customValue"));
        var source = DataAddress.Builder.newInstance()
                .type(HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE)
                .property(HttpDataAddressSchema.BASE_URL, "http://localhost:" + SOURCE_PORT)
                .build();
        var destination = DataAddress.Builder.newInstance()
                .type(HttpTlsDataAddressSchema.HTTP_TLS_DATA_TYPE)
                .property(HttpDataAddressSchema.BASE_URL, "http://localhost:" + DESTINATION_PORT)
                .build();
        sourceServer.stubFor(get(urlMatching(".*")).willReturn(aResponse().withStatus(200)));
        destinationServer.stubFor(post(urlMatching(".*")).willReturn(aResponse().withStatus(200)));

        var request = DataFlowStartMessage.Builder.newInstance()
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(source)
                .destinationDataAddress(destination)
                .traceContext(emptyMap())
                .build();

        var future = pipelineService.transfer(request);

        assertThat(future).succeedsWithin(10, SECONDS)
                .matches(StreamResult::succeeded);
        sourceServer.verify(getRequestedFor(urlMatching(".*")).withHeader("customSourceHeader", equalTo("customValue")));
        destinationServer.verify(postRequestedFor(urlMatching(".*")).withHeader("customSinkHeader", equalTo("customValue")));
    }
}
