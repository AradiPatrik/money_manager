package com.aradipatrik.datasource.test

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.testing.DomainLayerMocks.long
import com.aradipatrik.testing.DomainLayerMocks.string

object EntityFactory {
    fun walletEntity(
        id: String = string(),
        name: String = string(),
        syncStatus: SyncStatus = SyncStatus.Synced,
        updateTimestamp: Long = long()
    ) = WalletEntity(
        id = id,
        name = name,
        syncStatus = syncStatus,
        updatedTimeStamp = updateTimestamp
    )
}