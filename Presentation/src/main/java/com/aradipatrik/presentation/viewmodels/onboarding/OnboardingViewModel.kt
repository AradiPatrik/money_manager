package com.aradipatrik.presentation.viewmodels.onboarding

import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.ViewModelContext
import com.aradipatrik.domain.interactor.auth.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.presentation.common.MvRxViewModel
import org.koin.android.ext.android.inject

data class OnboardingState(val text: String = "Hello"): MvRxState

class OnboardingViewModel(
    initialState: OnboardingState,
    val signUpWithEmailAndPassword: SignUpWithEmailAndPasswordInteractor
): MvRxViewModel<OnboardingState>(initialState) {
    companion object : MvRxViewModelFactory<OnboardingViewModel, OnboardingState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: OnboardingState
        ) = OnboardingViewModel(
            state,
            viewModelContext.activity.inject<SignUpWithEmailAndPasswordInteractor>().value
        )
    }

    init {
        logStateChanges()
    }
}
