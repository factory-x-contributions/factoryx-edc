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
import okhttp3.Request;
import org.eclipse.edc.connector.dataplane.http.params.HttpRequestFactory;
import org.eclipse.edc.connector.dataplane.http.pipeline.HttpDataSink;
import org.eclipse.edc.connector.dataplane.http.spi.HttpDataAddress;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParams;
import org.eclipse.edc.connector.dataplane.http.spi.HttpRequestParamsProvider;
import org.eclipse.edc.connector.dataplane.spi.pipeline.InputStreamDataSource;
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.domain.DataAddress;
import org.eclipse.edc.spi.types.domain.transfer.DataFlowStartMessage;
import org.factoryx.edc.http.tls.client.lib.HttpTlsClientRegistryImpl;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.factoryx.edc.data.plane.http.tls.pipeline.TestFunctions.createHttpResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HttpTlsDataSinkFactoryTest {

    private final EdcHttpClient httpClient = mock();
    private final HttpTlsClientRegistry clientRegistry = new HttpTlsClientRegistryImpl(httpClient);
    private final Monitor monitor = mock();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final HttpRequestParamsProvider provider = mock();
    private final HttpRequestFactory requestFactory = mock();

    private HttpTlsDataSinkFactory factory;

    @BeforeEach
    void setUp() {
        factory = new HttpTlsDataSinkFactory(clientRegistry, executorService, 5, monitor, provider, requestFactory);
    }

    @Test
    void verifyValidationFailsIfProviderThrows() {
        var errorMsg = "Test error message";
        var address = HttpDataAddress.Builder.newInstance().baseUrl("https://example.com/test").build();
        var request = createRequest(address);
        when(provider.provideSinkParams(request)).thenThrow(new EdcException(errorMsg));

        var result = factory.validateRequest(request);

        assertThat(result.failed()).isTrue();
        assertThat(result.getFailureMessages()).hasSize(1);
        assertThat(result.getFailureMessages().get(0)).contains(errorMsg);
    }

    @Test
    void verifySuccessSinkCreation() {
        var address = HttpDataAddress.Builder.newInstance().baseUrl("https://example.com/test").build();
        var request = createRequest(address);
        var params = HttpRequestParams.Builder.newInstance()
                .baseUrl("https://example.com/test")
                .method("POST")
                .contentType("application/json")
                .build();
        when(provider.provideSinkParams(request)).thenReturn(params);

        assertThat(factory.validateRequest(request).succeeded()).isTrue();
        var sink = factory.createSink(request);
        assertThat(sink).isNotNull();

        var expected = HttpDataSink.Builder.newInstance()
                .params(params)
                .httpClient(httpClient)
                .monitor(monitor)
                .requestId(request.getId())
                .executorService(executorService)
                .requestFactory(requestFactory)
                .build();

        // validate the generated data sink field by field using reflection
        assertThat(sink).usingRecursiveComparison().isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = { "POST", "GET", "PUT" })
    void verifyCreateMethodDestination(String method) throws IOException {
        var address = HttpDataAddress.Builder.newInstance().baseUrl("https://example.com/test").build();
        var request = createRequest(address);
        var params = HttpRequestParams.Builder.newInstance()
                .baseUrl("https://example.com/test")
                .method(method)
                .contentType("application/json")
                .build();
        when(provider.provideSinkParams(request)).thenReturn(params);
        when(requestFactory.toRequest(any(), any())).thenReturn(createHttpRequest());
        when(httpClient.execute(ArgumentMatchers.isA(Request.class))).thenReturn(createHttpResponse().build());

        var sink = factory.createSink(request);

        var future = sink.transfer(new InputStreamDataSource("test", new ByteArrayInputStream("test".getBytes())));

        assertThat(future).succeedsWithin(10, TimeUnit.SECONDS)
                .satisfies(result -> assertThat(result.succeeded()).isTrue());
    }

    private DataFlowStartMessage createRequest(DataAddress destination) {
        return DataFlowStartMessage.Builder.newInstance()
                .id(UUID.randomUUID().toString())
                .processId(UUID.randomUUID().toString())
                .sourceDataAddress(DataAddress.Builder.newInstance().type("test-type").build())
                .destinationDataAddress(destination)
                .build();
    }

    private Request createHttpRequest() {
        return new Request.Builder()
                .url(Objects.requireNonNull(HttpUrl.parse("http://example.com/test")))
                .get()
                .build();
    }
}
