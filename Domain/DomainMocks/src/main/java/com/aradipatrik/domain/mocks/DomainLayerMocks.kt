package com.aradipatrik.domain.mocks

import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.model.Transaction
import com.aradipatrik.domain.model.User
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.domain.model.Wallet
import com.aradipatrik.testing.CommonMocks.date
import com.aradipatrik.testing.CommonMocks.int
import com.aradipatrik.testing.CommonMocks.string
import org.joda.time.DateTime

object DomainLayerMocks {
    fun category(
        id: String = string(),
        name: String = string(),
        iconId: String = string(),
        walletId: String = string()
    ) = Category(
        id = id,
        name = name,
        iconId = iconId
    )

    fun transaction(
        id: String = string(),
        category: Category = category(),
        walletId: String = string(),
        amount: Int = int(),
        memo: String = string(),
        date: DateTime = date()
    ) = Transaction(
        id = id,
        category = category,
        amount = amount,
        memo = memo,
        date = date
    )

    fun userCredentials(
        email: String = string(),
        password: String = string()
    ) = UserCredentials(
        email = email,
        password = password
    )

    fun user(
        id: String = string()
    ) = User(
        id = id
    )

    fun wallet(
        id: String = string(),
        name: String = string()
    ) = Wallet(
        id = id,
        name = name
    )
}
