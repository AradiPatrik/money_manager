package com.aradipatrik.presentation

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.aradipatrik.domain.interactor.GetTransactionsInIntervalInteractor
import com.aradipatrik.presentation.datahelpers.MockDataFactory.transactionPresentation
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.presentation.viewmodels.history.TransactionHistoryState
import com.aradipatrik.presentation.viewmodels.history.TransactionHistoryViewModel
import com.aradipatrik.testing.DomainLayerMocks.transaction
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import org.joda.time.YearMonth
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

class TransactionHistoryViewModelTest : KoinTest {

    private val dashboardTestModule = module {
        single<GetTransactionsInIntervalInteractor> { mockk() }
        single { CategoryPresentationMapper() }
        single { TransactionPresentationMapper(get()) }
    }

    private val mockGetTransactionsInIntervalInteractor: GetTransactionsInIntervalInteractor by inject()

    private val transactionMapper: TransactionPresentationMapper by inject()

    private lateinit var transactionHistoryViewModel: TransactionHistoryViewModel

    companion object {
        @JvmField
        @ClassRule
        val mvRxTestRule = MvRxTestRule()
    }

    @Before
    fun setup() {
        startKoin { modules(dashboardTestModule) }
    }

    @After
    fun teardown() {
        stopKoin()
    }

    @Test
    fun `initial state should be set correctly`() {
        val state =
            TransactionHistoryState()
        expectThat(state.selectedMonth).isEqualTo(YearMonth.now())
        expectThat(state.transactionsOfSelectedMonth).isEmpty()
        expectThat(state.request).isA<Uninitialized>()
    }

    @Test
    fun `fetchCurrentMonth should call getTransactionsInInterval use case with selectedMonthAsInterval, and return the result as success`() {
        // Arrange
        val initialState =
            TransactionHistoryState()
        val transaction = transaction()
        every {
            mockGetTransactionsInIntervalInteractor.get(
                GetTransactionsInIntervalInteractor.Params(initialState.selectedMonthAsInterval)
            )
        } returns Observable.just(listOf(transaction))
        transactionHistoryViewModel =
            TransactionHistoryViewModel(
                initialState, transactionMapper, mockGetTransactionsInIntervalInteractor
            )

        // Act
        transactionHistoryViewModel.fetchCurrentMonth()

        // Assert
        withState(transactionHistoryViewModel) { state ->
            expectThat(state.request)
                .isA<Success<List<TransactionPresentation>>>()
            expectThat(state.request())
                .isNotNull()
                .hasSize(1)
                .isEqualTo(listOf(transactionMapper.mapToPresentation(transaction)))
        }
    }

    @Test
    fun `fetchCurrentMonth should dispose of old requestDisposable and set request disposable`() {
        // Arrange
        val mockDisposable = mockk<Disposable>()
        val initialState =
            TransactionHistoryState()
        every {
            mockGetTransactionsInIntervalInteractor.get(any())
        } returns Observable.just(emptyList())
        every { mockDisposable.dispose() } just Runs
        transactionHistoryViewModel =
            TransactionHistoryViewModel(
                initialState, transactionMapper, mockGetTransactionsInIntervalInteractor
            )
        transactionHistoryViewModel.currentRequestDisposable = mockDisposable

        // Act
        transactionHistoryViewModel.fetchCurrentMonth()

        // Assert
        verify(exactly = 1) { mockDisposable.dispose() }
        expectThat(transactionHistoryViewModel.currentRequestDisposable).isNotEqualTo(mockDisposable)
    }

    @Test
    fun `fetchCurrentMonth should set list if successful`() {
        // Arrange
        val testTransactions = listOf(transaction())
        val testPresentations = testTransactions.map(transactionMapper::mapToPresentation)
        every {
            mockGetTransactionsInIntervalInteractor.get(any())
        } returns Observable.just(testTransactions)
        transactionHistoryViewModel =
            TransactionHistoryViewModel(
                TransactionHistoryState(),
                transactionMapper,
                mockGetTransactionsInIntervalInteractor
            )

        // Act
        transactionHistoryViewModel.fetchCurrentMonth()

        // Assert
        withState(transactionHistoryViewModel) {
            expectThat(it.transactionsOfSelectedMonth).isEqualTo(testPresentations)
        }
    }

    @Test
    fun `fetchCurrentMonth should have Fail type on error`() {
        // Arrange
        val initialState =
            TransactionHistoryState()
        every {
            mockGetTransactionsInIntervalInteractor.get(any())
        } returns Observable.error(RuntimeException())
        transactionHistoryViewModel =
            TransactionHistoryViewModel(
                initialState, transactionMapper, mockGetTransactionsInIntervalInteractor
            )

        // Act
        transactionHistoryViewModel.fetchCurrentMonth()

        // Assert
        withState(transactionHistoryViewModel) { state ->
            expectThat(state.request)
                .isA<Fail<*>>()
        }
    }

    @Test
    fun `fetchCurrentMonth should not set transactionsOfSelectedMonth on failure`() {
        // Arrange
        val initialTransactions = listOf(transactionPresentation())
        val initialState =
            TransactionHistoryState(
                transactionsOfSelectedMonth = initialTransactions
            )
        every {
            mockGetTransactionsInIntervalInteractor.get(any())
        } returns Observable.error(RuntimeException())
        transactionHistoryViewModel =
            TransactionHistoryViewModel(
                initialState, transactionMapper, mockGetTransactionsInIntervalInteractor
            )

        // Act
        transactionHistoryViewModel.fetchCurrentMonth()

        // Assert
        withState(transactionHistoryViewModel) { state ->
            expectThat(state.transactionsOfSelectedMonth).isEqualTo(initialTransactions)
        }
    }

    @Test
    fun `fetchCurrentMonth should be called on init`() {
        // Arrange
        val initialState =
            TransactionHistoryState()
        every {
            mockGetTransactionsInIntervalInteractor.get(any())
        } returns Observable.just(emptyList())

        // Act
        transactionHistoryViewModel =
            TransactionHistoryViewModel(
                initialState, transactionMapper, mockGetTransactionsInIntervalInteractor
            )

        // Assert
        verify(exactly = 1) {
            mockGetTransactionsInIntervalInteractor.get(any())
        }
    }
}
