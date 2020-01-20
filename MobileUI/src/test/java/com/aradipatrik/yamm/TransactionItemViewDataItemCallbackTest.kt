package com.aradipatrik.yamm

import com.aradipatrik.testing.DomainLayerMocks.string
import com.aradipatrik.yamm.util.PresentationLayerMocks.transactionPresentation
import com.aradipatrik.yamm.util.ViewDataMocks.transactionViewData
import com.aradipatrik.yamm.features.history.adapter.TransactionViewDataItemCallback
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class TransactionItemViewDataItemCallbackTest {
    @Test
    fun `Items the same when and only when their presentation id is the same`() {
        val sameIdsResult = TransactionViewDataItemCallback.areItemsTheSame(
            transactionViewData(presentationRef = transactionPresentation(id = "A")),
            transactionViewData(presentationRef = transactionPresentation(id = "A"))
        )
        val differentIdsResult = TransactionViewDataItemCallback.areItemsTheSame(
            transactionViewData(presentationRef = transactionPresentation(id = "A")),
            transactionViewData(presentationRef = transactionPresentation(id = "B"))
        )

        expectThat(sameIdsResult).isTrue()
        expectThat(differentIdsResult).isFalse()
    }

    @Test
    fun `Contents the same when and only when contents are deeply equal`() {
        val itemOne = transactionViewData()
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
}