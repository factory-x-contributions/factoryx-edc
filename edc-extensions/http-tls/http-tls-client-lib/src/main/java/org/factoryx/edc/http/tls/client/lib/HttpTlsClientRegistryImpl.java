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
import org.factoryx.edc.http.tls.client.lib.client.spi.HttpTlsClientRegistry;

import java.util.HashMap;
import java.util.Map;

public class HttpTlsClientRegistryImpl implements HttpTlsClientRegistry {

    private final Map<String, OkHttpClient> clientMap = new HashMap<>();

    private final OkHttpClient defaultHttpClient;

    public HttpTlsClientRegistryImpl(OkHttpClient defaultHttpClient) {
        this.defaultHttpClient = defaultHttpClient;
    }

    @Override
    public void register(String type, OkHttpClient okHttpClient) {
        clientMap.put(type, okHttpClient);
    }

    @Override
    public OkHttpClient clientFor(String type) {
        return clientMap.getOrDefault(type, defaultHttpClient);
    }
}
