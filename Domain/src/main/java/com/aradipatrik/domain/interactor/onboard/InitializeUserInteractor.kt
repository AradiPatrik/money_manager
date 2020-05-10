package com.aradipatrik.domain.interactor.onboard

import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.usecase.CompletableUseCase

class InitializeUserInteractor(
    private val walletRepository: WalletRepository,
    private val categoryRepository: CategoryRepository
) : CompletableUseCase<Unit> {
    override fun get(params: Unit?) = walletRepository.createWalletWithName("Initial wallet")
        .flatMapCompletable { wallet ->
            walletRepository.setSelectedWallet(wallet)
                .andThen(categoryRepository.addAll(initialCategories, wallet.id))
        }

    val initialCategories = listOf(
        "Groceries",
        "Food",
        "Gift",
        "Rent",
        "Entertainment",
        "Health",
        "Transportation",
        "Sports"
    ).map { Category(it, it, it) }
}
