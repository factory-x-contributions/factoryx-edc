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

package org.factoryx.edc.http.tls.client.lib.client;

import dev.failsafe.RetryPolicy;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.eclipse.edc.http.client.EdcHttpClientImpl;
import org.eclipse.edc.http.spi.EdcHttpClient;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.system.configuration.Config;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsConfiguration;
import org.factoryx.edc.http.tls.client.lib.client.spi.OkHttpTlsClientFactory;

import java.util.Optional;
import java.util.stream.Stream;

public class HttpTlsClientExtension implements ServiceExtension {

    public static final String TLS_HOST = "host";
    public static final String CERTIFICATE_TYPE = "certificate.type";
    public static final String CERTIFICATE_CONTENT = "certificate.content";
    public static final String CERTIFICATE_PASSWORD = "certificate.password";

    public static final String FX_EDC_HTTP_TLS_PREFIX = "fx.edc.http.tls";

    @Inject
    private Vault vault;

    @Inject
    private EdcHttpClient baseEdcHttpClient;
    @Inject
    private OkHttpClient baseHttpClient;

    @Inject
    private OkHttpTlsClientFactory httpTlsClientFactory;

    @Inject
    private HttpTlsClientRegistry httpTlsClientRegistry;

    @Inject
    private Monitor monitor;

    @Inject
    private RetryPolicy<Response> retryPolicy;

    @Override
    public void initialize(ServiceExtensionContext context) {

        context.getConfig(FX_EDC_HTTP_TLS_PREFIX).partition().forEach(config -> {

            monitor.debug("Initializing TLS configuration for '%s'".formatted(config.currentNode()));
            var tlsConfiguration = getTlsConfiguration(config);
            tlsConfiguration.ifPresent((configuration -> {
                OkHttpClient httpClient = httpTlsClientFactory.create(baseHttpClient, tlsConfiguration.get());
                httpTlsClientRegistry.register(configuration.getTlsHost(), createEdcHttpClient(httpClient, context));
            }));
        });
    }

    private Optional<HttpTlsConfiguration> getTlsConfiguration(Config config) {
        var tlsId = config.currentNode();
        var rootPath = "%s.%s".formatted(FX_EDC_HTTP_TLS_PREFIX, tlsId);

        var tlsHost = getSecretOrSetting(rootPath, TLS_HOST, config);
        var certificateType = getSecretOrSetting(rootPath, CERTIFICATE_TYPE, config);
        var certificateContent = getSecretOrSetting(rootPath, CERTIFICATE_CONTENT, config);
        var certificatePassword = getSecretOrSetting(rootPath, CERTIFICATE_PASSWORD, config);

        //var decodedCertificateContent = Base64.getDecoder().decode(certificateContent.get());

        boolean allPresent = Stream.of(tlsHost, certificateType, certificateContent, certificatePassword).allMatch(Optional::isPresent);
        if (allPresent) {
            return Optional.of(HttpTlsConfiguration.Builder.newInstance()
                    .tlsHost(tlsHost.get())
                    .certificateType(certificateType.get())
                    .certificateContent(certificateContent.get())
                    .certificatePassword(certificatePassword.get())
                    .build());

        } else {
            monitor.warning("Skipping TLS Configuration for '%s' due to missing config(s)".formatted(tlsId));
            return Optional.empty();
        }
    }

    private Optional<String> getSecretOrSetting(String rootPath, String key, Config config) {
        var fullKey = "%s.%s".formatted(rootPath, key);
        return Optional.ofNullable(vault.resolveSecret(fullKey))
                .or(() -> {
                    monitor.warning("TLS configuration value '%s' not found in vault, will fall back to Config. Please consider putting TLS configuration into the vault.".formatted(fullKey));
                    return Optional.ofNullable(config.getString(key, null));
                });
    }

    private EdcHttpClient createEdcHttpClient(OkHttpClient okHttpClient, ServiceExtensionContext context) {
        return new EdcHttpClientImpl(
                okHttpClient,
                retryPolicy,
                context.getMonitor()
        );
    }
}
