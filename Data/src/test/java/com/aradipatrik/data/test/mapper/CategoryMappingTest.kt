package com.aradipatrik.data.test.mapper

import com.aradipatrik.data.mapper.CategoryMapper
import com.aradipatrik.data.mapper.TimestampProvider
import com.aradipatrik.data.model.CategoryEntity
import com.aradipatrik.data.test.common.MockDataFactory.categoryEntity
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.test.MockDataFactory.category
import com.aradipatrik.domain.test.MockDataFactory.long
import io.mockk.every
import io.mockk.mockkObject
import org.junit.Before
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse

class CategoryMappingTest {
    private val categoryMapper = CategoryMapper()
    private val testTimestamp = long()

    @Before
    fun setup() {
        mockkObject(TimestampProvider)
        every { TimestampProvider.now() } returns testTimestamp
    }

    @Test
    fun mapToEntityMapsData() {
        val testDomain = category()
        val entity = categoryMapper.mapToEntity(testDomain)
        assertEqualsDomainEntity(testDomain, entity)
        expectThat(entity.lastUpdateTimestamp).isEqualTo(testTimestamp)
        expectThat(entity.isDeleted).isFalse()
    }

    @Test
    fun mapFromEntityMapsData() {
        val testEntity = categoryEntity()
        val domain = categoryMapper.mapFromEntity(testEntity)
        assertEqualsDomainEntity(domain, testEntity)
    }

    private fun assertEqualsDomainEntity(domain: Category, entity: CategoryEntity) {
        expectThat(domain.iconId).isEqualTo(entity.iconId)
        expectThat(domain.id).isEqualTo(entity.id)
        expectThat(domain.name).isEqualTo(entity.name)
    }
}