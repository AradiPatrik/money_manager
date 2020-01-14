package com.aradipatrik.presentation.common

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState
import com.aradipatrik.presentation.BuildConfig

abstract class MvRxViewModel<S : MvRxState>(initialState: S) :
    BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG)