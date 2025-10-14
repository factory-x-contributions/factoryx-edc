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
    `java-test-fixtures`
}

dependencies {
    testFixturesApi(libs.eclipse.tractusx.spi.core)
    testFixturesApi(libs.edc.lib.token)
    testFixturesApi(libs.edc.spi.jsonld)
    testFixturesApi(libs.edc.spi.identitytrust)
    testFixturesApi(libs.edc.spi.identity.did)
    testFixturesApi(libs.edc.lib.cryptocommon)
    testFixturesApi(testFixtures(libs.edc.api.management.test.fixtures))
    testFixturesApi(libs.edc.junit)

    testFixturesApi(libs.restAssured)
    testFixturesApi(libs.testcontainers.postgres)
}

// do not publish
edcBuild {
    publish.set(false)
}
