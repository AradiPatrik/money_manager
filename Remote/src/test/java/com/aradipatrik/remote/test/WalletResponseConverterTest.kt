package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLET_NAME_KEY
import com.aradipatrik.remote.payloadfactory.WalletResponseConverter
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

class WalletResponseConverterTest {
    @Test
    fun `Test mapping`() {
        val mockTimestamp = 80000000
        val converter = WalletResponseConverter()
        val mockId = string()
        val deleted = boolean()
        val name = string()
        val updateTimestamp = Timestamp(DateTime(abs(mockTimestamp).toLong()).toDate())

        val result = converter.mapResponseToEntity(
            mockk {
                every { id } returns mockId
                every { getString(WALLET_NAME_KEY) } returns name
                every { getBoolean(DELETED_KEY) } returns deleted
                every { getTimestamp(UPDATED_TIMESTAMP_KEY) } returns updateTimestamp
            }
        )

        expectThat(result) {
            get(WalletDataModel::updatedTimeStamp).isEqualTo(updateTimestamp.toDate().time)
            get(WalletDataModel::id).isEqualTo(mockId)
            get(WalletDataModel::name).isEqualTo(name)
            get(WalletDataModel::syncStatus).isEqualTo(
                if (deleted) {
                    SyncStatus.ToDelete
                } else {
                    SyncStatus.Synced
                }
            )
        }
    }
}
