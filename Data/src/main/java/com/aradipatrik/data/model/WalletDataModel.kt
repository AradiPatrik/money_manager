package com.aradipatrik.data.model

import com.aradipatrik.data.mapper.SyncStatus

data class WalletDataModel(
    val id: String, val name: String,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)
