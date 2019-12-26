package com.aradipatrik.data.repository.transaction

import com.aradipatrik.data.model.TransactionEntity
import com.aradipatrik.data.repository.common.RemoteTimestampedDataStore

interface RemoteTransactionRemoteDataStore
    : RemoteTimestampedDataStore<TransactionEntity>
