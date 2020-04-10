package com.aradipatrik.local.database.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.local.database.wallet.WalletRow

class WalletRowMapper {
    fun mapToRow(e: WalletEntity) = WalletRow(
        uid = e.id,
        name = e.name,
        syncStatusCode = e.syncStatus.code,
        updateTimestamp = e.updatedTimeStamp
    )

    fun mapToEntity(r: WalletRow) = WalletEntity(
        id = r.uid,
        name = r.name,
        syncStatus = SyncStatus.fromCode(r.syncStatusCode),
        updatedTimeStamp = r.updateTimestamp
    )
}
