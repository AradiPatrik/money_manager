package com.aradipatrik.presentation

data class CategoryPresentation(val name: String, val iconId: String)

data class TransactionPresentation(val amount: Int, val category: CategoryPresentation)