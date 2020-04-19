package com.aradipatrik.presentation.presentations

import org.joda.time.DateTime

data class TransactionPresentationModel(
    val id: String, val amount: Int,
    val categoryModel: CategoryPresentationModel, val date: DateTime,
    val memo: String
)