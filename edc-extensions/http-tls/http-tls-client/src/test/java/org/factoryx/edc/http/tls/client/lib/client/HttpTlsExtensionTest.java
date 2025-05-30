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
import org.eclipse.edc.junit.extensions.DependencyInjectionExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.factoryx.edc.http.tls.client.lib.HttpTlsClientRegistryImpl;
import org.factoryx.edc.http.tls.client.lib.OkHttpTlsClientFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;


@ExtendWith(DependencyInjectionExtension.class)
class HttpTlsExtensionTest {

    private final OkHttpClient baseHttpClient = mock();

    @BeforeEach
    void setup(ServiceExtensionContext context) {
        context.registerService(OkHttpClient.class, baseHttpClient);
    }

    @Test
    void testProviders(ServiceExtensionContext context, HttpTlsExtension extension) {

        assertThat(extension.httpTlsClientRegistry()).isInstanceOf(HttpTlsClientRegistryImpl.class);
        assertThat(extension.okHttpTlsClientFactory()).isInstanceOf(OkHttpTlsClientFactoryImpl.class);
    }

}
