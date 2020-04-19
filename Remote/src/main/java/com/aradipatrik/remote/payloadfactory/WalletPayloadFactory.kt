package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLET_NAME_KEY
import com.google.firebase.Timestamp

class WalletPayloadFactory {
    fun createPayloadFrom(wallet: WalletDataModel): HashMap<String, Any> = hashMapOf(
        WALLET_NAME_KEY to wallet.name,
        UPDATED_TIMESTAMP_KEY to Timestamp.now(),
        DELETED_KEY to (wallet.syncStatus == SyncStatus.ToDelete)
    )
}
