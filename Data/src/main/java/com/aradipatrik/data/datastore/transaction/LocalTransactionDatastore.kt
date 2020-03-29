package com.aradipatrik.data.datastore.transaction

import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.data.common.CrudDatastore
import com.aradipatrik.data.common.LocalTimestampedDatastore
import io.reactivex.Observable
import org.joda.time.Interval


interface LocalTransactionDatastore :
    LocalTimestampedDatastore<TransactionPartialEntity>,
    CrudDatastore<TransactionPartialEntity, String> {
    fun getInInterval(interval: Interval): Observable<List<TransactionJoinedEntity>>
}
