package com.aradipatrik.local.database.model.stats

import androidx.room.Dao
import androidx.room.Query
import com.aradipatrik.local.database.common.CommonConstants
import com.aradipatrik.local.database.common.CommonConstants.WALLET_ID_COLUMN_NAME
import com.aradipatrik.local.database.common.TransactionConstants
import com.aradipatrik.local.database.common.TransactionConstants.AMOUNT_COLUMN_NAME
import com.aradipatrik.local.database.common.TransactionConstants.DATE_COLUMN_NAME
import com.aradipatrik.local.database.common.TransactionConstants.TABLE_NAME
import io.reactivex.Observable

@Dao
interface ExpenseStatsDao {
    companion object Queries {
        const val EXPENSE_IN_WALLET = "SELECT SUM(ABS($AMOUNT_COLUMN_NAME)) FROM $TABLE_NAME " +
            "WHERE $WALLET_ID_COLUMN_NAME = :walletId " +
            "AND $AMOUNT_COLUMN_NAME < 0"
        const val INCOME_IN_WALLET = "SELECT SUM($AMOUNT_COLUMN_NAME) FROM $TABLE_NAME " +
            "WHERE $WALLET_ID_COLUMN_NAME = :walletId " +
            "AND $AMOUNT_COLUMN_NAME > 0"

        const val EXPENSE_IN_WALLET_IN_INTERVAL =
            "SELECT SUM(ABS($AMOUNT_COLUMN_NAME)) FROM $TABLE_NAME " +
                "WHERE $WALLET_ID_COLUMN_NAME = :walletId " +
                "AND $AMOUNT_COLUMN_NAME < 0 " +
                "AND $DATE_COLUMN_NAME BETWEEN :begin AND :end"

        const val INCOME_IN_WALLET_IN_INTERVAL =
            "SELECT SUM($AMOUNT_COLUMN_NAME) FROM $TABLE_NAME " +
                "WHERE $WALLET_ID_COLUMN_NAME = :walletId " +
                "AND $AMOUNT_COLUMN_NAME > 0 " +
                "AND $DATE_COLUMN_NAME BETWEEN :begin AND :end"
    }

    @Query(EXPENSE_IN_WALLET)
    fun getExpenseInWallet(walletId: String): Observable<Int>

    @Query(INCOME_IN_WALLET)
    fun getIncomeInWallet(walletId: String): Observable<Int>

    @Query(EXPENSE_IN_WALLET_IN_INTERVAL)
    fun getExpenseInWalletInInterval(begin: Long, end: Long, walletId: String): Observable<Int>

    @Query(INCOME_IN_WALLET_IN_INTERVAL)
    fun getIncomeInWalletInInterval(begin: Long, end: Long, walletId: String): Observable<Int>
}
