package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.usecase.ObservableUseCase
import com.aradipatrik.domain.model.Category
import com.aradipatrik.domain.interfaces.data.CategoryRepository
import io.reactivex.Observable

class GetCategoriesInteractor(
    private val repository: CategoryRepository,
    private val authenticator: Authenticator
): ObservableUseCase<List<Category>, Unit> {
    override fun get(params: Unit?): Observable<List<Category>> {
        require(authenticator.currentUser != null) { "There is no authenticated user in place" }
        return repository.getAll()
    }
}
