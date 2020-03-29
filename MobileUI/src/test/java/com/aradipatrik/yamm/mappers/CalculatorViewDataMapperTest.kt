package com.aradipatrik.yamm.mappers

import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.*
import com.aradipatrik.yamm.features.add.transaction.mapper.CalculatorViewDataMapper
import com.aradipatrik.yamm.features.add.transaction.model.CalculatorAction
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

    @Test
    fun `Add operation should map correctly when right side is null`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = addTransactionState(calculatorState = AddOperation(15, null))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.numberDisplay).isEqualTo("15 + ")
    }

    @Test
    fun `Add operation should map correctly when right side is not null`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = addTransactionState(calculatorState = AddOperation(15, 34))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.numberDisplay).isEqualTo("15 + 34")
    }

    @Test
    fun `Subtract operation should map correctly if rhs is null`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = AddTransactionState(calculatorState = SubtractOperation(15, null))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.numberDisplay).isEqualTo("15 - ")
    }

    @Test
    fun `Subtract operation should map correctly if rhs is not null`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = AddTransactionState(calculatorState = SubtractOperation(15, 8))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.numberDisplay).isEqualTo("15 - 8")
    }

    @Test
    fun `Action should be add transaction if calculatorState is SingleValue`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = AddTransactionState(calculatorState = SingleValue(5))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.calculatorAction).isEqualTo(CalculatorAction.AddTransaction)
    }

    @Test
    fun `Action should be calculate if calculatorState is AddOperation`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = AddTransactionState(calculatorState = AddOperation(1, 2))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.calculatorAction).isEqualTo(CalculatorAction.CalculateResult)
    }

    @Test
    fun `Action should be calculate if calculatorState is SubtractOperation`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = AddTransactionState(calculatorState = SubtractOperation(1, 2))

        // Act
        val viewData = mapper.mapToViewData(state)

        // Assert
        expectThat(viewData.calculatorAction).isEqualTo(CalculatorAction.CalculateResult)
    }

    @Test
    fun `Memo should be just mapped`() {
        // Arrange
        val mapper = CalculatorViewDataMapper()
        val state = AddTransactionState(memo = "testMemo")

        // Act
        val viewData = mapper.mapToViewData(state)

        //Assert
        expectThat(viewData.memo).isEqualTo(state.memo)
    }
}