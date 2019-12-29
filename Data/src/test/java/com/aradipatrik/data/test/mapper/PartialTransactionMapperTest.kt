package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.testing.DomainLayerMocks.long
import com.aradipatrik.testing.DomainLayerMocks.transaction
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PartialTransactionMapperTest {
    @Test
    fun testToEntity() {
        mockkObject(TimestampProvider)
        val timestamp = long()
        every { TimestampProvider.now() } returns timestamp
        val transaction = transaction()
        val mapper = PartialTransactionMapper()
        val entity = mapper.mapToEntity(transaction)
        expectThat(entity) {
            get(TransactionPartialEntity::amount).isEqualTo(transaction.amount)
            get(TransactionPartialEntity::categoryId).isEqualTo(transaction.category.id)
            get(TransactionPartialEntity::date).isEqualTo(transaction.date)
            get(TransactionPartialEntity::id).isEqualTo(transaction.id)
            get(TransactionPartialEntity::memo).isEqualTo(transaction.memo)
            get(TransactionPartialEntity::syncStatus).isEqualTo(SyncStatus.None)
            get(TransactionPartialEntity::updatedTimeStamp).isEqualTo(timestamp)
        }
    }
}