/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.http.tls.client.lib.client;

import okhttp3.OkHttpClient;
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.eclipse.edc.spi.system.configuration.ConfigFactory;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsConfiguration;
import org.factoryx.edc.http.tls.client.lib.client.spi.OkHttpTlsClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(DependencyInjectionExtension.class)
class HttpTlsClientExtensionTest {

    private final Vault vault = mock();

    private final OkHttpClient baseHttpClient = mock();

    private final OkHttpTlsClientFactory httpTlsClientFactory = mock();

    private final HttpTlsClientRegistry httpTlsClientRegistry = mock();

    private final Monitor monitor = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(Vault.class, vault);
        context.registerService(OkHttpClient.class, baseHttpClient);
        context.registerService(OkHttpTlsClientFactory.class, httpTlsClientFactory);
        context.registerService(HttpTlsClientRegistry.class, httpTlsClientRegistry);
        context.registerService(Monitor.class, monitor);
    }

    @Test
    void testInitialize(ServiceExtensionContext context, HttpTlsClientExtension extension) {


        OkHttpClient exampleComClient = new OkHttpClient.Builder().build();
        OkHttpClient factoryxComClient = new OkHttpClient.Builder().build();

        when(context.getConfig("fx.edc.http.tls")).thenReturn(getConfig());
        when(httpTlsClientFactory.create(eq(baseHttpClient), any(HttpTlsConfiguration.class))).thenReturn(exampleComClient, factoryxComClient);

        extension.initialize(context);

        verify(httpTlsClientFactory, times(2)).create(eq(baseHttpClient), any(HttpTlsConfiguration.class));
        verify(httpTlsClientRegistry).register(eq("example.com"), any(EdcHttpClient.class));
        verify(httpTlsClientRegistry).register(eq("factoryx.com"), any(EdcHttpClient.class));
    }

    Config getConfig() {
        Config config = ConfigFactory.fromMap(Map.of(
                "fx.edc.http.tls.example.host", "example.com",
                "fx.edc.http.tls.example.certificate.type", "PKCS12",
                "fx.edc.http.tls.example.certificate.content", "Base 64 encoded certificate file content",
                "fx.edc.http.tls.example.certificate.password", "certificate file password",
                "fx.edc.http.tls.factoryx.host", "factoryx.com",
                "fx.edc.http.tls.factoryx.certificate.type", "PKCS12",
                "fx.edc.http.tls.factoryx.certificate.content", "Base 64 encoded certificate file content",
                "fx.edc.http.tls.factoryx.certificate.password", "certificate file password"
        ));
        return config.getConfig("fx.edc.http.tls");
    }
}
