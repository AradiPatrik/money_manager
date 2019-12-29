package com.aradipatrik.data.repository.transaction

import com.aradipatrik.data.model.TransactionJoinedEntity
import com.aradipatrik.data.model.TransactionPartialEntity
import com.aradipatrik.data.repository.common.CrudDataStore
import com.aradipatrik.data.repository.common.LocalTimestampedDataStore
import io.reactivex.Observable
import org.joda.time.Interval


interface LocalTransactionDataStore :
    LocalTimestampedDataStore<TransactionPartialEntity>,
    CrudDataStore<TransactionPartialEntity, String> {
    fun getInInterval(interval: Interval): Observable<List<TransactionJoinedEntity>>
}
