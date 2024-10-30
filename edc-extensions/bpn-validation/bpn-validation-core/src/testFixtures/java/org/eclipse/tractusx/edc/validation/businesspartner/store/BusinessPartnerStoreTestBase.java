/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
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

package org.eclipse.tractusx.edc.validation.businesspartner.store;

import org.eclipse.tractusx.edc.validation.businesspartner.spi.BusinessPartnerStore;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class BusinessPartnerStoreTestBase {

    @Test
    void resolveForBpn() {
        getStore().save("test-bpn", List.of("group1", "group2", "group3"));
        assertThat(getStore().resolveForBpn("test-bpn").getContent()).containsExactly("group1", "group2", "group3");
    }

    @Test
    void resolveForBpn_notExists() {
        assertThat(getStore().resolveForBpn("test-bpn").succeeded()).isFalse();
    }

    @Test
    void resolveForBpn_existsNoGroups() {
        getStore().save("test-bpn", List.of());
        assertThat(getStore().resolveForBpn("test-bpn").getContent()).isNotNull().isEmpty();
    }

    @Test
    void save() {
        getStore().save("test-bpn", List.of("group1", "group2", "group3"));
        assertThat(getStore().resolveForBpn("test-bpn").getContent()).containsExactly("group1", "group2", "group3");
    }

    @Test
    void save_exists() {
        getStore().save("test-bpn", List.of("group1", "group2", "group3"));
        assertThat(getStore().save("test-bpn", List.of("group4")).succeeded()).isFalse();
    }

    @Test
    void delete() {
        var businessPartnerNumber = "test-bpn";
        getStore().save(businessPartnerNumber, List.of("group1", "group2", "group3"));
        var delete = getStore().delete(businessPartnerNumber);
        assertThat(delete.succeeded()).withFailMessage(delete::getFailureDetail).isTrue();
    }

    @Test
    void delete_notExist() {
        var businessPartnerNumber = "test-bpn";
        getStore().delete(businessPartnerNumber);
        assertThat(getStore().resolveForBpn(businessPartnerNumber).succeeded()).isFalse();
    }

    @Test
    void update() {
        var businessPartnerNumber = "test-bpn";
        getStore().save(businessPartnerNumber, List.of("group1", "group2", "group3"));
        assertThat(getStore().update(businessPartnerNumber, List.of("group4", "group5")).succeeded()).isTrue();
    }

    @Test
    void update_notExists() {
        assertThat(getStore().update("test-bpn", List.of("foo", "bar")).succeeded()).isFalse();
    }

    protected abstract BusinessPartnerStore getStore();
}
