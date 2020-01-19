package com.aradipatrik.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aradipatrik.local.database.category.CategoryDao
import com.aradipatrik.local.database.category.CategoryRow
import com.aradipatrik.local.database.transaction.TransactionDao
import com.aradipatrik.local.database.transaction.TransactionRow
import javax.inject.Inject

const val DATABASE_NAME = "transaction.db"

@Database(
    entities = [TransactionRow::class, CategoryRow::class],
    version = 1
)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        private var instance: TransactionDatabase? = null
        private val lock = Any()

        fun getInstance(context: Context): TransactionDatabase {
            if (instance == null) {
                synchronized(lock) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            TransactionDatabase::class.java,
                            "projects.db"
                        )
                            .build()
                    }
                    return instance as TransactionDatabase
                }
            }
            return instance as TransactionDatabase
        }
    }
}