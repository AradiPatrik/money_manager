package com.aradipatrik.domain.interactor.auth

import com.aradipatrik.domain.interfaces.data.UserRepository
import com.aradipatrik.domain.usecase.ObservableUseCase

class IsUserSignedInInteractor(
    private val userRepository: UserRepository
): ObservableUseCase<Boolean, Unit> {
    override fun get(params: Unit?) = userRepository.isUserSignedIn()
        .toObservable()

}
