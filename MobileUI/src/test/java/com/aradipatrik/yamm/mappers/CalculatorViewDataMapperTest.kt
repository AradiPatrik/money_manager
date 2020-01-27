package com.aradipatrik.yamm.mappers

import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SingleValue
import com.aradipatrik.yamm.features.add.transaction.mapper.CalculatorViewDataMapper
import com.aradipatrik.yamm.util.PresentationLayerMocks.addTransactionState
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class CalculatorViewDataMapperTest {
    @Test
    fun `Calculator view data should map calculator display correctly on single value`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = addTransactionState(calculatorState = SingleValue(15))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.numberDisplay).isEqualTo("15")
    }
}