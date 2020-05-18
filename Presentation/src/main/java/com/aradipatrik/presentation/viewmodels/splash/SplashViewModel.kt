package com.aradipatrik.presentation.viewmodels.splash

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.aradipatrik.domain.interactor.onboard.IsUserSignedInInteractor
import com.aradipatrik.presentation.common.MvRxViewModel
import org.koin.android.ext.android.inject

data class SplashState(val isUserSignedIn: Async<Boolean> = Uninitialized): MvRxState

class SplashViewModel(
    initialState: SplashState,
    isUserSignedInInteractor: IsUserSignedInInteractor
): MvRxViewModel<SplashState>(initialState) {
    companion object : MvRxViewModelFactory<SplashViewModel, SplashState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: SplashState
        ) = SplashViewModel(
            state,
            viewModelContext.activity.inject<IsUserSignedInInteractor>().value
        )
    }

    init {
        isUserSignedInInteractor.get().execute {
            copy(isUserSignedIn = it)
        }
    }
}
