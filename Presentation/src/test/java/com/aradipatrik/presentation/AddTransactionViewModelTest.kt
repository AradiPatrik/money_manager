package com.aradipatrik.presentation

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.usecase.AddTransaction
import com.aradipatrik.domain.usecase.GetCategories
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewEvent.AddClick
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewModel
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SingleValue
import com.aradipatrik.testing.DomainLayerMocks.category
import com.aradipatrik.testing.DomainLayerMocks.date
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.DateTime
import org.junit.After
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import strikt.api.expectThat
import strikt.assertions.*

class AddTransactionViewModelTest : KoinTest {
    private val addTransactionTestModule = module {
        single<AddTransaction> { mockk() }
        single<GetCategories> { mockk() }
        single { CategoryPresentationMapper() }
    }

    private val mockAddTransaction: AddTransaction by inject()
    private val mockGetCategories: GetCategories by inject()
    private val categoryMapper: CategoryPresentationMapper by inject()
    private lateinit var addTransactionViewModel: AddTransactionViewModel

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
    fun `Initial state should be set correctly`() {
        val testDate = DateTime.now()
        mockkStatic(DateTime::class)
        every { DateTime.now() } returns testDate

        val state = AddTransactionState()

        expectThat(state.addTransactionRequest).isEqualTo(Uninitialized)
        expectThat(state.categoryListRequest).isEqualTo(Uninitialized)
        expectThat(state.categoryList).isEqualTo(emptyList())
        expectThat(state.isExpense).isTrue()
        expectThat(state.calculatorState)
            .isA<SingleValue>()
            .get(SingleValue::value).isEqualTo(0)
        expectThat(state.selectedDate).isEqualTo(testDate)
        expectThat(state.selectedCategory).isNull()
        expectThat(state.memo).isEqualTo("")
    }

    @Test
    fun `Get categories should be called when vm is created`() {
        // Arrange
        val testCategories = listOf(category())
        val initialState = AddTransactionState()
        every {
            mockGetCategories.get(any())
        } returns Observable.just(testCategories)

        // Act
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategories, mockAddTransaction, categoryMapper
        )

        // Assert
        verify(exactly = 1) {
            mockGetCategories.get(any())
        }
        withState(addTransactionViewModel) { state ->
            expectThat(state.categoryListRequest).isA<Success<*>>()
            expectThat(state.categoryList)
                .isEqualTo(testCategories.map(categoryMapper::mapToPresentation))
            expectThat(state.selectedCategory)
                .isEqualTo(testCategories.map(categoryMapper::mapToPresentation)[0])
        }
    }

    @Test
    fun `Current categories should not be set on retrieval error, also error state should be failure`() {
        // Arrange
        val initialState = AddTransactionState()
        every {
            mockGetCategories.get(any())
        } returns Observable.error(RuntimeException())

        // Act
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategories, mockAddTransaction, categoryMapper
        )

        // Assert
        verify(exactly = 1) {
            mockGetCategories.get(any())
        }
        withState(addTransactionViewModel) { state ->
            expectThat(state.categoryListRequest).isA<Fail<*>>()
            expectThat(state.categoryList).isEmpty()
            expectThat(state.selectedCategory).isNull()
        }
    }

    @Test
    fun `Add transaction should be called with current state, when add transaction is called`() {
        // Arrange
        val initialState = AddTransactionState()
        val calculatorState = initialState.calculatorState
        val testCategory = category()
        every {
            mockGetCategories.get(any())
        } returns Observable.just(listOf(testCategory))
        every {
            mockAddTransaction.get(any())
        } returns Completable.complete()
        addTransactionViewModel =
            AddTransactionViewModel(
                initialState, mockGetCategories, mockAddTransaction, categoryMapper
            )

        // Act
        addTransactionViewModel.processEvent(AddClick)

        // Assert
        require(calculatorState is SingleValue)
        withState(addTransactionViewModel) { state ->
            expectThat(state.addTransactionRequest).isA<Success<*>>()
            verify {
                mockAddTransaction.get(
                    AddTransaction.Params.forTransaction(
                        Transaction(
                            "",
                            testCategory,
                            calculatorState.value,
                            initialState.memo,
                            initialState.selectedDate
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `Add transaction should return failure on use case error`() {
        // Arrange
        val initialState = AddTransactionState()
        every {
            mockGetCategories.get(any())
        } returns Observable.just(listOf(category()))
        every {
            mockAddTransaction.get(any())
        } returns Completable.error(RuntimeException())
        addTransactionViewModel =
            AddTransactionViewModel(
                initialState, mockGetCategories, mockAddTransaction, categoryMapper
            )

        // Act
        addTransactionViewModel.processEvent(AddClick)

        // Assert
        withState(addTransactionViewModel) { state ->
            expectThat(state.addTransactionRequest).isA<Fail<*>>()
        }
    }

    @Test
    fun `Add transaction should reset every transaction state to initial state except the request state`() {
        // Arrange
        val testDate = date()
        val initialState = AddTransactionState(
            memo = "testMemo", calculatorState = SingleValue(500),
            isExpense = false, selectedDate = testDate
        )
        every {
            mockGetCategories.get(any())
        } returns Observable.just(listOf(category()))
        every {
            mockAddTransaction.get(any())
        } returns Completable.complete()
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategories, mockAddTransaction, categoryMapper
        )

        // Act
        addTransactionViewModel.processEvent(AddClick)

        // Assert
        withState(addTransactionViewModel) { state ->
            expectThat(state.addTransactionRequest).isA<Success<*>>()
            expectThat(state.memo).isEqualTo("")
            expectThat(state.calculatorState)
                .isA<SingleValue>()
                .get(SingleValue::value).isEqualTo(0)
            expectThat(state.isExpense).isTrue()
            expectThat(state.selectedDate).isNotEqualTo(testDate)
        }
    }
}
