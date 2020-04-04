package com.aradipatrik.domain.interactor.category

import com.aradipatrik.domain.interfaces.data.CategoryRepository
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.usecase.ObservableUseCase
import io.reactivex.Observable

class GetCategoriesInteractor(
    private val repository: CategoryRepository
) : ObservableUseCase<List<Category>, Unit> {
    override fun get(params: Unit?): Observable<List<Category>> = repository.getAll()
}
