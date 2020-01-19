package com.aradipatrik.yamm.model

data class TransactionViewData(
    val id: String,
    val categoryId: String,
    val memo: String,
    val categoryIconResId: Int,
    val amount: Int,
    val colorResId: Int,
    val categoryName: String
)
