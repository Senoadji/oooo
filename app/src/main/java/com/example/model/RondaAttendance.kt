package com.example.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ronda_attendances",
    indices = [
        Index(value = ["date", "residentId"], unique = true),
        Index(value = ["residentId"]),
        Index(value = ["year", "weekOfYear"]),
        Index(value = ["status"]),
        Index(value = ["isFinePaid"])
    ]
)
data class RondaAttendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val residentId: Long,
    val houseNumber: String,
    val residentName: String,
    val date: String, // Format: YYYY-MM-DD
    val weekOfYear: Int, // Nomor minggu dalam tahun (1-53)
    val year: Int, // Tahun, misal 2026
    val status: String, // "HADIR", "TIDAK_HADIR", "IZIN"
    val fineAmount: Double = 20000.0, // Denda Rp 20.000 bila tidak hadir
    val isFinePaid: Boolean = false, // false = belum dibayar (terakumulasi), true = sudah dibayar
    val finePaidDate: String? = null,
    val notes: String = "",
    val recordedBy: String = "Petugas Ronda",
    val timestamp: Long = System.currentTimeMillis()
)

data class ResidentRondaAttendanceSummary(
    val resident: Resident,
    val todayAttendance: RondaAttendance?,
    val totalPresent: Int,
    val totalAbsent: Int,
    val totalExcused: Int,
    val accumulatedUnpaidFine: Double, // Total denda terakumulasi yang belum dibayar
    val unpaidWeeksCount: Int, // Jumlah minggu ketidakhadiran belum dibayar
    val totalPaidFine: Double,
    val unpaidRecords: List<RondaAttendance> = emptyList(),
    val history: List<RondaAttendance> = emptyList()
)

data class RondaOverallStats(
    val recordedTodayCount: Int,
    val presentTodayCount: Int,
    val absentTodayCount: Int,
    val excusedTodayCount: Int,
    val totalAccumulatedUnpaidFines: Double, // Total denda terakumulasi seluruh warga yang belum dibayar
    val totalUnpaidResidentsCount: Int, // Berapa warga yang punya tunggakan denda
    val totalFinesCollectedAllTime: Double // Total denda yang sudah dibayarkan dan masuk kas
)

data class ScheduledRondaOfficer(
    val orderNumber: Int,
    val resident: Resident,
    val attendance: RondaAttendance,
    val isChecked: Boolean, // attendance.status == "HADIR"
    val isExcused: Boolean, // attendance.status == "IZIN"
    val fineAmount: Double, // 20000.0 jika tidak dicentang, 0.0 jika dicentang atau izin
    val accumulatedPastFines: Double = 0.0,
    val pastUnpaidCount: Int = 0
)
