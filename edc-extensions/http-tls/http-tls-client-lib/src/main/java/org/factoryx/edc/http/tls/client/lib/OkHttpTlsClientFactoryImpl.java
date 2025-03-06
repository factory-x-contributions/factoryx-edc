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

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Base64;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class OkHttpTlsClientFactoryImpl implements OkHttpTlsClientFactory {

    @Override
    public OkHttpClient create(OkHttpClient baseClient, HttpTlsConfiguration configuration) {

        try {
            KeyManagerFactory keyManagerFactory = getKeyManagerFactory(configuration);
            X509TrustManager trustManager = getEmptyTrustManager();

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), new TrustManager[]{trustManager}, null);
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return baseClient.newBuilder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();

        } catch (Exception ex) {
            throw new IllegalStateException("Unable to configure keystore for http tls client", ex);
        }
    }

    protected KeyManagerFactory getKeyManagerFactory(HttpTlsConfiguration configuration) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(configuration.getCertificateType());
        byte[] decodedCertificateContent = Base64.getDecoder().decode(configuration.getCertificateContent());
        keyStore.load(new ByteArrayInputStream(decodedCertificateContent), configuration.getCertificatePassword().toCharArray());

        KeyManagerFactory factory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        factory.init(keyStore, configuration.getCertificatePassword().toCharArray());
        return factory;
    }

    protected X509TrustManager getEmptyTrustManager() throws Exception {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore) null);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager trustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:" +
                    Arrays.toString(trustManagers));
        }
        return trustManager;
    }
}
