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

package org.factoryx.edc.http.tls.client.lib.client.spi;

public class HttpTlsConfiguration {

    private String tlsHost;
    private String certificateType;


    // base64 encoded certificate file content as string
    private String certificateContent;
    private String certificatePassword;

    public String getTlsHost() {
        return tlsHost;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public String getCertificateContent() {
        return certificateContent;
    }

    public String getCertificatePassword() {
        return certificatePassword;
    }


    public static class Builder {
        private final HttpTlsConfiguration config;

        private Builder() {
            config = new HttpTlsConfiguration();
        }

        public static Builder newInstance() {
            return new Builder();
        }

        public Builder tlsHost(String tlsHost) {
            config.tlsHost = tlsHost;
            return this;
        }

        public Builder certificateType(String certificateType) {
            config.certificateType = certificateType;
            return this;
        }

        public Builder certificateContent(String certificateContent) {
            config.certificateContent = certificateContent;
            return this;
        }

        public Builder certificatePassword(String certificatePassword) {
            config.certificatePassword = certificatePassword;
            return this;
        }

        public HttpTlsConfiguration build() {
            return config;
        }
    }
}
