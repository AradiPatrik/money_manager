package com.aradipatrik.presentation

import com.aradipatrik.domain.mocks.DomainLayerMocks.category
import com.aradipatrik.domain.mocks.DomainLayerMocks.transaction
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentations.CategoryPresentationModel
import com.aradipatrik.presentation.presentations.TransactionPresentationModel

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class PresentationMappingTest {
    @Test
    fun testMapping() {
        val categoryMapper =
            CategoryPresentationMapper()
        val transactionMapper =
            TransactionPresentationMapper(
                categoryMapper
            )
        val testCategory = category()
        val testCategoryPresentation =
            CategoryPresentationModel(
                testCategory.id, testCategory.name, testCategory.iconId
            )
        val testTransaction = transaction(category = testCategory)
        val testTransactionPresentation =
            TransactionPresentationModel(
                testTransaction.id, testTransaction.amount,
                testCategoryPresentation, testTransaction.date, testTransaction.memo
            )

        val domainResult = transactionMapper.mapFromPresentation(testTransactionPresentation)
        val presentationResult = transactionMapper.mapToPresentation(testTransaction)

        expectThat(presentationResult).isEqualTo(testTransactionPresentation)
        expectThat(domainResult).isEqualTo(testTransaction)
    }
}