package com.aradipatrik.local.database.common

object TransactionConstants {
    const val TABLE_NAME = "transactions"
    const val ID_COLUMN_NAME = "uid"
    const val CATEGORY_ID_COLUMN_NAME = "category_uid"
    const val AMOUNT_COLUMN_NAME = "amount"
    const val MEMO_COLUMN_NAME = "memo"
    const val DATE_COLUMN_NAME = "date"
    const val UPDATE_TIMESTAMP_COLUMN_NAME = "update_timestamp"
    const val SYNC_STATUS_COLUMN_NAME = "sync_status"
}

object SyncStatusConstants {
    const val SYNCED_CODE = 0
    const val TO_UPDATE_CODE = 1
    const val TO_DELETE_CODE = 2
    const val TO_ADD_CODE = 3
    const val NONE_CODE = 4
}

object CategoryConstants {
    const val TABLE_NAME = "categories"
    const val ID_COLUMN_NAME = "uid"
    const val NAME_COLUMN_NAME = "name"
    const val ICON_ID_COLUMN_NAME = "icon_id"
    const val UPDATE_TIMESTAMP_COLUMN_NAME = "update_timestamp"
    const val SYNC_STATUS_COLUMN_NAME = "sync_status"
}
