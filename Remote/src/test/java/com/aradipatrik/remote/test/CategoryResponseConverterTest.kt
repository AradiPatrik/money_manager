package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.remote.CATEGORY_NAME_KEY
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.ICON_ID_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.payloadfactory.CategoryResponseConverter
import com.aradipatrik.testing.DomainLayerMocks.boolean
import com.aradipatrik.testing.DomainLayerMocks.string
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
        val updateTimestamp = Timestamp(DateTime(abs(mockTimestamp).toLong()).toDate())
        val result = converter.mapResponseToEntity(
            mockk {
                every { id } returns mockId
                every { getString(CATEGORY_NAME_KEY) } returns name
                every { getString(ICON_ID_KEY) } returns iconId
                every { getBoolean(DELETED_KEY) } returns deleted
                every { getTimestamp(UPDATED_TIMESTAMP_KEY) } returns updateTimestamp
            }
        )

        expectThat(result) {
            get(CategoryEntity::updatedTimeStamp).isEqualTo(updateTimestamp.toDate().time)
            get(CategoryEntity::id).isEqualTo(mockId)
            get(CategoryEntity::name).isEqualTo(name)
            get(CategoryEntity::iconId).isEqualTo(iconId)
            get(CategoryEntity::syncStatus).isEqualTo(
                if(deleted) {
                    SyncStatus.ToDelete
                } else {
                    SyncStatus.Synced
                }
            )
        }
    }
}
