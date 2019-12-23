package com.aradipatrik.domain.model

import org.joda.time.DateTime
import java.net.URI

data class Transaction(val id: String, val category: Category, val amount: Int, val memo: String,
                       val date: DateTime)
data class Category(val id: String, val name: String, val iconId: String)
data class User(val id: String, val firstName: String, val lastName: String, val email: String,
                val imageUri: URI)
