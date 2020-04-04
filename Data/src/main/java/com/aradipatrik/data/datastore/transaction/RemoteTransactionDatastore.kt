package com.aradipatrik.data.datastore.transaction

import com.aradipatrik.data.common.RemoteTimestampedDatastore
import com.aradipatrik.data.model.TransactionPartialEntity

interface RemoteTransactionDatastore : RemoteTimestampedDatastore<TransactionPartialEntity>
