package com.example.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.JimpitanEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface JimpitanDao {
    @Query("SELECT * FROM jimpitan_entries ORDER BY date DESC, timestamp DESC")
    fun getAllEntries(): Flow<List<JimpitanEntry>>

    @Query("SELECT * FROM jimpitan_entries WHERE date = :date ORDER BY houseNumber ASC")
    fun getEntriesByDate(date: String): Flow<List<JimpitanEntry>>

    @Query("SELECT * FROM jimpitan_entries WHERE date LIKE :monthPrefix || '%' ORDER BY date ASC")
    fun getEntriesByMonth(monthPrefix: String): Flow<List<JimpitanEntry>> // e.g. "2026-09"

    @Query("SELECT * FROM jimpitan_entries WHERE date LIKE :yearPrefix || '%' ORDER BY date ASC")
    fun getEntriesByYear(yearPrefix: String): Flow<List<JimpitanEntry>> // e.g. "2026"

    @Query("SELECT * FROM jimpitan_entries WHERE residentId = :residentId ORDER BY date DESC")
    fun getEntriesByResidentId(residentId: Long): Flow<List<JimpitanEntry>>

    @Query("SELECT * FROM jimpitan_entries WHERE houseNumber = :houseNumber ORDER BY date DESC")
    fun getEntriesByHouseNumber(houseNumber: String): Flow<List<JimpitanEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JimpitanEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<JimpitanEntry>)

    @Update
    suspend fun updateEntry(entry: JimpitanEntry)

    @Delete
    suspend fun deleteEntry(entry: JimpitanEntry)

    @Query("DELETE FROM jimpitan_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Query("DELETE FROM jimpitan_entries WHERE residentId = :residentId AND date = :date")
    suspend fun deleteEntryByResidentAndDate(residentId: Long, date: String)

    @Query("DELETE FROM jimpitan_entries WHERE residentId = :residentId AND date LIKE :monthPrefix || '%'")
    suspend fun deleteEntriesByResidentAndMonth(residentId: Long, monthPrefix: String)

    @Query("SELECT SUM(amount) FROM jimpitan_entries WHERE date LIKE :yearPrefix || '%'")
    fun getTotalIncomeByYear(yearPrefix: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM jimpitan_entries WHERE date LIKE :monthPrefix || '%'")
    fun getTotalIncomeByMonth(monthPrefix: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM jimpitan_entries WHERE date = :date")
    fun getTotalIncomeByDate(date: String): Flow<Double?>

    @Query("SELECT * FROM jimpitan_entries WHERE date = :date AND residentId = :residentId LIMIT 1")
    suspend fun getEntryByDateAndResident(date: String, residentId: Long): JimpitanEntry?

    @Query("DELETE FROM jimpitan_entries")
    suspend fun deleteAllEntries()
}
