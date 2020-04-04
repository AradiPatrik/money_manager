package com.aradipatrik.remote.payloadfactory

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.remote.*
import com.google.firebase.Timestamp

class TransactionPayloadFactory {
    fun createPayloadFrom(t: TransactionPartialEntity): HashMap<String, Any> = hashMapOf(
        AMOUNT_KEY to t.amount,
        UPDATED_TIMESTAMP_KEY to Timestamp.now(),
        DELETED_KEY to (t.syncStatus == SyncStatus.ToDelete),
        CATEGORY_ID_KEY to t.categoryId,
        DATE_KEY to t.date.toDate().time,
        MEMO_KEY to t.memo
    )
}
