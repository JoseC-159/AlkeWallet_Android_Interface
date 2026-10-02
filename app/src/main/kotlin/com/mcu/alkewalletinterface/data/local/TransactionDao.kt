package com.mcu.alkewalletinterface.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions WHERE userEmail = :email ORDER BY id DESC")
    suspend fun getTransactionsForUser(email: String): List<TransactionEntity>
}
