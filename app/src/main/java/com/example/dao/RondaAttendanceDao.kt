package com.example.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.RondaAttendance
import kotlinx.coroutines.flow.Flow

@Dao
interface RondaAttendanceDao {
    @Query("SELECT * FROM ronda_attendances ORDER BY date DESC, houseNumber ASC")
    fun getAllAttendances(): Flow<List<RondaAttendance>>

    @Query("SELECT * FROM ronda_attendances WHERE date = :date ORDER BY houseNumber ASC")
    fun getAttendancesByDate(date: String): Flow<List<RondaAttendance>>

    @Query("SELECT * FROM ronda_attendances WHERE date = :date ORDER BY id ASC")
    suspend fun getAttendancesByDateDirect(date: String): List<RondaAttendance>

    @Query("SELECT * FROM ronda_attendances WHERE residentId = :residentId ORDER BY date DESC")
    fun getAttendancesByResident(residentId: Long): Flow<List<RondaAttendance>>

    @Query("SELECT * FROM ronda_attendances WHERE year = :year ORDER BY weekOfYear DESC, date DESC")
    fun getAttendancesByYear(year: Int): Flow<List<RondaAttendance>>

    @Query("SELECT * FROM ronda_attendances WHERE status = 'TIDAK_HADIR' AND isFinePaid = 0 ORDER BY date ASC")
    fun getAllUnpaidFines(): Flow<List<RondaAttendance>>

    @Query("SELECT * FROM ronda_attendances WHERE residentId = :residentId AND status = 'TIDAK_HADIR' AND isFinePaid = 0 ORDER BY date ASC")
    fun getUnpaidFinesByResident(residentId: Long): Flow<List<RondaAttendance>>

    @Query("SELECT * FROM ronda_attendances WHERE date = :date AND residentId = :residentId LIMIT 1")
    suspend fun getAttendanceByDateAndResident(date: String, residentId: Long): RondaAttendance?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(attendance: RondaAttendance): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(attendances: List<RondaAttendance>)

    @Update
    suspend fun update(attendance: RondaAttendance)

    @Query("UPDATE ronda_attendances SET isFinePaid = 1, finePaidDate = :paidDate WHERE id = :id")
    suspend fun markFineAsPaid(id: Long, paidDate: String)

    @Query("UPDATE ronda_attendances SET isFinePaid = 1, finePaidDate = :paidDate WHERE residentId = :residentId AND status = 'TIDAK_HADIR' AND isFinePaid = 0")
    suspend fun markAllFinesPaidForResident(residentId: Long, paidDate: String)

    @Query("DELETE FROM ronda_attendances WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM ronda_attendances WHERE date = :date AND residentId = :residentId")
    suspend fun deleteByDateAndResident(date: String, residentId: Long)

    @Query("SELECT COUNT(*) FROM ronda_attendances")
    suspend fun getCount(): Int

    @Query("DELETE FROM ronda_attendances")
    suspend fun deleteAll()
}
