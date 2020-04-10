package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.model.TransactionWithIds
import com.aradipatrik.domain.mocks.DomainLayerMocks.transaction
import com.aradipatrik.testing.CommonMocks.long
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
            get(TransactionWithIds::amount).isEqualTo(transaction.amount)
            get(TransactionWithIds::categoryId).isEqualTo(transaction.category.id)
            get(TransactionWithIds::date).isEqualTo(transaction.date)
            get(TransactionWithIds::id).isEqualTo(transaction.id)
            get(TransactionWithIds::memo).isEqualTo(transaction.memo)
            get(TransactionWithIds::walletId).isEqualTo("")
            get(TransactionWithIds::syncStatus).isEqualTo(SyncStatus.None)
            get(TransactionWithIds::updatedTimeStamp).isEqualTo(timestamp)
        }
    }
}
