/********************************************************************************
 * Copyright (c) 2024 T-Systems International GmbH
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
    id(libs.plugins.swagger.get().pluginId)
}

dependencies {
    runtimeOnly(libs.eclipse.tractusx.edc.controlplane.base) {
        exclude("org.eclipse.tractusx.edc", "cx-policy")
        exclude("org.eclipse.tractusx.edc", "tx-dcp")
        exclude("org.eclipse.tractusx.edc", "bdrs-client")
        exclude("org.eclipse.tractusx.edc", "data-flow-properties-provider")
        exclude("org.eclipse.tractusx.edc", "bpn-validation")
    }

    // fx-edc extensions
    runtimeOnly(project(":edc-extensions:fx-json-ld-core"))
    runtimeOnly(project(":edc-extensions:contract-validation"))
    runtimeOnly(project(":edc-extensions:data-flow-properties-provider"))
    runtimeOnly(project(":edc-extensions:mqtt"))
    // Credentials FX policies
    runtimeOnly(project(":edc-extensions:fx-policy"))

    // needed for DCP integration
    runtimeOnly(project(":edc-extensions:dcp:fx-dcp"))
    runtimeOnly(project(":edc-extensions:did-validation"))
}
