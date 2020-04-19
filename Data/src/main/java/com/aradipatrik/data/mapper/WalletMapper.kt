package com.aradipatrik.data.mapper

import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.domain.model.Wallet

class WalletMapper {
    fun mapFromEntity(dataModel: WalletDataModel) = Wallet(
        id = dataModel.id,
        name = dataModel.name
    )

    fun mapToEntity(domain: Wallet) = WalletDataModel(
        id = domain.id,
        name = domain.name,
        updatedTimeStamp = TimestampProvider.now(),
        syncStatus = SyncStatus.None
    )
}
