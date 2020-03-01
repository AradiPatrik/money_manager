package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.usecase.ObservableUseCase
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.interfaces.CategoryRepository
import io.reactivex.Observable

class GetCategoriesInteractor(
    private val repository: CategoryRepository
): ObservableUseCase<List<Category>, Unit> {
    override fun get(params: Unit?): Observable<List<Category>> {
        return repository.getAll()
    }
}
