package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.WalletDataModel
import com.aradipatrik.remote.CATEGORY_NAME_KEY
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.google.firebase.firestore.DocumentSnapshot

class WalletResponseConverter {
    fun mapResponseToEntity(document: DocumentSnapshot): WalletDataModel =
        WalletDataModel(
            id = getIdFrom(document),
            syncStatus = getSyncStatusFrom(document),
            name = getNameFrom(document),
            updatedTimeStamp = getTimestampFrom(document)
        )

    private fun getIdFrom(document: DocumentSnapshot) = document.id

    private fun getSyncStatusFrom(document: DocumentSnapshot): SyncStatus {
        return document.getBoolean(DELETED_KEY)?.let {
            if (it) SyncStatus.ToDelete else SyncStatus.Synced
        } ?: throw WrongFieldTypeException(DELETED_KEY)
    }

    private fun getTimestampFrom(document: DocumentSnapshot) =
        document.getTimestamp(UPDATED_TIMESTAMP_KEY)?.toDate()?.time
            ?: throw WrongFieldTypeException(UPDATED_TIMESTAMP_KEY)

    private fun getNameFrom(document: DocumentSnapshot) =
        document.getString(CATEGORY_NAME_KEY)
            ?: throw WrongFieldTypeException(CATEGORY_NAME_KEY)
}
