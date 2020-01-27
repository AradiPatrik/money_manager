package com.aradipatrik.yamm.itemcallback

import com.aradipatrik.testing.DomainLayerMocks.string
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation
import com.aradipatrik.yamm.util.ViewDataMocks.transactionItemViewData
import com.aradipatrik.yamm.features.history.adapter.TransactionViewDataItemCallback
import com.aradipatrik.yamm.features.history.model.TransactionHeaderViewData
import org.joda.time.LocalDate
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class TransactionItemViewDataItemCallbackTest {
    @Test
    fun `Transaction items are the same when their presentation id is the same`() {
        val sameIdsResult = TransactionViewDataItemCallback.areItemsTheSame(
            transactionItemViewData(presentationRef = transactionPresentation(id = "A")),
            transactionItemViewData(presentationRef = transactionPresentation(id = "A"))
        )
        val differentIdsResult = TransactionViewDataItemCallback.areItemsTheSame(
            transactionItemViewData(presentationRef = transactionPresentation(id = "A")),
            transactionItemViewData(presentationRef = transactionPresentation(id = "B"))
        )

        expectThat(sameIdsResult).isTrue()
        expectThat(differentIdsResult).isFalse()
    }

    @Test
    fun `Transaction contents the same when and only when contents are deeply equal`() {
        val itemOne = transactionItemViewData()
        val sameAsItemOne = itemOne.copy()
        val sameAsItemOneButWithDifferentId = itemOne.copy(
            presentationRef = itemOne.presentationRef.copy(id = string())
        )

        val deeplyEqualResult = TransactionViewDataItemCallback.areContentsTheSame(
            itemOne, sameAsItemOne
        )
        val idsAreNotTheSameResult = TransactionViewDataItemCallback.areContentsTheSame(
            itemOne, sameAsItemOneButWithDifferentId
        )

        expectThat(deeplyEqualResult).isTrue()
        expectThat(idsAreNotTheSameResult).isFalse()
    }

    @Test
    fun `Header items are the same when they are corresponding to the same date`() {
        val sameDateResult = TransactionViewDataItemCallback.areItemsTheSame(
            TransactionHeaderViewData(LocalDate(2019, 1, 28)),
            TransactionHeaderViewData(LocalDate(2019, 1, 28))
        )
        val differentDateResult = TransactionViewDataItemCallback.areItemsTheSame(
            TransactionHeaderViewData(LocalDate(2019, 1, 29)),
            TransactionHeaderViewData(LocalDate(2019, 1, 28))
        )

        expectThat(sameDateResult).isTrue()
        expectThat(differentDateResult).isFalse()
    }

    @Test
    fun `Header contents the same when and only when contents are deeply equal`() {
        val deeplyEqualResult = TransactionViewDataItemCallback.areContentsTheSame(
            TransactionHeaderViewData(LocalDate(2019, 1, 28)),
            TransactionHeaderViewData(LocalDate(2019, 1, 28))
        )
        val idsAreNotTheSameResult = TransactionViewDataItemCallback.areContentsTheSame(
            TransactionHeaderViewData(LocalDate(2019, 1, 27)),
            TransactionHeaderViewData(LocalDate(2019, 1, 28))
        )

        expectThat(deeplyEqualResult).isTrue()
        expectThat(idsAreNotTheSameResult).isFalse()
    }

    @Test
    fun `Are items the same should return false, if different type of view data is received`() {
        // Arrange
        val headerItem = TransactionHeaderViewData(LocalDate(2019, 1, 28))
        val transactionItem = transactionItemViewData()

        // Act
        val areItemsTheSameResult = TransactionViewDataItemCallback.areItemsTheSame(
            headerItem, transactionItem
        )

        // Assert
        expectThat(areItemsTheSameResult).isFalse()
    }

    @Test
    fun `Are contents the same should return false if different types received`() {
        // Arrange
        val headerItem = TransactionHeaderViewData(LocalDate(2019, 1, 28))
        val transactionItem = transactionItemViewData()

        // Act
        val areContentsTheSameResult = TransactionViewDataItemCallback.areContentsTheSame(
            headerItem, transactionItem
        )

        // Assert
        expectThat(areContentsTheSameResult).isFalse()
    }
}
