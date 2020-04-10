package com.aradipatrik.domain.holder

import com.aradipatrik.domain.model.Wallet
import io.reactivex.processors.BehaviorProcessor

class SelectedWalletHolder {
    val selectedWalletProcessor = BehaviorProcessor.create<Wallet>()
}
