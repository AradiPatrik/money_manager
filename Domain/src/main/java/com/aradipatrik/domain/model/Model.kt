package com.aradipatrik.domain.model

import org.joda.time.DateTime

data class Transaction(
    val id: String,
    val category: Category,
    val amount: Int,
    val memo: String,
    val date: DateTime
)

data class Category(val id: String, val name: String, val iconId: String)

data class UserCredentials(val email: String, val password: String)

data class User(val id: String)

data class Wallet(val id: String, val name: String)
