package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.PartialTransactionMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.model.TransactionWithIdsDataModel
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
            get(TransactionWithIdsDataModel::amount).isEqualTo(transaction.amount)
            get(TransactionWithIdsDataModel::categoryId).isEqualTo(transaction.category.id)
            get(TransactionWithIdsDataModel::date).isEqualTo(transaction.date)
            get(TransactionWithIdsDataModel::id).isEqualTo(transaction.id)
            get(TransactionWithIdsDataModel::memo).isEqualTo(transaction.memo)
            get(TransactionWithIdsDataModel::walletId).isEqualTo(transaction.walletId)
            get(TransactionWithIdsDataModel::syncStatus).isEqualTo(SyncStatus.None)
            get(TransactionWithIdsDataModel::updatedTimeStamp).isEqualTo(timestamp)
        }
    }
}
