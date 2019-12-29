package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.*
import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.testing.DataLayerMocks.categoryEntity
import com.aradipatrik.testing.DataLayerMocks.joinedTransactionEntity
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.testing.DomainLayerMocks.category
import com.aradipatrik.testing.DomainLayerMocks.long
import com.aradipatrik.testing.DomainLayerMocks.transaction
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
    private val transactionMapper =
        JoinedTransactionMapper(mockCategoryMapper)
    private val testTimestamp = long()

    @Before
    fun setup() {
        mockkObject(TimestampProvider)
        every { TimestampProvider.now() } returns testTimestamp
    }

    @Test
    fun mapFromEntityMapsData() {
        val testEntity = joinedTransactionEntity()
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

    private fun assertEqualsDomainEntity(domain: Transaction, joinedEntity: TransactionJoinedEntity) {
        expectThat(domain.amount).isEqualTo(joinedEntity.amount)
        expectThat(domain.date).isEqualTo(joinedEntity.date)
        expectThat(domain.id).isEqualTo(joinedEntity.id)
        expectThat(domain.memo).isEqualTo(joinedEntity.memo)
    }
}
