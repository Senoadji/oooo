package com.example.ui.components

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Formatters {
    private val localeId = Locale("id", "ID")

    fun formatRupiah(amount: Double): String {
        val numberFormat = NumberFormat.getCurrencyInstance(localeId)
        numberFormat.maximumFractionDigits = 0
        return numberFormat.format(amount).replace("Rp", "Rp ")
    }

    fun formatRupiahCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> String.format(Locale.US, "Rp %.1fJt", amount / 1_000_000.0)
            amount >= 1_000 -> String.format(Locale.US, "Rp %.0fRb", amount / 1_000.0)
            else -> formatRupiah(amount)
        }
    }

    fun getCurrentDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }

    fun getCurrentYear(): Int {
        return Calendar.getInstance().get(Calendar.YEAR)
    }

    fun getCurrentMonth(): Int {
        return Calendar.getInstance().get(Calendar.MONTH) + 1
    }

    fun formatIndonesianDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateStr) ?: return dateStr
            val formatter = SimpleDateFormat("d MMMM yyyy", localeId)
            formatter.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatIndonesianDateShort(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateStr) ?: return dateStr
            val formatter = SimpleDateFormat("dd MMM yyyy", localeId)
            formatter.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatDayAndDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateStr) ?: return dateStr
            val formatter = SimpleDateFormat("EEEE, d MMMM yyyy", localeId)
            formatter.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun getMonthName(month: Int): String {
        val months = listOf(
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )
        return if (month in 1..12) months[month - 1] else "Bulan $month"
    }

    fun getWeekOfYear(dateStr: String): Int {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateStr) ?: return 1
            val cal = Calendar.getInstance()
            cal.time = date
            cal.get(Calendar.WEEK_OF_YEAR)
        } catch (e: Exception) {
            1
        }
    }

    fun getYearFromDate(dateStr: String): Int {
        return dateStr.substringBefore("-").toIntOrNull() ?: getCurrentYear()
    }

    fun getWeekLabel(dateStr: String): String {
        val week = getWeekOfYear(dateStr)
        val year = getYearFromDate(dateStr)
        return "Minggu ke-$week ($year)"
    }

    fun getDayOfWeekName(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = parser.parse(dateStr) ?: return "Senin"
            val formatter = SimpleDateFormat("EEEE", localeId)
            val day = formatter.format(date)
            // Capitalize first letter (e.g. Senin, Selasa)
            day.replaceFirstChar { if (it.isLowerCase()) it.titlecase(localeId) else it.toString() }
        } catch (e: Exception) {
            "Senin"
        }
    }

    fun getCurrentTimeString(): String {
        val sdf = SimpleDateFormat("HH:mm", localeId)
        return "${sdf.format(Date())} WIB"
    }

    data class RondaAccessStatus(
        val isAllowed: Boolean,
        val currentTimeFormatted: String,
        val currentDateFormatted: String,
        val isCorrectDate: Boolean,
        val isCorrectTime: Boolean,
        val statusMessage: String
    )

    fun getRondaAccessStatus(selectedDateStr: String): RondaAccessStatus {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        val timeFormat = SimpleDateFormat("HH:mm", localeId)
        val currentTimeStr = "${timeFormat.format(cal.time)} WIB"
        val todayDateStr = getCurrentDateString()

        val isTimeValid = (hour == 0) || (hour == 1 && minute == 0)

        val calYesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calYesterday.time)
        val isDateValid = (selectedDateStr == todayDateStr || selectedDateStr == yesterdayStr)

        val isAllowed = isTimeValid && isDateValid

        val message = when {
            isAllowed -> "Jam Presensi Ronda Aktif (00:00 - 01:00 WIB). Petugas jaga silakan centang kehadiran."
            !isDateValid -> "Bukan hari/tanggal ronda yang sedang berlangsung. Presensi hanya aktif pada hari & tanggal jadwal ronda pukul 00:00 - 01:00 WIB."
            else -> "Presensi terkunci. Ceklist daftar hadir hanya dibuka pukul 00:00 sampai 01:00 WIB (Saat ini pukul $currentTimeStr)."
        }

        return RondaAccessStatus(
            isAllowed = isAllowed,
            currentTimeFormatted = currentTimeStr,
            currentDateFormatted = formatDayAndDate(todayDateStr),
            isCorrectDate = isDateValid,
            isCorrectTime = isTimeValid,
            statusMessage = message
        )
    }

    fun isRondaChecklistTimeAllowed(selectedDateStr: String): Boolean {
        return getRondaAccessStatus(selectedDateStr).isAllowed
    }
}
