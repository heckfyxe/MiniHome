package me.heckfyxe.mihome.data.local.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow
import me.heckfyxe.mihome.data.local.database.entities.Account

@Dao
interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(account: Account)

    @Query("SELECT * FROM Account")
    suspend fun getAccount(): Account?

    @Query("SELECT * FROM Account")
    fun getAccountFlow(): Flow<Account?>

    @Query("DELETE FROM Account")
    suspend fun delete()
}