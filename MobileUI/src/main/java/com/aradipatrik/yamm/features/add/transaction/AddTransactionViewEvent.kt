package com.aradipatrik.yamm.features.add.transaction

sealed class AddTransactionViewEvent {
    data class NumberClick(val number: Int): AddTransactionViewEvent()
    object TickClick: AddTransactionViewEvent()
    object PointClick: AddTransactionViewEvent()
    object DeleteOneClick: AddTransactionViewEvent()
    object PlusClick: AddTransactionViewEvent()
    object MinusClick: AddTransactionViewEvent()
}