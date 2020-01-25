package com.aradipatrik.domain.usecase

import com.aradipatrik.domain.interactor.CompletableUseCase
import com.aradipatrik.domain.interactor.ObservableUseCase
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.repository.CategoryRepository
import io.reactivex.Observable

class GetCategories(
    private val repository: CategoryRepository
): ObservableUseCase<List<Category>, Unit> {
    override fun get(params: Unit?): Observable<List<Category>> {
        return repository.getAll()
    }
}