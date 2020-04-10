package com.aradipatrik.data.datastore.wallet

import com.aradipatrik.data.common.RemoteTimestampedChildDatastore
import com.aradipatrik.data.model.WalletEntity

interface RemoteWalletDatastore : RemoteTimestampedChildDatastore<WalletEntity, String>
