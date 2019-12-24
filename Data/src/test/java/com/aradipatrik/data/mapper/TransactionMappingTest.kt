package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.test.MockDataFactory.categoryEntity
import com.aradipatrik.data.test.MockDataFactory.transactionEntity
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.test.MockDataFactory.category
import com.aradipatrik.domain.test.MockDataFactory.long
import com.aradipatrik.domain.test.MockDataFactory.transaction
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
    private val transactionMapper = TransactionMapper(mockCategoryMapper)
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
        expectThat(entity.isDeleted).isFalse()
    }

    private fun assertEqualsDomainEntity(domain: Transaction, entity: TransactionEntity) {
        expectThat(domain.amount).isEqualTo(entity.amount)
        expectThat(domain.date).isEqualTo(entity.date)
        expectThat(domain.id).isEqualTo(entity.id)
        expectThat(domain.memo).isEqualTo(entity.memo)
    }
}