package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.remote.*
import com.aradipatrik.remote.payloadfactory.TransactionPayloadFactory
import com.aradipatrik.testing.DataLayerMocks.partialTransactionEntity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.hasEntry
import strikt.assertions.isA

class TransactionPayloadFactoryTest {
    @Test
    fun `Create payload for should create a hashmap containing correct common keys and values`() {
        val entity = partialTransactionEntity()
        val payload = TransactionPayloadFactory().createPayloadFrom(entity)
        expectThat(payload)
            .hasEntry(MEMO_KEY, entity.memo)
            .hasEntry(DATE_KEY, entity.date.millis)
            .hasEntry(AMOUNT_KEY, entity.amount)
            .hasEntry(CATEGORY_ID_KEY, entity.categoryId)
        expectThat(payload[UPDATED_TIMESTAMP_KEY])
            .isA<Timestamp>()
    }

    @Test
    fun `Sync status deleted`() {
        val entity = partialTransactionEntity(syncStatus = SyncStatus.ToDelete)
        val payload = TransactionPayloadFactory().createPayloadFrom(entity)
        expectThat(payload).hasEntry(DELETED_KEY, true)
    }

    @Test
    fun `Sync status not deleted`() {
        val entity = partialTransactionEntity(syncStatus = SyncStatus.None)
        val payload = TransactionPayloadFactory().createPayloadFrom(entity)
        expectThat(payload).hasEntry(DELETED_KEY, false)
    }
}
