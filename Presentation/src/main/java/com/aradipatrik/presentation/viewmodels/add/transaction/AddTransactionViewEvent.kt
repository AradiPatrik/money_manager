package com.aradipatrik.presentation.viewmodels.add.transaction

sealed class AddTransactionViewEvent {
    data class NumberClick(val number: Int): AddTransactionViewEvent()
    object PointClick: AddTransactionViewEvent()
    object DeleteOneClick: AddTransactionViewEvent()
    object PlusClick: AddTransactionViewEvent()
    object MinusClick: AddTransactionViewEvent()
    object ActionClick: AddTransactionViewEvent()
    object EqualsClick: AddTransactionViewEvent()
}