package com.aradipatrik.presentation

import com.airbnb.mvrx.ViewModelContext
import com.airbnb.mvrx.test.MvRxTestRule
import com.airbnb.mvrx.withState
import com.aradipatrik.domain.usecase.GetTransactionsInInterval
import com.aradipatrik.presentation.injection.DaggerApplication
import com.aradipatrik.presentation.injection.MapperContainer
import com.aradipatrik.presentation.injection.UseCaseContainer
import com.aradipatrik.presentation.mapper.CategoryPresentationMapper
import com.aradipatrik.presentation.mapper.TransactionPresentationMapper
import io.mockk.every
import io.mockk.mockk
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import strikt.api.expectThat
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
    fun `Test create should inject dependencies`() {
        // Arrange
        val state = DashboardState()

        // Act
        val dashboardViewModel = DashboardViewModel.create(mockViewModelContext, state)
        require(dashboardViewModel != null) { "Dashboard creation unsuccessful "}

        // Assert
        expectThat(dashboardViewModel.transactionMapper)
            .isEqualTo(transactionMapper)
        expectThat(dashboardViewModel.getTransactionsInInterval)
            .isEqualTo(mockGetTransactionsInInterval)
        withState(dashboardViewModel) { expectThat(it).isEqualTo(state) }
    }
}