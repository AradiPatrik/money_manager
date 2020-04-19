package com.aradipatrik.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aradipatrik.local.database.model.category.CategoryDao
import com.aradipatrik.local.database.model.category.CategoryRow
import com.aradipatrik.local.database.model.transaction.TransactionDao
import com.aradipatrik.local.database.model.transaction.TransactionRow
import com.aradipatrik.local.database.model.wallet.WalletDao
import com.aradipatrik.local.database.model.wallet.WalletRow

const val DATABASE_NAME = "transaction.db"

@Database(
    entities = [TransactionRow::class, CategoryRow::class, WalletRow::class],
    version = 1
)
abstract class TransactionDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun walletDao(): WalletDao

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
                        ).build()
                    }
                    return instance as TransactionDatabase
                }
            }
            return instance!!
        }
    }
}
