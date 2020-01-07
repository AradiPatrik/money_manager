package com.aradipatrik.local.model

object TransactionConstants {
    const val TABLE_NAME = "transactions"
    const val ID_COLUMN_NAME = "uid"
    const val AMOUNT_COLUMN_NAME = "amount"
    const val MEMO_COLUMN_NAME = "memo"
    const val DATE_COLUMN_NAME = "date"
    const val UPDATE_TIMESTAMP_COLUMN_NAME = "update_timestamp"
    const val IS_PENDING = "is_pending"
}

object CategoryConstants {
    const val TABLE_NAME = "categories"
    const val ID_COLUMN_NAME = "uid"
    const val NAME_COLUMN_NAME = "name"
    const val ICON_ID_COLUMN_NAME = "icon_id"
    const val UPDATE_TIMESTAMP_COLUMN_NAME = "update_timestamp"
    const val IS_PENDING = "is_pending"
}
