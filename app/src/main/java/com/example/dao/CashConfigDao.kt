package com.example.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.CashConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface CashConfigDao {
    @Query("SELECT value FROM cash_config WHERE `key` = :key LIMIT 1")
    fun getConfigValue(key: String): Flow<String?>

    @Query("SELECT value FROM cash_config WHERE `key` = :key LIMIT 1")
    suspend fun getConfigValueDirect(key: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setConfig(config: CashConfig)
}
