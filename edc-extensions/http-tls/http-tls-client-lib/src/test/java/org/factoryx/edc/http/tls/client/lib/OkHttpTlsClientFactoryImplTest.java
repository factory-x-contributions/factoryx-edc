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

import okhttp3.OkHttpClient;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsConfiguration;
import org.factoryx.edc.http.tls.client.lib.client.spi.OkHttpTlsClientFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class OkHttpTlsClientFactoryImplTest {

    private OkHttpTlsClientFactory httpTlsClientFactory;

    @BeforeEach
    void setup() {
        httpTlsClientFactory = new OkHttpTlsClientFactoryImpl();
    }

    @Test
    void create_client_success() throws IOException {

        HttpTlsConfiguration configuration = HttpTlsConfiguration.Builder.newInstance()
                .tlsHost("example.com")
                .certificateType("PKCS12")
                .certificateContent(getCertificateContentFromFile())
                .certificatePassword("password")
                .build();

        OkHttpClient httpClient = httpTlsClientFactory.create(configuration);
        assertThat(httpClient).isNotNull();
    }

    @Test
    void create_client_failure_wrong_password() throws IOException {

        HttpTlsConfiguration configuration = HttpTlsConfiguration.Builder.newInstance()
                .tlsHost("example.com")
                .certificateType("PKCS12")
                .certificateContent(getCertificateContentFromFile())
                .certificatePassword("incorrect-password")
                .build();

        assertThatThrownBy(() -> httpTlsClientFactory.create(configuration)).isInstanceOf(IllegalStateException.class);
    }

    private String getCertificateContentFromFile() throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("test-certificate.pfx")) {
            if (inputStream != null) {
                return Base64.getEncoder().encodeToString(inputStream.readAllBytes());
            }
        }
        throw new IllegalStateException("Certificate not found");
    }
}
