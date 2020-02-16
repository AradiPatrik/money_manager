package com.aradipatrik.local.database.seed

import android.content.Context
import com.aradipatrik.local.R
import com.aradipatrik.local.database.TransactionDatabase
import com.aradipatrik.local.database.category.CategoryRow
import com.aradipatrik.local.database.common.SyncStatusConstants
import io.reactivex.Completable
import org.joda.time.DateTime
import java.io.InputStreamReader

object DatabaseSeeder {
    private lateinit var db: TransactionDatabase

    fun seedDatabase(db: TransactionDatabase, context: Context): Completable {
        this.db = db
        return context.resources.openRawResource(R.raw.category_seed).reader().use(::insertCategories)
    }

    private fun insertCategories(reader: InputStreamReader): Completable {
        val categoryNames = reader.readText().split(",")
        val categoryRows = categoryNames.map(::createRowFromName)
        return db.categoryDao().insert(categoryRows)
    }

    private fun createRowFromName(categoryName: String) = CategoryRow(
        uid = categoryName,
        name = categoryName,
        iconId = categoryName,
        updateTimestamp = System.currentTimeMillis(),
        syncStatusCode = SyncStatusConstants.TO_ADD_CODE
    )
}