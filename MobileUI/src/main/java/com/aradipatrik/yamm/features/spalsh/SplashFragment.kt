package com.aradipatrik.yamm.features.spalsh

import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.aradipatrik.presentation.viewmodels.splash.SplashViewModel
import com.aradipatrik.yamm.R

class SplashFragment : BaseMvRxFragment(R.layout.fragment_splash) {
    private val viewModel: SplashViewModel by fragmentViewModel()

    override fun invalidate() = withState(viewModel) {
        if (it.isUserSignedIn.invoke() == false) {
            findNavController().navigate(R.id.action_splashFragment_to_onboardingFragment)
        } else {
            findNavController().navigate(R.id.action_splashFragment_to_mainFragment)
        }
    }
}
