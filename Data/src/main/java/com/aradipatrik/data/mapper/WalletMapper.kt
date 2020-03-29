package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.WalletEntity
import com.aradipatrik.domain.model.Wallet

class WalletMapper {
    fun mapFromEntity(entity: WalletEntity) = Wallet(
        id = entity.id,
        name = entity.name
    )

    fun mapToEntity(domain: Wallet) = WalletEntity(
        id = domain.id,
        name = domain.name,
        updatedTimeStamp = TimestampProvider.now(),
        syncStatus = SyncStatus.None
    )
}