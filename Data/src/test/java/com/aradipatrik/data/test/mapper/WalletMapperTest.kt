package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.mapper.WalletMapper
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.domain.model.Wallet
import com.aradipatrik.testing.CommonMocks.long
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class WalletMapperTest {
    @Test
    fun `mapping should work`() {
        val testTimestamp = long()
        mockkObject(TimestampProvider)
        every { TimestampProvider.now() } returns testTimestamp

        val domain = Wallet("testId", "testName")
        val entity = WalletDataModel("testId", "testName", testTimestamp, SyncStatus.None)

        val mappedDomain = WalletMapper().mapFromEntity(entity)
        val mappedEntity = WalletMapper().mapToEntity(domain)

        expectThat(domain).isEqualTo(mappedDomain)
        expectThat(entity).isEqualTo(mappedEntity)
    }
}