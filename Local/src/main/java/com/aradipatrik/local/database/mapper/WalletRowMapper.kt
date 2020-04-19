package com.aradipatrik.local.database.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.local.database.model.wallet.WalletRow

class WalletRowMapper {
    fun mapToRow(e: WalletDataModel) = WalletRow(
        uid = e.id,
        name = e.name,
        syncStatusCode = e.syncStatus.code,
        updateTimestamp = e.updatedTimeStamp
    )

    fun mapToEntity(r: WalletRow) = WalletDataModel(
        id = r.uid,
        name = r.name,
        syncStatus = SyncStatus.fromCode(r.syncStatusCode),
        updatedTimeStamp = r.updateTimestamp
    )
}
