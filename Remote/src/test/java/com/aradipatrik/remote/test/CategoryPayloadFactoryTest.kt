package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.remote.*
import com.aradipatrik.remote.payloadfactory.CategoryPayloadFactory
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.testing.DataLayerMocks
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
import com.google.firebase.Timestamp
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.hasEntry
import strikt.assertions.isA

class CategoryPayloadFactoryTest {
    @Test
    fun `Create payload for should create a hashmap containing correct common keys and values`() {
        val entity = categoryEntity()
        val payload = CategoryPayloadFactory().createPayloadFrom(entity)
        expectThat(payload)
            .hasEntry(ICON_ID_KEY, entity.iconId)
            .hasEntry(CATEGORY_NAME_KEY, entity.name)
        expectThat(payload[UPDATED_TIMESTAMP_KEY])
            .isA<Timestamp>()
    }

    @Test
    fun `Sync status deleted`() {
        val entity = categoryEntity(syncStatus = SyncStatus.ToDelete)
        val payload = CategoryPayloadFactory().createPayloadFrom(entity)
        expectThat(payload).hasEntry(DELETED_KEY, true)
    }

    @Test
    fun `Sync status not deleted`() {
        val entity = categoryEntity(syncStatus = SyncStatus.None)
        val payload = CategoryPayloadFactory().createPayloadFrom(entity)
        expectThat(payload).hasEntry(DELETED_KEY, false)
    }
}