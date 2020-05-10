package com.aradipatrik.domain.interactor.onboard

import com.aradipatrik.domain.interactor.onboard.LogInWithEmailAndPasswordInteractor.*
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.domain.usecase.CompletableUseCase
import io.reactivex.Completable

class LogInWithEmailAndPasswordInteractor(
    val authenticator: Authenticator,
    val userRepository: UserRepository
) : CompletableUseCase<Params> {
    data class Params internal constructor(val credentials: UserCredentials) {
        companion object {
            fun forEmailAndPassword(email: String, password: String) = Params(
                UserCredentials(email, password)
            )
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "Params are required" }
        return authenticator.loginUserWithCredentials(params.credentials)
            .flatMapCompletable(userRepository::setSignedInUser)
    }
}
