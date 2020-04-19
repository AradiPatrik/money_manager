package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryDataModel
import com.aradipatrik.remote.CATEGORY_NAME_KEY
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.ICON_ID_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLET_ID_KEY
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.testing.CommonMocks.boolean
import com.aradipatrik.testing.CommonMocks.string
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockk
import org.joda.time.DateTime
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.math.abs

class CategoryResponseConverterTest {
    @Test
    fun `Convert correct response, should map correctly`() {
        val mockTimestamp = 80000000
        val converter = CategoryResponseConverter()
        val mockId = string()
        val deleted = boolean()
        val name = string()
        val iconId = string()
        val walletId = string()
        val updateTimestamp = Timestamp(DateTime(abs(mockTimestamp).toLong()).toDate())
        val result = converter.mapResponseToEntity(
            mockk {
                every { id } returns mockId
                every { getString(CATEGORY_NAME_KEY) } returns name
                every { getString(ICON_ID_KEY) } returns iconId
                every { getBoolean(DELETED_KEY) } returns deleted
                every { getTimestamp(UPDATED_TIMESTAMP_KEY) } returns updateTimestamp
                every { getString(WALLET_ID_KEY) } returns walletId
            }
        )

        expectThat(result) {
            get(CategoryDataModel::updatedTimeStamp).isEqualTo(updateTimestamp.toDate().time)
            get(CategoryDataModel::id).isEqualTo(mockId)
            get(CategoryDataModel::name).isEqualTo(name)
            get(CategoryDataModel::iconId).isEqualTo(iconId)
            get(CategoryDataModel::syncStatus).isEqualTo(
                if (deleted) {
                    SyncStatus.ToDelete
                } else {
                    SyncStatus.Synced
                }
            )
            get(CategoryDataModel::walletId).isEqualTo(walletId)
        }
    }
}
