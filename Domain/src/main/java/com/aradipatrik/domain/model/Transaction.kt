package com.aradipatrik.domain.model

import org.joda.time.DateTime

data class Transaction(
    val id: String,
    val category: Category,
    val amount: Int,
    val memo: String,
    val date: DateTime
)