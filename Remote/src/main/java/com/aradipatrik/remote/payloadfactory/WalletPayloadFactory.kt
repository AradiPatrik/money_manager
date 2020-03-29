package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLET_NAME_KEY
import com.google.firebase.Timestamp

class WalletPayloadFactory {
    fun createPayloadFrom(wallet: WalletEntity): HashMap<String, Any> = hashMapOf(
        WALLET_NAME_KEY to wallet.name,
        UPDATED_TIMESTAMP_KEY to Timestamp.now(),
        DELETED_KEY to (wallet.syncStatus == SyncStatus.ToDelete)
    )
}
