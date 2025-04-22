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

plugins {
    `java-library`
}

dependencies {
    implementation(libs.edc.spi.core)
    implementation(libs.edc.dataplane.http)
    implementation(libs.edc.dataplane.util)
    implementation(libs.edc.validator.data.address.http.data)
    implementation(libs.edc.spi.validator)
    implementation(project(":spi:http-tls:http-tls-client-spi"))
    implementation(project(":spi:http-tls:http-tls-data-address-spi"))

    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.dataplane.core)
    testImplementation(libs.mockserver.netty)
    testImplementation(libs.edc.spi.controlplane.api.client)
    testImplementation(project(":edc-extensions:http-tls:http-tls-client"))
    testImplementation(project(":edc-extensions:http-tls:http-tls-client-lib"))
}
