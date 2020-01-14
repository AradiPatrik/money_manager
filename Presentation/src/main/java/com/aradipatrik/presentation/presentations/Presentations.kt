package com.aradipatrik.presentation.presentations

import org.joda.time.DateTime

data class CategoryPresentation(val id: String, val name: String, val iconId: String)

data class TransactionPresentation(val id: String, val amount: Int,
                                   val category: CategoryPresentation, val date: DateTime,
                                   val memo: String)