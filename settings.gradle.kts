/**
 * Copyright (c) 2024 Contributors to the Factory-X project
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

rootProject.name = "factoryx-edc"

// spi modules
include(":spi:core-spi")
include(":spi:http-tls:http-tls-client-spi")
include(":spi:http-tls:http-tls-data-address-spi")

// extensions - control plane
include(":edc-extensions:fx-json-ld-core")
include(":edc-extensions:fx-policy")
include(":edc-extensions:data-flow-properties-provider")
include(":edc-extensions:dcp:fx-dcp")
include(":edc-extensions:did-validation")
include(":edc-extensions:did-validation:did-validation-core")
include(":edc-extensions:did-validation:did-validation-api")
include(":edc-extensions:fx-validator")
include(":edc-extensions:contract-validation")
include(":edc-extensions:http-tls:http-tls-client")
include(":edc-extensions:http-tls:http-tls-client-lib")
include(":edc-extensions:http-tls:data-plane-http-tls")

// modules for controlplane artifacts
include(":edc-controlplane")
include(":edc-controlplane:edc-controlplane-base")
include(":edc-controlplane:edc-runtime-memory")
include(":edc-controlplane:edc-controlplane-postgresql-hashicorp-vault")

// modules for dataplane artifacts
include(":edc-dataplane")
include(":edc-dataplane:edc-dataplane-base")
include(":edc-dataplane:edc-dataplane-hashicorp-vault")

include(":samples:multi-tenancy")


// this is needed to have access to snapshot builds of plugins
//pluginManagement {
//    repositories {
//        maven {
//            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
//        }
//        mavenCentral()
//        gradlePluginPortal()
//    }
//}

//dependencyResolutionManagement {
//    repositories {
//        maven {
//            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
//        }
//        mavenCentral()
//        mavenLocal()
//    }
//}
