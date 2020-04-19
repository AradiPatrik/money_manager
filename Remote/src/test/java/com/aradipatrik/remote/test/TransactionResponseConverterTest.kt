package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.remote.*
import com.aradipatrik.remote.payloadfactory.TransactionResponseConverter
import com.aradipatrik.testing.CommonMocks.boolean
import com.aradipatrik.testing.CommonMocks.long
import com.aradipatrik.testing.CommonMocks.string
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.joda.time.DateTime
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.math.abs

class TransactionResponseConverterTest {
    @Test
    fun `Convert correct response, should map correctly`() {
        val mockTimestamp = 80000000
        val converter = TransactionResponseConverter()
        val mockId = string()
        val deleted = boolean()
        val memo = string()
        val date = DateTime.now()
        val millis = date.millis
        val updateTimestamp = Timestamp(DateTime(abs(mockTimestamp).toLong()).toDate())
        val categoryId = string()
        val amount = long()
        val walletId = string()
        val result = converter.mapResponseToEntity(
            mockk {
                every { id } returns mockId
                every { getString(MEMO_KEY) } returns memo
                every { getBoolean(DELETED_KEY) } returns deleted
                every { getLong(DATE_KEY) } returns millis
                every { getTimestamp(UPDATED_TIMESTAMP_KEY) } returns updateTimestamp
                every { getString(CATEGORY_ID_KEY) } returns categoryId
                every { getLong(AMOUNT_KEY) } returns amount
                every { getString(WALLET_ID_KEY) } returns walletId
            }
        )

        expectThat(result) {
            get(TransactionWithIdsDataModel::updatedTimeStamp).isEqualTo(updateTimestamp.toDate().time)
            get(TransactionWithIdsDataModel::id).isEqualTo(mockId)
            get(TransactionWithIdsDataModel::categoryId).isEqualTo(categoryId)
            get(TransactionWithIdsDataModel::memo).isEqualTo(memo)
            get(TransactionWithIdsDataModel::date).isEqualTo(date)
            get(TransactionWithIdsDataModel::amount).isEqualTo(amount.toInt())
            get(TransactionWithIdsDataModel::syncStatus).isEqualTo(
                if (deleted) {
                    SyncStatus.ToDelete
                } else {
                    SyncStatus.Synced
                }
            )
            get(TransactionWithIdsDataModel::walletId).isEqualTo(walletId)
        }
    }
}
