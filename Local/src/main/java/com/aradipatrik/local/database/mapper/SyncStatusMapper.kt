package com.aradipatrik.local.database.mapper

import com.aradipatrik.data.mapper.SyncStatus
import com.aradipatrik.local.database.common.SyncStatusConstants.NONE_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.SYNCED_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_ADD_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_DELETE_CODE
import com.aradipatrik.local.database.common.SyncStatusConstants.TO_UPDATE_CODE
import java.util.*

val SyncStatus.code: Int
    get() = when (this) {
        SyncStatus.Synced -> SYNCED_CODE
        SyncStatus.ToUpdate -> TO_UPDATE_CODE
        SyncStatus.ToAdd -> TO_ADD_CODE
        SyncStatus.ToDelete -> TO_DELETE_CODE
        SyncStatus.None -> NONE_CODE
    }

fun SyncStatus.Companion.fromCode(code: Int): SyncStatus =
    EnumSet.allOf(SyncStatus::class.java).first { it.code == code }
