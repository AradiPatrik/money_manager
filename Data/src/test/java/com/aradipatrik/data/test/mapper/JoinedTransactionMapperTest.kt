package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.JoinedTransactionMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.mocks.DataLayerMocks.categoryEntity
import com.aradipatrik.data.mocks.DataLayerMocks.transactionWithCategory
import com.aradipatrik.data.model.TransactionWithCategoryDataModel
import com.aradipatrik.domain.mocks.DomainLayerMocks.category
import com.aradipatrik.domain.mocks.DomainLayerMocks.transaction
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.testing.CommonMocks.long

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class JoinedTransactionMapperTest {
    private val testCategory = category()
    private val testCategoryEntity = categoryEntity()
    private val mockCategoryMapper = mockk<CategoryMapper> {
        every { mapFromEntity(any()) } returns testCategory
        every { mapToEntity(any()) } returns testCategoryEntity
    }
    private val transactionMapper = JoinedTransactionMapper(mockCategoryMapper)
    private val testTimestamp = long()

    @Before
    fun setup() {
        mockkObject(TimestampProvider)
        every { TimestampProvider.now() } returns testTimestamp
    }

    @Test
    fun mapFromEntityMapsData() {
        val testEntity = transactionWithCategory()
        val domain = transactionMapper.mapFromEntity(testEntity)
        assertEqualsDomainEntity(domain, testEntity)
        expectThat(domain.category).isEqualTo(testCategory)
    }

    @Test
    fun mapToEntityMapsData() {
        val testDomain = transaction()
        val entity = transactionMapper.mapToEntity(testDomain)
        assertEqualsDomainEntity(testDomain, entity)
        expectThat(entity.category).isEqualTo(testCategoryEntity)
        expectThat(entity.updatedTimeStamp).isEqualTo(testTimestamp)
        expectThat(entity.syncStatus).isEqualTo(SyncStatus.None)
        expectThat(entity.walletId).isEqualTo(testDomain.walletId)
    }

    private fun assertEqualsDomainEntity(
        domain: Transaction,
        withCategoryDataModel: TransactionWithCategoryDataModel
    ) {
        expectThat(domain.amount).isEqualTo(withCategoryDataModel.amount)
        expectThat(domain.date).isEqualTo(withCategoryDataModel.date)
        expectThat(domain.id).isEqualTo(withCategoryDataModel.id)
        expectThat(domain.memo).isEqualTo(withCategoryDataModel.memo)
        expectThat(domain.walletId).isEqualTo(withCategoryDataModel.walletId)
    }
}
