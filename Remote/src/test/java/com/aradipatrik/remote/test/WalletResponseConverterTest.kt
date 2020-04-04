package com.aradipatrik.remote.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLET_NAME_KEY
import com.aradipatrik.remote.payloadfactory.WalletResponseConverter
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
            get(WalletEntity::updatedTimeStamp).isEqualTo(updateTimestamp.toDate().time)
            get(WalletEntity::id).isEqualTo(mockId)
            get(WalletEntity::name).isEqualTo(name)
            get(WalletEntity::syncStatus).isEqualTo(
                if (deleted) {
                    SyncStatus.ToDelete
                } else {
                    SyncStatus.Synced
                }
            )
        }
    }
}