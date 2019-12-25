package com.aradipatrik.data.repository.transaction

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.repository.common.CrudDataStore
import com.aradipatrik.data.repository.common.LocalTimestampedDataStore
import io.reactivex.Observable
import org.joda.time.Interval


interface LocalTransactionDataStore :
    LocalTimestampedDataStore<TransactionEntity>,
    CrudDataStore<TransactionEntity, String> {
    fun getInInterval(interval: Interval): Observable<List<TransactionEntity>>
}
