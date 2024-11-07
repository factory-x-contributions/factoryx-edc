/**
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

// extensions - control plane
include(":edc-extensions:fx-policy")

// test modules
include(":edc-tests:e2e-fixtures")
include(":edc-tests:edc-controlplane:edr-api-tests")
include(":edc-tests:edc-controlplane:catalog-tests")
include(":edc-tests:edc-controlplane:transfer-tests")
include(":edc-tests:edc-controlplane:iatp-tests")
include(":edc-tests:edc-controlplane:policy-tests")
include(":edc-tests:runtime:extensions")
include(":edc-tests:runtime:runtime-memory")
include(":edc-tests:runtime:mock-connector")
include(":edc-tests:runtime:dataplane-cloud")
include(":edc-tests:runtime:runtime-postgresql")
include(":edc-tests:runtime:iatp:runtime-memory-iatp-ih")
include(":edc-tests:runtime:iatp:runtime-memory-iatp-dim-ih")
include(":edc-tests:runtime:iatp:runtime-memory-iatp-dim")
include(":edc-tests:runtime:iatp:runtime-memory-sts")
include(":edc-tests:runtime:iatp:iatp-extensions")

// modules for controlplane artifacts
include(":edc-controlplane")
include(":edc-controlplane:edc-controlplane-base")
include(":edc-controlplane:edc-runtime-memory")
include(":edc-controlplane:edc-controlplane-postgresql-azure-vault")
include(":edc-controlplane:edc-controlplane-postgresql-hashicorp-vault")

// modules for dataplane artifacts
include(":edc-dataplane")
include(":edc-dataplane:edc-dataplane-azure-vault")
include(":edc-dataplane:edc-dataplane-base")
include(":edc-dataplane:edc-dataplane-hashicorp-vault")


// this is needed to have access to snapshot builds of plugins
pluginManagement {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        mavenCentral()
        mavenLocal()
    }
}
