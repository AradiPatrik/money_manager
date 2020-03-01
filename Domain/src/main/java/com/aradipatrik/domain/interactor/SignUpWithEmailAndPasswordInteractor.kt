package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.usecase.CompletableUseCase
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.domain.interactor.SignUpWithEmailAndPasswordInteractor.Params
import io.reactivex.Completable

class SignUpWithEmailAndPasswordInteractor: CompletableUseCase<Params> {
    data class Params(val userCredentials: UserCredentials) {
        companion object {
            fun forCredentials(userCredentials: UserCredentials) = Params(userCredentials)
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return Completable.complete()
    }
}
