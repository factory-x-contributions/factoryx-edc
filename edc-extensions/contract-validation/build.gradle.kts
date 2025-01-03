/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

plugins {
    `java-library`
}

dependencies {
    implementation(project(":spi:core-spi"))
    implementation(libs.eclipse.tractusx.coreutils)
    implementation(libs.edc.controlplane.contract)

    testImplementation(libs.edc.junit)
    testImplementation(libs.edc.lib.json)
}
