package com.aradipatrik.presentation.viewmodels.addtransaction

sealed class AddTransactionViewEvent {
    data class NumberClick(val number: Int) : AddTransactionViewEvent()
    data class MemoChange(val memo: String) : AddTransactionViewEvent()
    object PointClick : AddTransactionViewEvent()
    object DeleteOneClick : AddTransactionViewEvent()
    object PlusClick : AddTransactionViewEvent()
    object MinusClick : AddTransactionViewEvent()
    object ActionClick : AddTransactionViewEvent()
    object EqualsClick : AddTransactionViewEvent()
}
