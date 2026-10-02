package com.mcu.alkewalletinterface.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE userEmail = :email LIMIT 1")
    suspend fun getAccountForUser(email: String): AccountEntity?
}
