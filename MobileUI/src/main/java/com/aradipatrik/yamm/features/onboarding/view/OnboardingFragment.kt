package com.aradipatrik.yamm.features.onboarding.view

import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.onboarding.OnboardingViewModel
import com.aradipatrik.yamm.R

class OnboardingFragment : BaseMvRxFragment(R.layout.fragment_onboarding) {
    private val viewModel: OnboardingViewModel by fragmentViewModel()

    override fun invalidate() = withState(viewModel) { state ->

    }
}