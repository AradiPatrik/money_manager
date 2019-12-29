package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.remote.*
import com.aradipatrik.remote.AMOUNT_KEY
import com.aradipatrik.remote.CATEGORY_ID_KEY
import com.aradipatrik.remote.DATE_KEY
import com.aradipatrik.remote.MEMO_KEY
import com.aradipatrik.remote.UPDATED_TIMESTAMP_KEY
import com.google.firebase.firestore.DocumentSnapshot
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import java.io.IOException
import javax.inject.Inject

data class WrongFieldTypeException(val fieldId: String) : IOException("Wrong type $fieldId")

class TransactionResponsePayloadConverter @Inject constructor() {
    fun mapResponseToEntity(document: DocumentSnapshot): TransactionPartialEntity =
        TransactionPartialEntity(
            id = getIdFrom(document),
            categoryId = getCategoryFrom(document),
            amount = getAmountFrom(document),
            memo = getMemoFrom(document),
            date = getDateFrom(document),
            updatedTimeStamp = getTimestampFrom(document),
            syncStatus = getSyncStatusFrom(document)
        )

    private fun getIdFrom(document: DocumentSnapshot) = document.id

    private fun getSyncStatusFrom(document: DocumentSnapshot): SyncStatus {
        return document.getBoolean(DELETED_KEY)?.let {
            if (it) SyncStatus.ToDelete else SyncStatus.Synced
        } ?: throw WrongFieldTypeException(UPDATED_TIMESTAMP_KEY)
    }

    private fun getTimestampFrom(document: DocumentSnapshot) =
        (document.getLong(UPDATED_TIMESTAMP_KEY)
            ?: throw WrongFieldTypeException(UPDATED_TIMESTAMP_KEY))

    private fun getDateFrom(document: DocumentSnapshot): DateTime {
        return document.getLong(DATE_KEY)?.let {
            DateTime(DATE_KEY, DateTimeZone.getDefault())
        } ?: throw WrongFieldTypeException(DATE_KEY)
    }

    private fun getMemoFrom(document: DocumentSnapshot) =
        (document.getString(MEMO_KEY)
            ?: throw WrongFieldTypeException(MEMO_KEY))

    private fun getAmountFrom(document: DocumentSnapshot) =
        (document.getLong(AMOUNT_KEY)?.toInt()
            ?: throw WrongFieldTypeException(AMOUNT_KEY))

    private fun getCategoryFrom(document: DocumentSnapshot) =
        (document.getString(CATEGORY_ID_KEY)
            ?: throw WrongFieldTypeException(CATEGORY_ID_KEY))
}
