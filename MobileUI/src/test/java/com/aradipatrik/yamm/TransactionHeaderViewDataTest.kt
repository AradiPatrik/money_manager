package com.aradipatrik.yamm

import com.aradipatrik.yamm.features.history.model.TransactionHeaderViewData
import org.joda.time.LocalDate
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TransactionHeaderViewDataTest {
    @Test
    fun `TransactionHeaderViewData should have correct string representation`() {
        // Arrange
        val desiredStringRepresentation = "2019-01-28"
        val date = LocalDate(2019, 1, 28)

        // Act
        val headerViewData = TransactionHeaderViewData(date)

        // Assert
        expectThat(headerViewData.asFormattedString).isEqualTo(desiredStringRepresentation)
    }
}