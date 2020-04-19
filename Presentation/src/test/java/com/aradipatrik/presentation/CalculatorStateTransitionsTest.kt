package com.aradipatrik.presentation

import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.transaction.AddTransactionInteractor
import com.aradipatrik.domain.mocks.DomainLayerMocks.category
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionViewEvent.*
import com.aradipatrik.presentation.viewmodels.addtransaction.AddTransactionViewModel
import com.aradipatrik.presentation.viewmodels.addtransaction.CalculatorState.*
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest
import strikt.api.expectThat
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNull

class CalculatorStateTransitionsTest : KoinTest {

    private val addTransactionTestModule = module {
        single { (initialState: AddTransactionState) ->
            AddTransactionViewModel(
                initialState,
                get(),
                get(),
                get()
            )
        }
        single<AddTransactionInteractor> {
            mockk {
                every { get(any<AddTransactionInteractor.Params>()) } returns Completable.complete()
            }
        }
        single<GetCategoriesInteractor> {
            mockk {
                every { get(any<Unit>()) } returns Observable.just(listOf(category()))
            }
        }
        single { CategoryPresentationMapper() }
    }

    companion object {
        @JvmField
        @ClassRule
        val mvRxTestRule = MvRxTestRule()
    }

    @Before
    fun setup() {
        startKoin { modules(addTransactionTestModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `AddOperation value should return correct value`() {
        val addOperation = AddOperation(1, 2)
        val initialAddOperation = AddOperation(2, null)
        expectThat(addOperation.value).isEqualTo(3)
        expectThat(initialAddOperation.value).isEqualTo(2)
    }

    @Test
    fun `Subtract operation value should return correct value`() {
        val subtractOperation = SubtractOperation(3, 1)
        val initialSubtractOperation = SubtractOperation(2, null)
        expectThat(subtractOperation.value).isEqualTo(2)
        expectThat(initialSubtractOperation.value).isEqualTo(2)
    }

    @Test
    fun `On add click SingleValue should become AddOperation, with value as lhs`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(2)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(PlusClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(2)
                    get(AddOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `On add click AddOperation should become AddOperation, with value as lhs`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(1, 2)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(PlusClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(3)
                    get(AddOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `On add click SubtractOperation should become AddOperation, with value as lhs`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(3, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(PlusClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(2)
                    get(AddOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `On minus click SingleValue should become SubtractOperation, with value as lhs`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(3)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(MinusClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(3)
                    get(SubtractOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `On minus click SubtractOperation should become SubtractOperation, with value as lhs`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(3, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(MinusClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(2)
                    get(SubtractOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `On minus click AddOperation should become SubtractOperation, with value as lhs`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(3, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(MinusClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(4)
                    get(SubtractOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `DeleteOne on SingleValue with only one digit, should become SingleValue of 0`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(5)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(0)
        }
    }

    @Test
    fun `Delete one on SingleValue with more then one digit, should become SingleValue with last digit removed`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(52)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(5)
        }
    }

    @Test
    fun `Delete one on AddOperation with initial state, should become SingleValue with lhs as value`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, null)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(5)
        }
    }

    @Test
    fun `Delete one on AddOperation with only one digit, should become AddOperation with initial state`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, 2)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(5)
                    get(AddOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `Delete one on AddOperation with more then one digit rhs, should become AddOperation with an rhs with last digit removed`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, 23)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(5)
                    get(AddOperation::rhs).isEqualTo(2)
                }
        }
    }

    @Test
    fun `Delete one on SubtractOperation with initial state, should become SingleValue with lhs as value`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, null)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(5)
        }
    }

    @Test
    fun `Delete one on SubtractOperation with only one digit, should become SubtractOperation with initial state`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, 2)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(5)
                    get(SubtractOperation::rhs).isNull()
                }
        }
    }

    @Test
    fun `Delete one on SubtractOperation with more then one digit rhs, should become SubtractOperation with an rhs with last digit removed`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, 23)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(DeleteOneClick)

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(5)
                    get(SubtractOperation::rhs).isEqualTo(2)
                }
        }
    }

    @Test
    fun `0 click on SingleValue of 0 should have no effect`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(0)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(0))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(0)
        }
    }

    @Test
    fun `0 click on SingleValue with single digit other than 0 should append digit to value`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(0))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(10)
        }
    }

    @Test
    fun `Any click on SingleValue with more than one digits should append digit to value`() {
        val initialState =
            AddTransactionState(
                calculatorState = SingleValue(15)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(2))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(152)
        }
    }

    @Test
    fun `AddOperation with rhs null should have digit overriden`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, null)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(0))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(5)
                    get(AddOperation::rhs).isEqualTo(0)
                }
        }
    }

    @Test
    fun `AddOperation with rhs 0 should have digit overriden`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, 0)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(3))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(5)
                    get(AddOperation::rhs).isEqualTo(3)
                }
        }
    }

    @Test
    fun `AddOperation with rhs with value other than 0 or null, should be appended with digit on number click`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(3))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<AddOperation>()
                .and {
                    get(AddOperation::lhs).isEqualTo(5)
                    get(AddOperation::rhs).isEqualTo(13)
                }
        }
    }

    @Test
    fun `SubtractOperation with rhs null should have digit overriden`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, null)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(0))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(5)
                    get(SubtractOperation::rhs).isEqualTo(0)
                }
        }
    }

    @Test
    fun `SubtractOperation with rhs 0 should have digit overriden`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, 0)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(3))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(5)
                    get(SubtractOperation::rhs).isEqualTo(3)
                }
        }
    }

    @Test
    fun `SubtractOperation with rhs with value other than 0 or null, should be appended with digit on number click`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(NumberClick(3))

        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SubtractOperation>()
                .and {
                    get(SubtractOperation::lhs).isEqualTo(5)
                    get(SubtractOperation::rhs).isEqualTo(13)
                }
        }
    }

    @Test
    fun `Equals on AddOperation should transform calculator state into SingleValue, with value of the AddOperation`() {
        val initialState =
            AddTransactionState(
                calculatorState = AddOperation(5, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(ActionClick)
        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(6)
        }
    }

    @Test
    fun `Equals on SubtractOperation should transform calculator state into SingleValue, with value of the SubtractOperation`() {
        val initialState =
            AddTransactionState(
                calculatorState = SubtractOperation(5, 1)
            )
        val viewModel: AddTransactionViewModel = getKoin().get { parametersOf(initialState) }

        viewModel.processEvent(ActionClick)
        withState(viewModel) { state ->
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(4)
        }
    }
}
