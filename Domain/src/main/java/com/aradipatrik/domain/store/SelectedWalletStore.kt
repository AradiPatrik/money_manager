package com.aradipatrik.domain.store

import com.aradipatrik.domain.model.Wallet
import io.reactivex.processors.BehaviorProcessor

class SelectedWalletStore {
    val selectedWalletProcessor = BehaviorProcessor.create<Wallet>()
}