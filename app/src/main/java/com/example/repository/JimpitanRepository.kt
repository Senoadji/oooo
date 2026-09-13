package com.example.repository

import com.example.dao.CashConfigDao
import com.example.dao.ExpenseDao
import com.example.dao.JimpitanDao
import com.example.dao.ResidentDao
import com.example.dao.RondaAttendanceDao
import com.example.model.CashConfig
import com.example.model.DailyBalance
import com.example.model.ExpenseEntry
import com.example.model.GlobalFinancialReport
import com.example.model.JimpitanEntry
import com.example.model.MonthlyBalance
import com.example.model.Resident
import com.example.model.ResidentFinancialSummary
import com.example.model.ResidentRondaAttendanceSummary
import com.example.model.RondaAttendance
import com.example.model.RondaOverallStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JimpitanRepository(
    private val residentDao: ResidentDao,
    private val jimpitanDao: JimpitanDao,
    private val expenseDao: ExpenseDao,
    private val cashConfigDao: CashConfigDao,
    private val rondaAttendanceDao: RondaAttendanceDao
) {
    val allResidents: Flow<List<Resident>> = residentDao.getAllResidents()
    val allJimpitanEntries: Flow<List<JimpitanEntry>> = jimpitanDao.getAllEntries()
    val allExpenses: Flow<List<ExpenseEntry>> = expenseDao.getAllExpenses()
    val allRondaAttendances: Flow<List<RondaAttendance>> = rondaAttendanceDao.getAllAttendances()

    fun getStartingBalance(year: Int): Flow<String?> {
        return cashConfigDao.getConfigValue("STARTING_BALANCE_$year")
    }

    suspend fun setStartingBalance(year: Int, amount: Double) {
        cashConfigDao.setConfig(CashConfig("STARTING_BALANCE_$year", amount.toLong().toString()))
    }

    suspend fun insertResident(resident: Resident): Long = residentDao.insertResident(resident)
    suspend fun updateResident(resident: Resident) = residentDao.updateResident(resident)
    suspend fun deleteResident(resident: Resident) = residentDao.deleteResident(resident)

    suspend fun recordJimpitan(entry: JimpitanEntry): Long = jimpitanDao.insertEntry(entry)
    suspend fun recordBatchJimpitan(entries: List<JimpitanEntry>) = jimpitanDao.insertEntries(entries)
    suspend fun deleteJimpitan(id: Long) = jimpitanDao.deleteEntryById(id)
    suspend fun deleteJimpitanByResidentAndDate(residentId: Long, date: String) =
        jimpitanDao.deleteEntryByResidentAndDate(residentId, date)

    suspend fun updateResidentMonthlyDeposit(
        resident: Resident,
        year: Int,
        month: Int,
        newAmount: Double,
        date: String,
        collector: String,
        notes: String
    ) {
        val monthPrefix = String.format(Locale.US, "%d-%02d", year, month)
        jimpitanDao.deleteEntriesByResidentAndMonth(resident.id, monthPrefix)
        if (newAmount > 0.0) {
            jimpitanDao.insertEntry(
                JimpitanEntry(
                    residentId = resident.id,
                    houseNumber = resident.houseNumber,
                    residentName = resident.residentName,
                    date = date,
                    amount = newAmount,
                    collectorName = collector,
                    notes = notes
                )
            )
        }
    }

    suspend fun insertExpense(expense: ExpenseEntry): Long = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseEntry) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(id: Long) = expenseDao.deleteExpenseById(id)

    fun getEntriesByDate(date: String): Flow<List<JimpitanEntry>> = jimpitanDao.getEntriesByDate(date)
    fun getExpensesByDate(date: String): Flow<List<ExpenseEntry>> = expenseDao.getExpensesByDate(date)

    private val monthNames = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )

    fun getGlobalReport(year: Int): Flow<GlobalFinancialReport> {
        val yearPrefix = year.toString()
        val startingBalanceFlow = getStartingBalance(year)
        val jimpitanFlow = jimpitanDao.getEntriesByYear(yearPrefix)
        val expensesFlow = expenseDao.getExpensesByYear(yearPrefix)
        val residentsFlow = residentDao.getAllResidents()

        return combine(
            startingBalanceFlow,
            jimpitanFlow,
            expensesFlow,
            residentsFlow
        ) { startingBalStr, jimpitanList, expenseList, residents ->
            val startingBalanceYear = startingBalStr?.toDoubleOrNull() ?: 1500000.0
            
            // Calculate 12 cumulative months
            val monthlyBalances = mutableListOf<MonthlyBalance>()
            var runningBalance = startingBalanceYear
            var totalIncomeYear = 0.0
            var totalExpenseYear = 0.0

            for (m in 1..12) {
                val monthPrefix = String.format(Locale.US, "%d-%02d", year, m)
                val monthJimpitan = jimpitanList.filter { it.date.startsWith(monthPrefix) }
                val monthExpenses = expenseList.filter { it.date.startsWith(monthPrefix) }

                val monthIncome = monthJimpitan.sumOf { it.amount }
                val monthExpense = monthExpenses.sumOf { it.amount }

                totalIncomeYear += monthIncome
                totalExpenseYear += monthExpense

                val startOfMonthBalance = runningBalance
                val endOfMonthBalance = startOfMonthBalance + monthIncome - monthExpense
                runningBalance = endOfMonthBalance

                monthlyBalances.add(
                    MonthlyBalance(
                        year = year,
                        month = m,
                        monthName = monthNames[m - 1],
                        startingBalance = startOfMonthBalance,
                        totalIncome = monthIncome,
                        totalExpense = monthExpense,
                        netChange = monthIncome - monthExpense,
                        endingBalance = endOfMonthBalance,
                        jimpitanCount = monthJimpitan.size,
                        expenseCount = monthExpenses.size
                    )
                )
            }

            GlobalFinancialReport(
                year = year,
                startingBalanceYear = startingBalanceYear,
                totalIncomeYear = totalIncomeYear,
                totalExpenseYear = totalExpenseYear,
                currentBalance = runningBalance,
                monthlyBalances = monthlyBalances,
                totalHouseholds = residents.size,
                activeHouseholds = residents.count { it.isActive }
            )
        }
    }

    fun getResidentSummaries(year: Int, selectedMonth: Int? = null): Flow<List<ResidentFinancialSummary>> {
        val yearPrefix = year.toString()
        val residentsFlow = residentDao.getAllResidents()
        val jimpitanFlow = jimpitanDao.getEntriesByYear(yearPrefix)

        return combine(residentsFlow, jimpitanFlow) { residents, allEntries ->
            residents.map { res ->
                val resEntries = allEntries.filter { it.residentId == res.id }
                val totalPaidYear = resEntries.sumOf { it.amount }
                
                val currentMonthPrefix = if (selectedMonth != null) {
                    String.format(Locale.US, "%d-%02d", year, selectedMonth)
                } else {
                    val currentCal = Calendar.getInstance()
                    String.format(Locale.US, "%d-%02d", year, currentCal.get(Calendar.MONTH) + 1)
                }

                val totalPaidMonth = resEntries
                    .filter { it.date.startsWith(currentMonthPrefix) }
                    .sumOf { it.amount }

                val monthlyBreakdown = (1..12).associateWith { m ->
                    val mPrefix = String.format(Locale.US, "%d-%02d", year, m)
                    resEntries.filter { it.date.startsWith(mPrefix) }.sumOf { it.amount }
                }

                val lastDate = resEntries.maxByOrNull { it.date }?.date

                ResidentFinancialSummary(
                    resident = res,
                    totalPaidYear = totalPaidYear,
                    totalPaidMonth = totalPaidMonth,
                    paidDaysCount = resEntries.size,
                    lastPaymentDate = lastDate,
                    monthlyBreakdown = monthlyBreakdown,
                    transactions = resEntries.sortedByDescending { it.date }
                )
            }
        }
    }

    fun getDailyBalances(monthPrefix: String): Flow<List<DailyBalance>> {
        val jimpitanFlow = jimpitanDao.getEntriesByMonth(monthPrefix)
        val expensesFlow = expenseDao.getExpensesByMonth(monthPrefix)

        return combine(jimpitanFlow, expensesFlow) { jimpitanList, expenseList ->
            val allDates = (jimpitanList.map { it.date } + expenseList.map { it.date }).distinct().sortedDescending()
            
            allDates.map { d ->
                val dayJimpitan = jimpitanList.filter { it.date == d }
                val dayExpenses = expenseList.filter { it.date == d }
                val income = dayJimpitan.sumOf { it.amount }
                val expense = dayExpenses.sumOf { it.amount }

                DailyBalance(
                    date = d,
                    totalIncome = income,
                    totalExpense = expense,
                    netChange = income - expense,
                    jimpitanEntries = dayJimpitan,
                    expenseEntries = dayExpenses
                )
            }
        }
    }

    // Ronda & Denda Management
    fun getRondaAttendancesByDate(date: String): Flow<List<RondaAttendance>> =
        rondaAttendanceDao.getAttendancesByDate(date)

    suspend fun recordRondaAttendance(attendance: RondaAttendance): Long =
        rondaAttendanceDao.insertOrUpdate(attendance)

    suspend fun recordBatchRondaAttendance(attendances: List<RondaAttendance>) =
        rondaAttendanceDao.insertBatch(attendances)

    suspend fun payRondaFine(id: Long, paidDate: String) =
        rondaAttendanceDao.markFineAsPaid(id, paidDate)

    suspend fun payAllRondaFinesForResident(residentId: Long, paidDate: String) =
        rondaAttendanceDao.markAllFinesPaidForResident(residentId, paidDate)

    suspend fun deleteRondaAttendance(id: Long) =
        rondaAttendanceDao.deleteById(id)

    suspend fun deleteRondaAttendanceByDateAndResident(date: String, residentId: Long) =
        rondaAttendanceDao.deleteByDateAndResident(date, residentId)

    suspend fun getDefaultScheduledResidentIdsForDay(dayName: String): List<Long> {
        val configKey = "RONDA_SCHEDULE_${dayName.uppercase()}"
        val stored = cashConfigDao.getConfigValueDirect(configKey)
        if (!stored.isNullOrBlank()) {
            val parsed = stored.split(",").mapNotNull { it.trim().toLongOrNull() }
            if (parsed.isNotEmpty()) return parsed
        }
        return when (dayName) {
            "Senin" -> listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
            "Selasa" -> listOf(8L, 9L, 10L, 11L, 12L, 13L, 14L)
            "Rabu" -> listOf(15L, 16L, 17L, 18L, 19L, 20L, 21L)
            "Kamis" -> listOf(23L, 24L, 25L, 26L, 27L, 28L, 29L)
            "Jumat" -> listOf(30L, 31L, 32L, 33L, 34L, 35L, 36L)
            "Sabtu" -> listOf(45L, 46L, 47L, 48L, 49L, 50L, 51L)
            "Minggu" -> listOf(67L, 68L, 69L, 70L, 71L, 72L, 73L)
            else -> listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L)
        }
    }

    suspend fun saveWeeklyScheduleForDay(dayName: String, residentIds: List<Long>) {
        val configKey = "RONDA_SCHEDULE_${dayName.uppercase()}"
        cashConfigDao.setConfig(CashConfig(configKey, residentIds.joinToString(",")))
    }

    suspend fun ensureRondaScheduleForDate(date: String) {
        val existing = rondaAttendanceDao.getAttendancesByDateDirect(date)
        if (existing.isEmpty()) {
            val dayName = com.example.ui.components.Formatters.getDayOfWeekName(date)
            val defaultIds = getDefaultScheduledResidentIdsForDay(dayName)
            val allActive = residentDao.getActiveResidentsDirect()
            
            var selectedResidents = allActive.filter { defaultIds.contains(it.id) }
            if (selectedResidents.size < 7 && allActive.isNotEmpty()) {
                val needed = 7 - selectedResidents.size
                val others = allActive.filter { !defaultIds.contains(it.id) }.take(needed)
                selectedResidents = selectedResidents + others
            }
            
            val weekOfYear = com.example.ui.components.Formatters.getWeekOfYear(date)
            val year = com.example.ui.components.Formatters.getYearFromDate(date)
            val entries = selectedResidents.map { res ->
                RondaAttendance(
                    residentId = res.id,
                    houseNumber = res.houseNumber,
                    residentName = res.residentName,
                    date = date,
                    weekOfYear = weekOfYear,
                    year = year,
                    status = "TIDAK_HADIR", // Belum centang -> otomatis alpa
                    fineAmount = 20000.0, // Otomatis terkena denda Rp 20.000 jika tidak centang
                    isFinePaid = false,
                    notes = "Jadwal Regu $dayName (7 Petugas)",
                    recordedBy = "Sistem Ronda"
                )
            }
            if (entries.isNotEmpty()) {
                rondaAttendanceDao.insertBatch(entries)
            }
        }
    }

    fun getScheduledRondaOfficers(date: String): Flow<List<com.example.model.ScheduledRondaOfficer>> {
        return combine(
            allResidents,
            allRondaAttendances,
            rondaAttendanceDao.getAttendancesByDate(date)
        ) { residents, allAttendances, todayAttendances ->
            val residentMap = residents.associateBy { it.id }
            todayAttendances.mapIndexed { index, att ->
                val resident = residentMap[att.residentId] ?: Resident(
                    id = att.residentId,
                    houseNumber = att.houseNumber,
                    residentName = att.residentName,
                    phone = "",
                    status = "Tetap",
                    defaultAmount = 2000.0,
                    isActive = true
                )
                val isChecked = att.status == "HADIR"
                val isExcused = att.status == "IZIN"
                val fineAmount = if (isChecked || isExcused) 0.0 else 20000.0

                val pastUnpaidList = allAttendances.filter { 
                    it.residentId == att.residentId && 
                    it.date != date && 
                    it.status == "TIDAK_HADIR" && 
                    !it.isFinePaid 
                }
                val pastFine = pastUnpaidList.sumOf { it.fineAmount }

                com.example.model.ScheduledRondaOfficer(
                    orderNumber = index + 1,
                    resident = resident,
                    attendance = att,
                    isChecked = isChecked,
                    isExcused = isExcused,
                    fineAmount = fineAmount,
                    accumulatedPastFines = pastFine,
                    pastUnpaidCount = pastUnpaidList.size
                )
            }
        }
    }

    suspend fun toggleOfficerAttendance(resident: Resident, date: String, isChecked: Boolean) {
        val weekOfYear = com.example.ui.components.Formatters.getWeekOfYear(date)
        val year = com.example.ui.components.Formatters.getYearFromDate(date)
        val existing = rondaAttendanceDao.getAttendanceByDateAndResident(date, resident.id)
        
        val status = if (isChecked) "HADIR" else "TIDAK_HADIR"
        val fine = if (isChecked) 0.0 else 20000.0
        
        val entry = (existing ?: RondaAttendance(
            residentId = resident.id,
            houseNumber = resident.houseNumber,
            residentName = resident.residentName,
            date = date,
            weekOfYear = weekOfYear,
            year = year,
            status = status,
            fineAmount = fine,
            isFinePaid = false
        )).copy(
            status = status,
            fineAmount = fine,
            isFinePaid = false,
            timestamp = System.currentTimeMillis()
        )
        rondaAttendanceDao.insertOrUpdate(entry)
    }

    suspend fun setOfficerExcused(resident: Resident, date: String, reason: String = "Izin") {
        val weekOfYear = com.example.ui.components.Formatters.getWeekOfYear(date)
        val year = com.example.ui.components.Formatters.getYearFromDate(date)
        val existing = rondaAttendanceDao.getAttendanceByDateAndResident(date, resident.id)
        
        val entry = (existing ?: RondaAttendance(
            residentId = resident.id,
            houseNumber = resident.houseNumber,
            residentName = resident.residentName,
            date = date,
            weekOfYear = weekOfYear,
            year = year,
            status = "IZIN",
            fineAmount = 0.0,
            isFinePaid = false
        )).copy(
            status = "IZIN",
            fineAmount = 0.0,
            notes = reason,
            timestamp = System.currentTimeMillis()
        )
        rondaAttendanceDao.insertOrUpdate(entry)
    }

    suspend fun addOfficerToDate(resident: Resident, date: String) {
        val weekOfYear = com.example.ui.components.Formatters.getWeekOfYear(date)
        val year = com.example.ui.components.Formatters.getYearFromDate(date)
        val existing = rondaAttendanceDao.getAttendanceByDateAndResident(date, resident.id)
        if (existing == null) {
            val entry = RondaAttendance(
                residentId = resident.id,
                houseNumber = resident.houseNumber,
                residentName = resident.residentName,
                date = date,
                weekOfYear = weekOfYear,
                year = year,
                status = "TIDAK_HADIR", // Belum centang -> otomatis terkena denda Rp 20.000 sampai dicentang
                fineAmount = 20000.0,
                isFinePaid = false,
                notes = "Petugas Tambahan",
                recordedBy = "Pengurus Ronda"
            )
            rondaAttendanceDao.insertOrUpdate(entry)
        }
    }

    suspend fun removeOfficerFromDate(residentId: Long, date: String) {
        rondaAttendanceDao.deleteByDateAndResident(date, residentId)
    }

    suspend fun resetScheduleForDate(date: String) {
        val existing = rondaAttendanceDao.getAttendancesByDateDirect(date)
        existing.forEach {
            rondaAttendanceDao.deleteById(it.id)
        }
        ensureRondaScheduleForDate(date)
    }

    fun getResidentRondaSummaries(selectedDate: String): Flow<List<ResidentRondaAttendanceSummary>> {
        return combine(
            allResidents,
            allRondaAttendances
        ) { residents, allAttendances ->
            residents.map { resident ->
                val residentAttendances = allAttendances.filter { it.residentId == resident.id }
                val todayAtt = residentAttendances.find { it.date == selectedDate }
                
                val presentCount = residentAttendances.count { it.status == "HADIR" }
                val absentCount = residentAttendances.count { it.status == "TIDAK_HADIR" }
                val excusedCount = residentAttendances.count { it.status == "IZIN" }
                
                // Denda terakumulasi: semua sesi TIDAK_HADIR yang belum dibayar (isFinePaid = false)
                val unpaidList = residentAttendances.filter { it.status == "TIDAK_HADIR" && !it.isFinePaid }
                val accumulatedFine = unpaidList.sumOf { it.fineAmount }
                
                val paidList = residentAttendances.filter { it.status == "TIDAK_HADIR" && it.isFinePaid }
                val totalPaidFine = paidList.sumOf { it.fineAmount }

                ResidentRondaAttendanceSummary(
                    resident = resident,
                    todayAttendance = todayAtt,
                    totalPresent = presentCount,
                    totalAbsent = absentCount,
                    totalExcused = excusedCount,
                    accumulatedUnpaidFine = accumulatedFine,
                    unpaidWeeksCount = unpaidList.size,
                    totalPaidFine = totalPaidFine,
                    unpaidRecords = unpaidList,
                    history = residentAttendances
                )
            }
        }
    }

    fun getRondaOverallStats(selectedDate: String): Flow<RondaOverallStats> {
        return combine(
            allResidents,
            allRondaAttendances
        ) { residents, attendances ->
            val todayList = attendances.filter { it.date == selectedDate }
            val presentToday = todayList.count { it.status == "HADIR" }
            val absentToday = todayList.count { it.status == "TIDAK_HADIR" }
            val excusedToday = todayList.count { it.status == "IZIN" }

            val unpaidList = attendances.filter { it.status == "TIDAK_HADIR" && !it.isFinePaid }
            val totalAccumulatedUnpaid = unpaidList.sumOf { it.fineAmount }
            val uniqueUnpaidResidents = unpaidList.map { it.residentId }.distinct().size

            val paidList = attendances.filter { it.status == "TIDAK_HADIR" && it.isFinePaid }
            val totalPaidAllTime = paidList.sumOf { it.fineAmount }

            RondaOverallStats(
                recordedTodayCount = todayList.size,
                presentTodayCount = presentToday,
                absentTodayCount = absentToday,
                excusedTodayCount = excusedToday,
                totalAccumulatedUnpaidFines = totalAccumulatedUnpaid,
                totalUnpaidResidentsCount = uniqueUnpaidResidents,
                totalFinesCollectedAllTime = totalPaidAllTime
            )
        }
    }
}
