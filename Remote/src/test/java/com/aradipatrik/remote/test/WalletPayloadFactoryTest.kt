package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mocks.DataLayerMocks.walletDataModel
import com.aradipatrik.remote.CATEGORY_NAME_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.payloadfactory.WalletPayloadFactory
import com.google.firebase.Timestamp
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.hasEntry
import strikt.assertions.isA

class WalletPayloadFactoryTest {
    @Test
    fun `Test mapping`() {
        val entity = walletDataModel(syncStatus = SyncStatus.Synced)
        val payload = WalletPayloadFactory().createPayloadFrom(entity)
        expectThat(payload)
            .hasEntry(CATEGORY_NAME_KEY, entity.name)
        expectThat(payload[UPDATED_TIMESTAMP_KEY])
            .isA<Timestamp>()
    }
}
