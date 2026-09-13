package com.example.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Resident
import kotlinx.coroutines.flow.Flow

@Dao
interface ResidentDao {
    @Query("SELECT * FROM residents ORDER BY houseNumber ASC")
    fun getAllResidents(): Flow<List<Resident>>

    @Query("SELECT * FROM residents WHERE isActive = 1 ORDER BY houseNumber ASC")
    fun getActiveResidents(): Flow<List<Resident>>

    @Query("SELECT * FROM residents WHERE isActive = 1 ORDER BY houseNumber ASC")
    suspend fun getActiveResidentsDirect(): List<Resident>

    @Query("SELECT * FROM residents WHERE id = :id")
    suspend fun getResidentById(id: Long): Resident?

    @Query("SELECT * FROM residents WHERE houseNumber = :houseNumber LIMIT 1")
    suspend fun getResidentByHouseNumber(houseNumber: String): Resident?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResident(resident: Resident): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResidents(residents: List<Resident>)

    @Update
    suspend fun updateResident(resident: Resident)

    @Delete
    suspend fun deleteResident(resident: Resident)

    @Query("DELETE FROM residents WHERE id = :id")
    suspend fun deleteResidentById(id: Long)

    @Query("SELECT COUNT(*) FROM residents")
    suspend fun getResidentCount(): Int

    @Query("DELETE FROM residents")
    suspend fun deleteAllResidents()
}
