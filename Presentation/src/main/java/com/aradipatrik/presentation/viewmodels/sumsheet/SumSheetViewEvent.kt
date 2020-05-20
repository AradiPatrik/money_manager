package com.aradipatrik.presentation.viewmodels.sumsheet

sealed class SumSheetViewEvent {
    object DecrementClick: SumSheetViewEvent()
    object IncrementClick: SumSheetViewEvent()
}