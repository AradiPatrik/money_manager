package com.aradipatrik.domain.interactor

import com.aradipatrik.domain.exceptions.auth.ValidationException
import com.aradipatrik.domain.usecase.CompletableUseCase
import com.aradipatrik.domain.interactor.SignUpWithEmailAndPasswordInteractor.Params
import com.aradipatrik.domain.interfaces.auth.Authenticator
import com.aradipatrik.domain.model.UserCredentials
import com.aradipatrik.domain.validation.CredentialsValidator
import io.reactivex.Completable

class SignUpWithEmailAndPasswordInteractor(
    private val authenticator: Authenticator
): CompletableUseCase<Params> {
    data class Params internal constructor(val credentials: UserCredentials) {
        companion object {
            fun forEmailAndPassword(email: String, password: String) = Params(
                UserCredentials(email, password)
            )
        }
    }

    override fun get(params: Params?): Completable {
        require(params != null) { "${this::class.java.simpleName} params can't be null" }
        return try {
            CredentialsValidator.validate(params.credentials)
            authenticator.registerUserWithCredentials(params.credentials)
        } catch (exception: ValidationException) {
            Completable.error(exception)
        }
    }
}
