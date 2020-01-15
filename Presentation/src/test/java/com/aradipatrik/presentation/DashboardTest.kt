package com.aradipatrik.presentation

import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.ViewModelContext
import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.aradipatrik.domain.usecase.GetTransactionsInInterval
import com.aradipatrik.presentation.injection.DaggerApplication
import com.aradipatrik.presentation.injection.MapperContainer
import com.aradipatrik.presentation.injection.UseCaseContainer
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import com.aradipatrik.presentation.presentations.TransactionPresentation
import com.aradipatrik.testing.DomainLayerMocks.transaction
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class DashboardTest {

    private lateinit var mockViewModelContext: ViewModelContext

    private lateinit var mockDaggerApplication: DaggerApplication

    private lateinit var mockMapperContainer: MapperContainer

    private lateinit var mockUseCaseContainer: UseCaseContainer

    private lateinit var mockGetTransactionsInInterval: GetTransactionsInInterval

    private lateinit var transactionMapper: TransactionPresentationMapper

    private lateinit var dashboardViewModel: DashboardViewModel

    companion object {
        @JvmField
        @ClassRule
        val mvRxTestRule = MvRxTestRule()
    }

    @Before
    fun setup() {
        transactionMapper = TransactionPresentationMapper(CategoryPresentationMapper())
        mockGetTransactionsInInterval = mockk()
        mockUseCaseContainer = mockk {
            every { getTransactionsInInterval } returns mockGetTransactionsInInterval
        }
        mockMapperContainer = mockk {
            every { mapper } returns transactionMapper
        }
        mockDaggerApplication = mockk {
            every { useCaseContainer } returns mockUseCaseContainer
            every { mapperContainer } returns mockMapperContainer
        }
        mockViewModelContext = mockk {
            every { app<DaggerApplication>() } returns mockDaggerApplication
        }
    }

    @Test
    fun `Create should inject dependencies`() {
        // Arrange
        val state = DashboardState()

        // Act
        val dashboardViewModel = DashboardViewModel.create(mockViewModelContext, state)
        require(dashboardViewModel != null) { "Dashboard creation unsuccessful " }

        // Assert
        expectThat(dashboardViewModel.transactionMapper)
            .isEqualTo(transactionMapper)
        expectThat(dashboardViewModel.getTransactionsInInterval)
            .isEqualTo(mockGetTransactionsInInterval)
        withState(dashboardViewModel) { expectThat(it).isEqualTo(state) }
    }

    @Test
    fun `selectedMonthAsInterval should return selected month as interval`() {
        // Arrange
        val state = DashboardState()
        val selectedMonthAsInterval = state.selectedMonth.toInterval()

        // Act
        val result = state.selectedMonthAsInterval

        // Assert
        expectThat(result).isEqualTo(selectedMonthAsInterval)
    }

    @Test
    fun `refreshCurrentMonth should call getTransactionsInInterval use case with selectedMonthAsInterval, and return the result as success`() {
        // Arrange
        val initialState = DashboardState()
        val transaction = transaction()
        dashboardViewModel = DashboardViewModel(
            initialState, transactionMapper, mockGetTransactionsInInterval
        )
        every {
            mockGetTransactionsInInterval.get(
                GetTransactionsInInterval.Params(initialState.selectedMonthAsInterval)
            )
        } returns Observable.just(listOf(transaction))

        // Act
        dashboardViewModel.refreshCurrentMonth()

        // Assert
        withState(dashboardViewModel) { state ->
            expectThat(state.transactionsInSelectedMonth)
                .isA<Success<List<TransactionPresentation>>>()
            expectThat(state.transactionsInSelectedMonth())
                .isNotNull()
                .hasSize(1)
                .isEqualTo(listOf(transactionMapper.mapToPresentation(transaction)))
        }
    }

    @Test
    fun `refreshCurrentMonth should have Fail type on error`() {
        // Arrange
        val initialState = DashboardState()
        dashboardViewModel = DashboardViewModel(
            initialState, transactionMapper, mockGetTransactionsInInterval
        )
        every {
            mockGetTransactionsInInterval.get(any())
        } returns Observable.error(RuntimeException())

        // Act
        dashboardViewModel.refreshCurrentMonth()

        // Assert
        withState(dashboardViewModel) { state ->
            expectThat(state.transactionsInSelectedMonth)
                .isA<Fail<*>>()
        }
    }
}
