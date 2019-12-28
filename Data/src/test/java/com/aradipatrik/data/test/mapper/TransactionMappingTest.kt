package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.mapper.TransactionMapper
import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.test.common.MockDataFactory.categoryEntity
import com.aradipatrik.data.test.common.MockDataFactory.transactionEntity
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.testing.MockDomainDataFactory.category
import com.aradipatrik.testing.MockDomainDataFactory.long
import com.aradipatrik.testing.MockDomainDataFactory.transaction
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse

class TransactionMappingTest {
    private val testCategory = category()
    private val testCategoryEntity = categoryEntity()
    private val mockCategoryMapper = mockk<CategoryMapper> {
        every { mapFromEntity(any()) } returns testCategory
        every { mapToEntity(any()) } returns testCategoryEntity
    }
    private val transactionMapper =
        TransactionMapper(mockCategoryMapper)
    private val testTimestamp = long()

    @Before
    fun setup() {
        mockkObject(TimestampProvider)
        every { TimestampProvider.now() } returns testTimestamp
    }

    @Test
    fun mapFromEntityMapsData() {
        val testEntity = transactionEntity()
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
    }

    private fun assertEqualsDomainEntity(domain: Transaction, entity: TransactionEntity) {
        expectThat(domain.amount).isEqualTo(entity.amount)
        expectThat(domain.date).isEqualTo(entity.date)
        expectThat(domain.id).isEqualTo(entity.id)
        expectThat(domain.memo).isEqualTo(entity.memo)
    }
}
