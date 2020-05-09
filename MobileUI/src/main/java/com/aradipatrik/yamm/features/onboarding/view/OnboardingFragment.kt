package com.aradipatrik.yamm.features.onboarding.view

import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.onboarding.OnboardingViewModel
import com.aradipatrik.presentation.viewmodels.onboarding.RegisterViewEvent
import com.aradipatrik.yamm.R
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxbinding3.widget.textChangeEvents
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.fragment_onboarding.email_address_text_input_layout
import kotlinx.android.synthetic.main.fragment_onboarding.password_text_input_layout
import kotlinx.android.synthetic.main.fragment_onboarding.register_button

class OnboardingFragment : BaseMvRxFragment(R.layout.fragment_onboarding) {
    private val viewModel: OnboardingViewModel by fragmentViewModel()

    private val eventsDisposable = CompositeDisposable()
    private val uiEvents
        get() = Observable.merge(
            email_address_text_input_layout.editText!!.textChangeEvents()
                .map { RegisterViewEvent.EmailChange(it.text.toString()) }
                .distinctUntilChanged(),
            password_text_input_layout.editText!!.textChangeEvents()
                .map { RegisterViewEvent.PasswordChange(it.text.toString()) }
                .distinctUntilChanged(),
            register_button.clicks().map { RegisterViewEvent.RegisterClick }
        )

    override fun onResume() {
        super.onResume()
        eventsDisposable += uiEvents.subscribe(viewModel::processEvent)
    }

    override fun onPause() {
        super.onPause()
        eventsDisposable.clear()
    }

    override fun invalidate() = withState(viewModel) { state ->
        when (state.registerOperation) {
            is Success -> findNavController()
                .navigate(R.id.action_onboardingFragment_to_mainFragment)
            is Fail -> Toast.makeText(
                activity!!,
                (state.registerOperation as Fail).error.message!!,
                Toast.LENGTH_LONG
            ).show()
            else -> {
            }
        }

        Unit
    }
}
