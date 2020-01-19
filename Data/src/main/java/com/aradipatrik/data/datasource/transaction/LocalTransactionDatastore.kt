package com.aradipatrik.data.datasource.transaction

import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.data.common.CrudDataStore
import com.aradipatrik.data.common.LocalTimestampedDataStore
import io.reactivex.Observable
import org.joda.time.Interval


interface LocalTransactionDatastore :
    LocalTimestampedDataStore<TransactionPartialEntity>,
    CrudDataStore<TransactionPartialEntity, String> {
    fun getInInterval(interval: Interval): Observable<List<TransactionJoinedEntity>>
}
