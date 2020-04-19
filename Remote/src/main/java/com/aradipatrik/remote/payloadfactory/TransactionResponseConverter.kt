package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionWithIdsDataModel
import com.aradipatrik.remote.AMOUNT_KEY
import com.aradipatrik.remote.CATEGORY_ID_KEY
import com.aradipatrik.remote.DATE_KEY
import com.aradipatrik.remote.DELETED_KEY
import com.aradipatrik.remote.MEMO_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.aradipatrik.remote.WALLET_ID_KEY
import com.google.firebase.firestore.DocumentSnapshot
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.IOException

data class WrongFieldTypeException(val fieldId: String) : IOException("Wrong type $fieldId")

class TransactionResponseConverter {
    fun mapResponseToEntity(document: DocumentSnapshot): TransactionWithIdsDataModel =
        TransactionWithIdsDataModel(
            id = getIdFrom(document),
            categoryId = getCategoryFrom(document),
            amount = getAmountFrom(document),
            memo = getMemoFrom(document),
            date = getDateFrom(document),
            updatedTimeStamp = getTimestampFrom(document),
            syncStatus = getSyncStatusFrom(document),
            walletId = getWalletIdFrom(document)
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

    private fun getDateFrom(document: DocumentSnapshot) =
        document.getLong(DATE_KEY)?.let {
            DateTime(it, DateTimeZone.getDefault())
        } ?: throw WrongFieldTypeException(DATE_KEY)

    private fun getMemoFrom(document: DocumentSnapshot) =
        document.getString(MEMO_KEY)
            ?: throw WrongFieldTypeException(MEMO_KEY)

    private fun getAmountFrom(document: DocumentSnapshot) =
        document.getLong(AMOUNT_KEY)?.toInt()
            ?: throw WrongFieldTypeException(AMOUNT_KEY)

    private fun getCategoryFrom(document: DocumentSnapshot) =
        document.getString(CATEGORY_ID_KEY)
            ?: throw WrongFieldTypeException(CATEGORY_ID_KEY)

    private fun getWalletIdFrom(document: DocumentSnapshot) =
        document.getString(WALLET_ID_KEY)
            ?: throw WrongFieldTypeException(WALLET_ID_KEY)
}
