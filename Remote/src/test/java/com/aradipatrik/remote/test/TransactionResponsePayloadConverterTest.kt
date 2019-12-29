package com.aradipatrik.remote.test

import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.remote.*
import com.aradipatrik.remote.payloadfactory.TransactionResponsePayloadConverter
import com.aradipatrik.testing.DomainLayerMocks.boolean
import com.aradipatrik.testing.DomainLayerMocks.long
import com.aradipatrik.testing.DomainLayerMocks.string
import io.mockk.every
import io.mockk.mockk
import org.joda.time.DateTime
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TransactionResponsePayloadConverterTest {
    @Test
    fun `Convert correct response, should map correctly`() {
        val converter = TransactionResponsePayloadConverter()
        val mockId = string()
        val deleted = boolean()
        val memo = string()
        val date = DateTime()
        val millis = DateTime().millis
        val updateTimestamp = long()
        val categoryId = string()
        val result = converter.mapResponseToEntity(
            mockk {
                every { id } returns mockId
                every { getString(MEMO_KEY) } returns memo
                every { getBoolean(DELETED_KEY) } returns deleted
                every { getLong(DATE_KEY) } returns millis
                every { getLong(UPDATED_TIMESTAMP_KEY) } returns updateTimestamp
                every { getString(CATEGORY_ID_KEY) } returns categoryId
            }
        )
        expectThat(result) {
            get(TransactionPartialEntity::updatedTimeStamp).isEqualTo(updateTimestamp)
            get(TransactionPartialEntity::id).isEqualTo(mockId)
            get(TransactionPartialEntity::categoryId).isEqualTo(categoryId)
            get(TransactionPartialEntity::memo).isEqualTo(memo)
            get(TransactionPartialEntity::date).isEqualTo(date)
            get(TransactionPartialEntity::).isEqualTo(updateTimestamp)
        }
    }
}
