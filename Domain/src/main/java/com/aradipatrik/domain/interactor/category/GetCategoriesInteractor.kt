package com.aradipatrik.domain.interactor.category

import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.interfaces.data.WalletRepository
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.usecase.ObservableUseCase
import io.reactivex.Observable

class GetCategoriesInteractor(
    private val categoryRepository: CategoryRepository,
    private val walletRepository: WalletRepository
) : ObservableUseCase<List<Category>, Unit> {
    override fun get(params: Unit?) =
        walletRepository.getSelectedWallet().flatMapObservable { selectedWallet ->
            categoryRepository.getAll(selectedWallet.id)
        }
}
