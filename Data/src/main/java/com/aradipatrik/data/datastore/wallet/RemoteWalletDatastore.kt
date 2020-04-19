package com.aradipatrik.data.datastore.wallet

import com.aradipatrik.data.common.RemoteTimestampedDatastore
import com.aradipatrik.data.model.WalletDataModel

interface RemoteWalletDatastore : RemoteTimestampedDatastore<WalletDataModel>
