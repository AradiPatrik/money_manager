package com.aradipatrik.presentation

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.aradipatrik.domain.interactor.category.GetCategoriesInteractor
import com.aradipatrik.domain.interactor.transaction.AddTransactionInteractor
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionState
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewEvent.ActionClick
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewEvent.MemoChange
import com.aradipatrik.presentation.viewmodels.add.transaction.AddTransactionViewModel
import com.aradipatrik.presentation.viewmodels.add.transaction.CalculatorState.SingleValue
import com.aradipatrik.testing.CommonMocks.category
import com.aradipatrik.testing.CommonMocks.date
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
        single<AddTransactionInteractor> { mockk() }
        single<GetCategoriesInteractor> { mockk() }
        single { CategoryPresentationMapper() }
    }

    private val mockAddTransactionInteractor: AddTransactionInteractor by inject()
    private val mockGetCategoriesInteractor: GetCategoriesInteractor by inject()
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
        expectThat(state.categoryListRequestModel).isEqualTo(Uninitialized)
        expectThat(state.categoryListModel).isEqualTo(emptyList())
        expectThat(state.isExpense).isTrue()
        expectThat(state.calculatorState)
            .isA<SingleValue>()
            .get(SingleValue::value).isEqualTo(0)
        expectThat(state.selectedDate).isEqualTo(testDate)
        expectThat(state.selectedCategoryModel).isNull()
        expectThat(state.memo).isEqualTo("")
    }

    @Test
    fun `Get categories should be called when vm is created`() {
        // Arrange
        val testCategories = listOf(category())
        val initialState = AddTransactionState()
        every {
            mockGetCategoriesInteractor.get(any())
        } returns Observable.just(testCategories)

        // Act
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategoriesInteractor, mockAddTransactionInteractor, categoryMapper
        )

        // Assert
        verify(exactly = 1) {
            mockGetCategoriesInteractor.get(any())
        }
        withState(addTransactionViewModel) { state ->
            expectThat(state.categoryListRequestModel).isA<Success<*>>()
            expectThat(state.categoryListModel)
                .isEqualTo(testCategories.map(categoryMapper::mapToPresentation))
            expectThat(state.selectedCategoryModel)
                .isEqualTo(testCategories.map(categoryMapper::mapToPresentation)[0])
        }
    }

    @Test
    fun `Current categories should not be set on retrieval error, also error state should be failure`() {
        // Arrange
        val initialState = AddTransactionState()
        every {
            mockGetCategoriesInteractor.get(any())
        } returns Observable.error(RuntimeException())

        // Act
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategoriesInteractor, mockAddTransactionInteractor, categoryMapper
        )

        // Assert
        verify(exactly = 1) {
            mockGetCategoriesInteractor.get(any())
        }
        withState(addTransactionViewModel) { state ->
            expectThat(state.categoryListRequestModel).isA<Fail<*>>()
            expectThat(state.categoryListModel).isEmpty()
            expectThat(state.selectedCategoryModel).isNull()
        }
    }

    @Test
    fun `Add transaction should be called with current state, when add transaction is called`() {
        // Arrange
        val initialState = AddTransactionState()
        val calculatorState = initialState.calculatorState
        val testCategory = category()
        every {
            mockGetCategoriesInteractor.get(any())
        } returns Observable.just(listOf(testCategory))
        every {
            mockAddTransactionInteractor.get(any())
        } returns Completable.complete()
        addTransactionViewModel =
            AddTransactionViewModel(
                initialState,
                mockGetCategoriesInteractor,
                mockAddTransactionInteractor,
                categoryMapper
            )

        // Act
        addTransactionViewModel.processEvent(ActionClick)

        // Assert
        require(calculatorState is SingleValue)
        withState(addTransactionViewModel) { state ->
            expectThat(state.addTransactionRequest).isA<Success<*>>()
            verify {
                mockAddTransactionInteractor.get(
                    AddTransactionInteractor.Params.forTransaction(
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
            mockGetCategoriesInteractor.get(any())
        } returns Observable.just(listOf(category()))
        every {
            mockAddTransactionInteractor.get(any())
        } returns Completable.error(RuntimeException())
        addTransactionViewModel =
            AddTransactionViewModel(
                initialState,
                mockGetCategoriesInteractor,
                mockAddTransactionInteractor,
                categoryMapper
            )

        // Act
        addTransactionViewModel.processEvent(ActionClick)

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
            mockGetCategoriesInteractor.get(any())
        } returns Observable.just(listOf(category()))
        every {
            mockAddTransactionInteractor.get(any())
        } returns Completable.complete()
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategoriesInteractor, mockAddTransactionInteractor, categoryMapper
        )

        // Act
        addTransactionViewModel.processEvent(ActionClick)

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

    @Test
    fun `Memo change event should be reflected in state`() {
        val initialState = AddTransactionState()
        every {
            mockGetCategoriesInteractor.get(any())
        } returns Observable.just(listOf(category()))
        addTransactionViewModel = AddTransactionViewModel(
            initialState, mockGetCategoriesInteractor, mockAddTransactionInteractor, categoryMapper
        )

        addTransactionViewModel.processEvent(MemoChange("memo"))

        withState(addTransactionViewModel) { state ->
            expectThat(state.memo).isEqualTo("memo")
        }
    }
}
