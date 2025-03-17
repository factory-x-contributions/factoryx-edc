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

package org.factoryx.edc.http.tls.client.lib;

import dev.failsafe.RetryPolicy;
import okhttp3.OkHttpClient;
import org.eclipse.edc.http.client.EdcHttpClientImpl;
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.spi.monitor.ConsoleMonitor;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

class HttpTlsClientRegistryImplTest {

    HttpTlsClientRegistry httpTlsClientRegistry;

    EdcHttpClient defaultHttpClient;

    @BeforeEach
    void setup() {
        defaultHttpClient = new EdcHttpClientImpl(new OkHttpClient.Builder().build(), RetryPolicy.ofDefaults(), new ConsoleMonitor());
        httpTlsClientRegistry = new HttpTlsClientRegistryImpl(defaultHttpClient);
    }

    @Test
    void test_register_success() {
        OkHttpClient httpClient = new OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).build();
        EdcHttpClient edcHttpClient = new EdcHttpClientImpl(httpClient, RetryPolicy.ofDefaults(), new ConsoleMonitor());
        httpTlsClientRegistry.register("example.com", edcHttpClient);

        assertThat(httpTlsClientRegistry.clientFor("example.com")).isEqualTo(edcHttpClient);
    }

    @Test
    void test_register_failure() {
        assertThat(httpTlsClientRegistry.clientFor("example.com")).isEqualTo(defaultHttpClient);
    }
}
