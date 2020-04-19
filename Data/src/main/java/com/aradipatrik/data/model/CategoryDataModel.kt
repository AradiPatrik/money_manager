package com.aradipatrik.data.model

import com.aradipatrik.data.mapper.SyncStatus

data class CategoryDataModel(
    val id: String, val name: String, val iconId: String, val walletId: String,
    val updatedTimeStamp: Long, val syncStatus: SyncStatus
)