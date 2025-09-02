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

package org.factoryx.edc.core.spi;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @Test
    void testIsBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank("   "));
        assertFalse(StringUtils.isBlank("hello"));
    }

    @Test
    void testIsNotBlank() {
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank("   "));
        assertTrue(StringUtils.isNotBlank("hello"));
    }

    @Test
    void testIsEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty("  "));
        assertFalse(StringUtils.isEmpty("hello"));
    }

    @Test
    void testIsNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty("  "));
        assertTrue(StringUtils.isNotEmpty("hello"));
    }

    @Test
    void testIsAnyBlank() {
        assertFalse(StringUtils.isAnyBlank((String[]) null));
        assertFalse(StringUtils.isAnyBlank());
        assertTrue(StringUtils.isAnyBlank("abc", null, "xyz"));
        assertTrue(StringUtils.isAnyBlank("abc", "", "xyz"));
        assertTrue(StringUtils.isAnyBlank("abc", "   ", "xyz"));
        assertFalse(StringUtils.isAnyBlank("abc", "def"));
    }

    @Test
    void testIsNoneBlank() {
        assertTrue(StringUtils.isNoneBlank((String[]) null));
        assertTrue(StringUtils.isNoneBlank());
        assertFalse(StringUtils.isNoneBlank("abc", "   "));
        assertTrue(StringUtils.isNoneBlank("abc", "def", "ghi"));

    }
}
