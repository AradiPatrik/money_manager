package com.aradipatrik.presentation.viewmodels.onboarding

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MvRxState
import com.airbnb.mvrx.MvRxViewModelFactory
import com.airbnb.mvrx.Uninitialized
import com.airbnb.mvrx.ViewModelContext
import com.aradipatrik.domain.interactor.onboard.LogInWithEmailAndPasswordInteractor
import com.aradipatrik.domain.interactor.onboard.SignUpWithEmailAndPasswordInteractor
import com.aradipatrik.presentation.common.MvRxViewModel
import com.aradipatrik.presentation.common.ViewEventProcessor
import com.aradipatrik.presentation.viewmodels.onboarding.RegisterViewEvent.EmailChange
import com.aradipatrik.presentation.viewmodels.onboarding.RegisterViewEvent.LogInClick
import com.aradipatrik.presentation.viewmodels.onboarding.RegisterViewEvent.PasswordChange
import com.aradipatrik.presentation.viewmodels.onboarding.RegisterViewEvent.RegisterClick
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

data class OnboardingState(
    val email: String = "",
    val password: String = "",
    val loginOperation: Async<Unit> = Uninitialized
) : MvRxState

class OnboardingViewModel(
    initialState: OnboardingState,
    private val signUpWithEmailAndPassword: SignUpWithEmailAndPasswordInteractor,
    private val logInWithEmailAndPasswordInteractor: LogInWithEmailAndPasswordInteractor
) : MvRxViewModel<OnboardingState>(initialState), ViewEventProcessor<RegisterViewEvent> {
    companion object : MvRxViewModelFactory<OnboardingViewModel, OnboardingState> {
        override fun create(
            viewModelContext: ViewModelContext,
            state: OnboardingState
        ) = OnboardingViewModel(
            state,
            viewModelContext.activity.inject<SignUpWithEmailAndPasswordInteractor>().value,
            viewModelContext.activity.inject<LogInWithEmailAndPasswordInteractor>().value
        )
    }

    override fun processEvent(event: RegisterViewEvent) {
        when (event) {
            is PasswordChange -> updatePassword(event.newValue)
            is EmailChange -> updateEmail(event.newValue)
            is RegisterClick -> register()
            is LogInClick -> login()
        }
    }

    private fun updatePassword(newPassword: String) = setState { copy(password = newPassword) }
    private fun updateEmail(newEmail: String) = setState { copy(email = newEmail) }

    private fun register() = withState { state ->
        signUpWithEmailAndPassword.get(
            SignUpWithEmailAndPasswordInteractor.Params.forEmailAndPassword(
                state.email, state.password
            )
        )
            .subscribeOn(Schedulers.io())
            .execute { copy(loginOperation = it) }
    }

    private fun login() = withState { state ->
        logInWithEmailAndPasswordInteractor.get(
            LogInWithEmailAndPasswordInteractor.Params.forEmailAndPassword(
                state.email, state.password
            )
        )
            .subscribeOn(Schedulers.io())
            .execute { copy(loginOperation = it) }
    }
}
