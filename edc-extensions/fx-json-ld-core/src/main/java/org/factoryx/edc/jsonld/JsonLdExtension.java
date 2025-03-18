/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.factoryx.edc.jsonld;

import org.eclipse.edc.jsonld.spi.JsonLd;
import org.eclipse.edc.runtime.metamodel.annotation.Inject;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.result.Result;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_CONTEXT;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_NS;
import static org.factoryx.edc.edr.spi.CoreConstants.FX_POLICY_PREFIX;

/**
 *  Provides JSON-LD structure for Factory-X policies.
 */
public class JsonLdExtension implements ServiceExtension {

    private static final String PREFIX = "document" + File.separator;
    private static final Map<String, String> FILES = Map.of(
            FX_POLICY_CONTEXT, PREFIX + "fx-policy-v1.jsonld");

    @Inject
    private JsonLd jsonLdService;

    @Inject
    private Monitor monitor;

    @Override
    public void initialize(ServiceExtensionContext context) {
        jsonLdService.registerNamespace(FX_POLICY_PREFIX, FX_POLICY_NS);
        FILES.entrySet().stream().map(this::mapToFile)
                .forEach(result -> result.onSuccess(entry -> jsonLdService.registerCachedDocument(entry.getKey(), entry.getValue().toURI()))
                        .onFailure(failure -> monitor.warning("Failed to register cached json-ld document: " + failure.getFailureDetail())));
    }

    private Result<Map.Entry<String, File>> mapToFile(Map.Entry<String, String> fileEntry) {
        return getResourceFile(fileEntry.getValue())
                .map(file1 -> Map.entry(fileEntry.getKey(), file1));
    }

    @NotNull
    private Result<File> getResourceFile(String name) {
        try (var stream = getClass().getClassLoader().getResourceAsStream(name)) {
            if (stream == null) {
                return Result.failure(format("Cannot find resource %s", name));
            }

            var filename = Path.of(name).getFileName().toString();
            var parts = filename.split("\\.");
            var tempFile = Files.createTempFile(parts[0], "." + parts[1]);
            Files.copy(stream, tempFile, REPLACE_EXISTING);
            return Result.success(tempFile.toFile());
        } catch (Exception e) {
            return Result.failure(format("Cannot read resource %s: ", name));
        }
    }

}
