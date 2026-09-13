package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.JimpitanDatabase
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
import com.example.model.ScheduledRondaOfficer
import com.example.model.dawisName
import com.example.repository.JimpitanRepository
import com.example.ui.components.Formatters
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class JimpitanViewModel(application: Application) : AndroidViewModel(application) {
    private val database = JimpitanDatabase.getDatabase(application, viewModelScope)
    private val repository = JimpitanRepository(
        database.residentDao(),
        database.jimpitanDao(),
        database.expenseDao(),
        database.cashConfigDao(),
        database.rondaAttendanceDao()
    )

    private val _selectedYear = MutableStateFlow(Formatters.getCurrentYear())
    val selectedYear: StateFlow<Int> = _selectedYear.asStateFlow()

    private val _selectedMonth = MutableStateFlow(Formatters.getCurrentMonth())
    val selectedMonth: StateFlow<Int> = _selectedMonth.asStateFlow()

    private val _selectedDate = MutableStateFlow(Formatters.getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _expenseCategoryFilter = MutableStateFlow<String?>(null)
    val expenseCategoryFilter: StateFlow<String?> = _expenseCategoryFilter.asStateFlow()

    private val _activeChecklistTab = MutableStateFlow(0)
    val activeChecklistTab: StateFlow<Int> = _activeChecklistTab.asStateFlow()

    fun setActiveChecklistTab(tab: Int) {
        _activeChecklistTab.value = tab
    }

    val allResidents: StateFlow<List<Resident>> = repository.allResidents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Role-based Access Control (Mode Warga / Publik vs Mode Admin & Petugas)
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

    private val _adminPin = MutableStateFlow("0606") // Default PIN RT 06
    val adminPin: StateFlow<String> = _adminPin.asStateFlow()

    fun loginAdmin(enteredPin: String): Boolean {
        if (enteredPin.trim() == _adminPin.value) {
            _isAdminLoggedIn.value = true
            return true
        }
        return false
    }

    fun logoutAdmin() {
        _isAdminLoggedIn.value = false
    }

    fun updateAdminPin(oldPin: String, newPin: String): Boolean {
        if (oldPin.trim() == _adminPin.value && newPin.trim().length >= 4) {
            _adminPin.value = newPin.trim()
            return true
        }
        return false
    }

    val vacantResidents: StateFlow<List<Resident>> = allResidents
        .map { list -> list.filter { it.status.equals("Kosong", ignoreCase = true) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val globalReport: StateFlow<GlobalFinancialReport> = _selectedYear
        .flatMapLatest { year -> repository.getGlobalReport(year) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            GlobalFinancialReport(
                year = Formatters.getCurrentYear(),
                startingBalanceYear = 1500000.0,
                totalIncomeYear = 0.0,
                totalExpenseYear = 0.0,
                currentBalance = 1500000.0,
                monthlyBalances = emptyList(),
                totalHouseholds = 0,
                activeHouseholds = 0
            )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val residentSummaries: StateFlow<List<ResidentFinancialSummary>> = combine(
        _selectedYear,
        _selectedMonth
    ) { year, month -> Pair(year, month) }
        .flatMapLatest { (year, month) -> repository.getResidentSummaries(year, month) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val dailyBalances: StateFlow<List<DailyBalance>> = combine(_selectedYear, _selectedMonth) { y, m ->
        String.format(Locale.US, "%d-%02d", y, m)
    }.flatMapLatest { monthPrefix ->
        repository.getDailyBalances(monthPrefix)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedDateEntries: StateFlow<List<JimpitanEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getEntriesByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedDateExpenses: StateFlow<List<ExpenseEntry>> = _selectedDate
        .flatMapLatest { date -> repository.getExpensesByDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExpenses: StateFlow<List<ExpenseEntry>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val rondaSummaries: StateFlow<List<ResidentRondaAttendanceSummary>> = _selectedDate
        .flatMapLatest { date -> repository.getResidentRondaSummaries(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val scheduledOfficers: StateFlow<List<ScheduledRondaOfficer>> = _selectedDate
        .flatMapLatest { date -> repository.getScheduledRondaOfficers(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val rondaOverallStats: StateFlow<RondaOverallStats> = _selectedDate
        .flatMapLatest { date -> repository.getRondaOverallStats(date) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            RondaOverallStats(0, 0, 0, 0, 0.0, 0, 0.0)
        )

    init {
        viewModelScope.launch {
            _selectedDate.collect { date ->
                repository.ensureRondaScheduleForDate(date)
            }
        }
        viewModelScope.launch {
            allResidents.collect { residents ->
                if (residents.isNotEmpty() && residents.none { it.status.equals("Kosong", ignoreCase = true) }) {
                    // Beri 3 contoh rumah kosong realistis di RT 06 agar panel langsung aktif & terisi
                    val sampleVacant = listOf(
                        Triple("DW1-15", "Pemilik dinas di Jakarta (Bpk. Rudi). Kunci dititip ke Pak RT. Pantau gembok depan & lampu teras.", "081298765432"),
                        Triple("DW2-18", "Tahap renovasi interior. Listrik & air dimatikan. Rutin ditengok kontraktor tiap siang.", "081387654321"),
                        Triple("DW3-10", "Rumah kosong dalam proses jual/sewa. Pagar terkunci gembok ganda. Lampu otomatis teras malam.", "081776543210")
                    )
                    sampleVacant.forEach { (houseNo, notes, phone) ->
                        val target = residents.find { it.houseNumber.equals(houseNo, ignoreCase = true) }
                        if (target != null) {
                            repository.updateResident(
                                target.copy(
                                    status = "Kosong",
                                    notes = notes,
                                    phone = phone,
                                    defaultAmount = 0.0
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    fun setSelectedYear(year: Int) {
        _selectedYear.value = year
    }

    fun setSelectedMonth(month: Int) {
        _selectedMonth.value = month
    }

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setExpenseCategoryFilter(category: String?) {
        _expenseCategoryFilter.value = category
    }

    // Resident Management
    fun addResident(houseNumber: String, name: String, phone: String, status: String, defaultAmount: Double, notes: String) {
        viewModelScope.launch {
            repository.insertResident(
                Resident(
                    houseNumber = houseNumber.trim().uppercase(),
                    residentName = name.trim(),
                    phone = phone.trim(),
                    status = status,
                    defaultAmount = defaultAmount,
                    notes = notes.trim()
                )
            )
        }
    }

    fun updateResident(resident: Resident) {
        viewModelScope.launch {
            repository.updateResident(resident)
        }
    }

    fun deleteResident(resident: Resident) {
        viewModelScope.launch {
            repository.deleteResident(resident)
        }
    }

    // Jimpitan Collections
    fun toggleDailyJimpitan(resident: Resident, date: String, collector: String = "Petugas Ronda") {
        viewModelScope.launch {
            val currentEntries = selectedDateEntries.value
            val existing = currentEntries.find { it.residentId == resident.id }
            if (existing != null) {
                repository.deleteJimpitan(existing.id)
            } else {
                repository.recordJimpitan(
                    JimpitanEntry(
                        residentId = resident.id,
                        houseNumber = resident.houseNumber,
                        residentName = resident.residentName,
                        date = date,
                        amount = resident.defaultAmount,
                        collectorName = collector,
                        notes = "Jimpitan harian"
                    )
                )
            }
        }
    }

    fun recordCustomJimpitan(
        resident: Resident,
        date: String,
        amount: Double,
        collector: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.recordJimpitan(
                JimpitanEntry(
                    residentId = resident.id,
                    houseNumber = resident.houseNumber,
                    residentName = resident.residentName,
                    date = date,
                    amount = amount,
                    collectorName = collector.ifBlank { "Petugas Ronda" },
                    notes = notes.trim()
                )
            )
        }
    }

    fun updateResidentMonthlyDeposit(
        resident: Resident,
        year: Int,
        month: Int,
        amount: Double,
        date: String? = null,
        collector: String = "Bendahara RT",
        notes: String = "Penyesuaian rekap setoran bulanan"
    ) {
        viewModelScope.launch {
            val actualDate = if (!date.isNullOrBlank()) {
                date.trim()
            } else {
                String.format(Locale.US, "%d-%02d-15", year, month)
            }
            repository.updateResidentMonthlyDeposit(
                resident = resident,
                year = year,
                month = month,
                newAmount = amount,
                date = actualDate,
                collector = collector.ifBlank { "Bendahara RT" },
                notes = notes.ifBlank { "Penyesuaian rekap setoran bulanan" }
            )
        }
    }

    fun recordMonthlyAdjustment(
        year: Int,
        month: Int,
        incomeAdjustment: Double,
        expenseAdjustment: Double,
        notes: String
    ) {
        viewModelScope.launch {
            val date = String.format(Locale.US, "%d-%02d-28", year, month)
            if (incomeAdjustment > 0.0) {
                repository.recordJimpitan(
                    JimpitanEntry(
                        residentId = 0L,
                        houseNumber = "KAS-RT",
                        residentName = "Penyesuaian Kas Masuk RT 06",
                        date = date,
                        amount = incomeAdjustment,
                        collectorName = "Bendahara RT",
                        notes = notes.ifBlank { "Penyesuaian setoran bulanan" }
                    )
                )
            }
            if (expenseAdjustment > 0.0) {
                repository.insertExpense(
                    ExpenseEntry(
                        date = date,
                        category = "Lain-lain",
                        amount = expenseAdjustment,
                        description = notes.ifBlank { "Penyesuaian pengeluaran kas bulanan" },
                        receiptNotes = "Penyesuaian Rekap"
                    )
                )
            }
        }
    }

    fun markAllHousesPaidForDate(date: String, collector: String = "Petugas Ronda", dawis: String = "Semua") {
        viewModelScope.launch {
            val residents = allResidents.value.filter { 
                it.isActive && (dawis == "Semua" || it.dawisName == dawis)
            }
            val currentEntries = selectedDateEntries.value
            val entriesToAdd = mutableListOf<JimpitanEntry>()

            for (r in residents) {
                if (currentEntries.none { it.residentId == r.id }) {
                    entriesToAdd.add(
                        JimpitanEntry(
                            residentId = r.id,
                            houseNumber = r.houseNumber,
                            residentName = r.residentName,
                            date = date,
                            amount = r.defaultAmount,
                            collectorName = collector,
                            notes = if (dawis == "Semua") "Tandai lunas serentak" else "Tandai lunas $dawis"
                        )
                    )
                }
            }
            if (entriesToAdd.isNotEmpty()) {
                repository.recordBatchJimpitan(entriesToAdd)
            }
        }
    }

    fun deleteJimpitanEntry(id: Long) {
        viewModelScope.launch {
            repository.deleteJimpitan(id)
        }
    }

    // Expense Management
    fun addExpense(date: String, category: String, amount: Double, description: String, receiptNotes: String) {
        viewModelScope.launch {
            repository.insertExpense(
                ExpenseEntry(
                    date = date,
                    category = category,
                    amount = amount,
                    description = description.trim(),
                    receiptNotes = receiptNotes.trim()
                )
            )
        }
    }

    fun updateExpense(expense: ExpenseEntry) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun updateStartingBalance(year: Int, amount: Double) {
        viewModelScope.launch {
            repository.setStartingBalance(year, amount)
        }
    }

    // WhatsApp Report Generator
    fun shareMonthlyReport(context: Context, monthBalance: MonthlyBalance) {
        val report = buildString {
            appendLine("📢 *LAPORAN KAS JIMPITAN WARGA*")
            appendLine("🏡 *RT 06 RW 06 PERUMAHAN KCVRI*")
            appendLine("📍 *Berkoh - Purwokerto Selatan*")
            appendLine("🗓️ Periode: *${monthBalance.monthName} ${monthBalance.year}*")
            appendLine("--------------------------------------")
            appendLine("💰 *Saldo Awal Bulan* : ${Formatters.formatRupiah(monthBalance.startingBalance)}")
            appendLine("📥 *Total Pemasukan*   : ${Formatters.formatRupiah(monthBalance.totalIncome)} (${monthBalance.jimpitanCount} setoran)")
            appendLine("📤 *Total Pengeluaran* : ${Formatters.formatRupiah(monthBalance.totalExpense)} (${monthBalance.expenseCount} pos)")
            appendLine("--------------------------------------")
            appendLine("💵 *SALDO AKHIR BULAN* : *${Formatters.formatRupiah(monthBalance.endingBalance)}*")
            appendLine("_(Saldo akhir ini akan menjadi saldo awal ${Formatters.getMonthName(monthBalance.month % 12 + 1)})_")
            appendLine("--------------------------------------")
            appendLine("Terima kasih atas partisipasi dan kepedulian seluruh warga RT 06 RW 06 KCVRI Berkoh.")
            appendLine("Guyub Rukun Mbangun Lingkungan! 🤝✨")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan Kas Jimpitan")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun shareYearlyReport(context: Context, report: GlobalFinancialReport) {
        val text = buildString {
            appendLine("📊 *REKAPITULASI TAHUNAN KAS JIMPITAN ${report.year}*")
            appendLine("🏡 *RT 06 RW 06 PERUMAHAN KCVRI*")
            appendLine("📍 *Berkoh - Purwokerto Selatan*")
            appendLine("--------------------------------------")
            appendLine("🔹 Saldo Awal Tahun (${report.year}) : ${Formatters.formatRupiah(report.startingBalanceYear)}")
            appendLine("🔹 Total Pemasukan Tahunan : ${Formatters.formatRupiah(report.totalIncomeYear)}")
            appendLine("🔹 Total Pengeluaran Tahunan : ${Formatters.formatRupiah(report.totalExpenseYear)}")
            appendLine("🔹 *SALDO KAS TERKINI*     : *${Formatters.formatRupiah(report.currentBalance)}*")
            appendLine("--------------------------------------")
            appendLine("📈 *Rincian Akumulasi Berantai Per Bulan:*")
            report.monthlyBalances.forEach { mb ->
                if (mb.totalIncome > 0 || mb.totalExpense > 0 || mb.month <= Formatters.getCurrentMonth()) {
                    appendLine("• *${mb.monthName}*: Awal ${Formatters.formatRupiahCompact(mb.startingBalance)} | +${Formatters.formatRupiahCompact(mb.totalIncome)} | -${Formatters.formatRupiahCompact(mb.totalExpense)} ➔ *Akhir ${Formatters.formatRupiahCompact(mb.endingBalance)}*")
                }
            }
            appendLine("--------------------------------------")
            appendLine("Total Warga Terdaftar: ${report.totalHouseholds} Rumah")
            appendLine("Salam Guyub Rukun RT 06 RW 06 KCVRI Berkoh.")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Rekapitulasi Kas Jimpitan")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    // Ronda Attendance & Denda Management
    fun toggleOfficerAttendance(officer: ScheduledRondaOfficer, isChecked: Boolean) {
        viewModelScope.launch {
            repository.toggleOfficerAttendance(officer.resident, _selectedDate.value, isChecked)
        }
    }

    fun setOfficerExcused(officer: ScheduledRondaOfficer, reason: String = "Izin") {
        viewModelScope.launch {
            repository.setOfficerExcused(officer.resident, _selectedDate.value, reason)
        }
    }

    fun addOfficerToSchedule(resident: Resident) {
        viewModelScope.launch {
            repository.addOfficerToDate(resident, _selectedDate.value)
        }
    }

    fun removeOfficerFromSchedule(residentId: Long) {
        viewModelScope.launch {
            repository.removeOfficerFromDate(residentId, _selectedDate.value)
        }
    }

    fun resetScheduleToDefault() {
        viewModelScope.launch {
            repository.resetScheduleForDate(_selectedDate.value)
        }
    }

    fun markAllScheduledPresent() {
        viewModelScope.launch {
            val list = scheduledOfficers.value
            list.forEach { off ->
                repository.toggleOfficerAttendance(off.resident, _selectedDate.value, true)
            }
        }
    }

    fun markAllScheduledAbsent() {
        viewModelScope.launch {
            val list = scheduledOfficers.value
            list.forEach { off ->
                repository.toggleOfficerAttendance(off.resident, _selectedDate.value, false)
            }
        }
    }

    fun saveWeeklySchedule(dayName: String, residentIds: List<Long>) {
        viewModelScope.launch {
            repository.saveWeeklyScheduleForDay(dayName, residentIds)
            val currentDay = Formatters.getDayOfWeekName(_selectedDate.value)
            if (currentDay.equals(dayName, ignoreCase = true)) {
                repository.resetScheduleForDate(_selectedDate.value)
            }
        }
    }

    fun loadWeeklyScheduleIds(dayName: String, onResult: (List<Long>) -> Unit) {
        viewModelScope.launch {
            val ids = repository.getDefaultScheduledResidentIdsForDay(dayName)
            onResult(ids)
        }
    }

    fun setRondaStatus(
        resident: Resident,
        date: String,
        status: String, // "HADIR", "TIDAK_HADIR", "IZIN"
        notes: String = "",
        recordedBy: String = "Petugas Ronda"
    ) {
        viewModelScope.launch {
            val weekOfYear = Formatters.getWeekOfYear(date)
            val year = Formatters.getYearFromDate(date)
            // Denda 20.000 jika tidak hadir ronda
            val fineAmount = if (status == "TIDAK_HADIR") 20000.0 else 0.0
            
            val entry = RondaAttendance(
                residentId = resident.id,
                houseNumber = resident.houseNumber,
                residentName = resident.residentName,
                date = date,
                weekOfYear = weekOfYear,
                year = year,
                status = status,
                fineAmount = fineAmount,
                isFinePaid = false,
                notes = notes.trim(),
                recordedBy = recordedBy
            )
            repository.recordRondaAttendance(entry)
        }
    }

    fun removeRondaAttendance(residentId: Long, date: String) {
        viewModelScope.launch {
            repository.deleteRondaAttendanceByDateAndResident(date, residentId)
        }
    }

    fun markAllRondaPresent(date: String, dawis: String = "Semua", recordedBy: String = "Petugas Ronda") {
        viewModelScope.launch {
            val weekOfYear = Formatters.getWeekOfYear(date)
            val year = Formatters.getYearFromDate(date)
            val residents = allResidents.value.filter {
                it.isActive && (dawis == "Semua" || it.dawisName == dawis)
            }
            val entries = residents.map { r ->
                RondaAttendance(
                    residentId = r.id,
                    houseNumber = r.houseNumber,
                    residentName = r.residentName,
                    date = date,
                    weekOfYear = weekOfYear,
                    year = year,
                    status = "HADIR",
                    fineAmount = 0.0,
                    isFinePaid = false,
                    notes = if (dawis == "Semua") "Hadir bersama" else "Hadir regu $dawis",
                    recordedBy = recordedBy
                )
            }
            repository.recordBatchRondaAttendance(entries)
        }
    }

    fun paySingleFine(attendanceId: Long, residentName: String, paidDate: String = Formatters.getCurrentDateString()) {
        viewModelScope.launch {
            repository.payRondaFine(attendanceId, paidDate)
        }
    }

    fun payAllAccumulatedFines(resident: Resident, paidDate: String = Formatters.getCurrentDateString()) {
        viewModelScope.launch {
            repository.payAllRondaFinesForResident(resident.id, paidDate)
        }
    }

    fun shareRondaReport(context: Context, date: String) {
        val officers = scheduledOfficers.value
        val summaries = rondaSummaries.value
        val stats = rondaOverallStats.value
        val formattedDate = Formatters.formatDayAndDate(date)
        val weekOfYear = Formatters.getWeekOfYear(date)
        val dayName = Formatters.getDayOfWeekName(date)

        val report = buildString {
            appendLine("📋 *DAFTAR HADIR & REKAP DENDA RONDA*")
            appendLine("🏡 *RT 06 RW 06 PERUMAHAN KCVRI BERKOH*")
            appendLine("🗓️ Hari / Tanggal: *$formattedDate* (Minggu ke-$weekOfYear)")
            appendLine("👥 Regu Ronda: *${officers.size} Petugas Terjadwal* (Regu $dayName)")
            appendLine("======================================")
            appendLine("📝 *KOLOM DAFTAR HADIR PETUGAS:*")
            if (officers.isEmpty()) {
                appendLine("_(Belum ada petugas terjadwal)_")
            } else {
                officers.forEachIndexed { index, off ->
                    val num = index + 1
                    val icon = when {
                        off.isChecked -> "✅ [CENTANG HADIR]"
                        off.isExcused -> "🟡 [IZIN / SAKIT]"
                        else -> "❌ [TIDAK CENTANG / ALPA]"
                    }
                    val dendaLabel = when {
                        off.isChecked -> "Bebas Denda"
                        off.isExcused -> "Bebas Denda (Izin)"
                        else -> "⚠️ Denda Rp 20.000"
                    }
                    appendLine("$num. $icon")
                    appendLine("   👤 ${off.resident.houseNumber} ${off.resident.residentName} (${off.resident.dawisName})")
                    appendLine("   📌 Status: $dendaLabel")
                    if (off.accumulatedPastFines > 0) {
                        appendLine("   ⚠️ Tunggakan Denda Lalu: ${Formatters.formatRupiah(off.accumulatedPastFines)} (${off.pastUnpaidCount}x absen)")
                    }
                }
            }
            appendLine("======================================")
            appendLine("📊 *REKAP MALAM INI:*")
            val presentCount = officers.count { it.isChecked }
            val absentCount = officers.count { !it.isChecked && !it.isExcused }
            val excusedCount = officers.count { it.isExcused }
            appendLine("🟢 Hadir (Centang)       : $presentCount Orang")
            appendLine("🔴 Tidak Hadir (Denda)   : $absentCount Orang (Rp ${Formatters.formatRupiahCompact(absentCount * 20000.0)})")
            appendLine("🟡 Izin / Sakit          : $excusedCount Orang")
            appendLine("--------------------------------------")
            appendLine("⚠️ *TOTAL DENDA TERTUNGGAK SELURUH WARGA:*")
            appendLine("👉 *${Formatters.formatRupiah(stats.totalAccumulatedUnpaidFines)}* (${stats.totalUnpaidResidentsCount} warga)")
            appendLine("--------------------------------------")
            appendLine("📌 *Warga dengan Denda Ronda Tertunggak:*")
            val unpaidResidents = summaries.filter { it.accumulatedUnpaidFine > 0 }
            if (unpaidResidents.isEmpty()) {
                appendLine("✅ Nihil / Tidak ada tunggakan denda ronda.")
            } else {
                unpaidResidents.forEach { r ->
                    appendLine("• ${r.resident.houseNumber} ${r.resident.residentName} (${r.resident.dawisName}): *${Formatters.formatRupiah(r.accumulatedUnpaidFine)}* [${r.unpaidWeeksCount}x absen]")
                }
            }
            appendLine("--------------------------------------")
            appendLine("Aturan RT 06: Petugas hadir wajib centang nama pada kolom daftar hadir. Yang tidak centang otomatis terkena denda ronda Rp 20.000 terakumulasi setiap minggu bila belum dibayarkan.")
            appendLine("Guyub Rukun, Aman, & Tentram! 🛡️🤝")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Presensi & Denda Ronda")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    // Vacant House Management (Rumah Kosong)
    fun setHouseVacantStatus(
        resident: Resident,
        isVacant: Boolean,
        vacantReason: String = "",
        emergencyContact: String = ""
    ) {
        viewModelScope.launch {
            val newStatus = if (isVacant) "Kosong" else "Tetap"
            val newNotes = if (isVacant) {
                val reasonPart = if (vacantReason.isNotBlank()) "Rumah Kosong ($vacantReason)" else "Rumah Kosong"
                val contactPart = if (emergencyContact.isNotBlank()) " - Kontak: $emergencyContact" else ""
                "$reasonPart$contactPart"
            } else {
                resident.notes.replace(Regex("Rumah Kosong[^|]*", RegexOption.IGNORE_CASE), "").trim().ifBlank { "Penghuni Tetap" }
            }
            val updated = resident.copy(
                status = newStatus,
                notes = newNotes,
                defaultAmount = if (isVacant) 0.0 else 2000.0
            )
            repository.updateResident(updated)
        }
    }

    fun markHouseVacantById(
        residentId: Long,
        vacantReason: String,
        emergencyContact: String,
        notes: String
    ) {
        viewModelScope.launch {
            val resident = allResidents.value.find { it.id == residentId } ?: return@launch
            val fullNotes = buildString {
                append("Rumah Kosong")
                if (vacantReason.isNotBlank()) append(" ($vacantReason)")
                if (emergencyContact.isNotBlank()) append(" - Kontak: $emergencyContact")
                if (notes.isNotBlank()) append(" | $notes")
            }
            val updated = resident.copy(
                status = "Kosong",
                notes = fullNotes,
                phone = if (emergencyContact.isNotBlank()) emergencyContact else resident.phone,
                defaultAmount = 0.0
            )
            repository.updateResident(updated)
        }
    }

    fun restoreHouseFromVacant(
        residentId: Long,
        newStatus: String = "Tetap",
        residentName: String = "",
        phone: String = ""
    ) {
        viewModelScope.launch {
            val resident = allResidents.value.find { it.id == residentId } ?: return@launch
            val updated = resident.copy(
                status = newStatus,
                residentName = residentName.ifBlank { resident.residentName },
                phone = phone.ifBlank { resident.phone },
                notes = "Penghuni $newStatus",
                defaultAmount = 2000.0
            )
            repository.updateResident(updated)
        }
    }

    fun shareVacantHousesReport(context: Context) {
        val vacantList = vacantResidents.value
        val report = buildString {
            appendLine("🏠 *DAFTAR RUMAH KOSONG & PANTAUAN RONDA*")
            appendLine("🏡 *RT 06 RW 06 PERUMAHAN KCVRI BERKOH*")
            appendLine("🗓️ Per Tanggal: *${Formatters.formatDayAndDate(Formatters.getCurrentDateString())}*")
            appendLine("======================================")
            appendLine("Total Rumah Kosong Terdata: *${vacantList.size} Rumah*")
            appendLine("Mohon petugas ronda malam memantau keamanan gembok pagar, lampu penerangan, dan kondisi lingkungan rumah-rumah ini:")
            appendLine("--------------------------------------")
            if (vacantList.isEmpty()) {
                appendLine("✅ Tidak ada rumah kosong tercatat di RT 06.")
            } else {
                vacantList.forEachIndexed { index, r ->
                    val num = index + 1
                    appendLine("$num. 📍 *${r.houseNumber}* - ${r.residentName} (${r.dawisName})")
                    if (r.phone.isNotBlank()) {
                        appendLine("   📞 Kontak Pemilik: ${r.phone}")
                    }
                    if (r.notes.isNotBlank()) {
                        appendLine("   📝 Catatan/Kondisi: ${r.notes}")
                    }
                    appendLine("   🛡️ Status Ronda: Wajib Cek Pagar & Lampu")
                }
            }
            appendLine("======================================")
            appendLine("Petugas Ronda RT 06: Jika ada hal mencurigakan segera hubungi pemilik rumah atau Ketua RT.")
            appendLine("Aman, Guyub, & Waspada! 🛡️✨")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, report)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Bagikan Daftar Rumah Kosong ke WA Ronda")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }
}
