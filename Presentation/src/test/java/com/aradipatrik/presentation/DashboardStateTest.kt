package com.aradipatrik.presentation

import org.junit.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DashboardStateTest {
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


}