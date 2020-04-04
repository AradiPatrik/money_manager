package com.aradipatrik.domain.exceptions.wallet

import com.aradipatrik.domain.model.Wallet

data class WalletNotFoundException(
    val availableWallet: List<Wallet>,
    val selectedWallet: String
) : Exception()