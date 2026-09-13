package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.DailyBalance
import com.example.model.Resident
import com.example.model.ResidentRondaAttendanceSummary
import com.example.model.RondaAttendance
import com.example.model.ScheduledRondaOfficer
import com.example.model.dawisName
import com.example.ui.components.AddOfficerToRondaDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.ExcuseOfficerDialog
import com.example.ui.components.Formatters
import com.example.ui.components.ManageWeeklyRondaScheduleDialog
import com.example.ui.components.PayRondaFineDialog
import com.example.ui.components.PublicViewOnlyNoticeDialog
import com.example.ui.components.RecordJimpitanDialog
import com.example.ui.components.RondaTimeRestrictedDialog
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Logout
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.BlueInfoBg
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.GreenIncomeBg
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.ui.theme.RedExpense
import com.example.ui.theme.RedExpenseBg
import com.example.viewmodel.JimpitanViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChecklistScreen(
    viewModel: JimpitanViewModel
) {
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val allResidents by viewModel.allResidents.collectAsStateWithLifecycle()
    val selectedDateEntries by viewModel.selectedDateEntries.collectAsStateWithLifecycle()
    val selectedDateExpenses by viewModel.selectedDateExpenses.collectAsStateWithLifecycle()
    val dailyBalances by viewModel.dailyBalances.collectAsStateWithLifecycle()
    val rondaSummaries by viewModel.rondaSummaries.collectAsStateWithLifecycle()
    val scheduledOfficers by viewModel.scheduledOfficers.collectAsStateWithLifecycle()
    val rondaStats by viewModel.rondaOverallStats.collectAsStateWithLifecycle()
    val selectedTab by viewModel.activeChecklistTab.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var showPublicNoticeDialog by remember { mutableStateOf(false) }
    var rondaSubView by remember { mutableIntStateOf(0) } // 0: Regu Ronda Hari Ini (7+ Petugas), 1: Rekap Denda Tertunggak RT
    var residentForCustomJimpitan by remember { mutableStateOf<Resident?>(null) }
    var residentForFineDialog by remember { mutableStateOf<ResidentRondaAttendanceSummary?>(null) }
    var showAddOfficerDialog by remember { mutableStateOf(false) }
    var officerForExcuseDialog by remember { mutableStateOf<ScheduledRondaOfficer?>(null) }
    var showWeeklyScheduleDialog by remember { mutableStateOf(false) }
    var showRondaTimeLockedDialog by remember { mutableStateOf(false) }
    var adminRondaOverride by remember { mutableStateOf(false) }
    var selectedDawisFilter by remember { mutableStateOf("Semua") }
    var selectedRondaDawisFilter by remember { mutableStateOf("Semua") }
    var selectedRondaStatusFilter by remember { mutableStateOf("Semua") }

    val dawisFilterOptions = listOf("Semua", "Dawis 1", "Dawis 2", "Dawis 3", "Dawis 4")
    val activeResidents = allResidents.filter { it.isActive }
    val displayedResidents = activeResidents.filter { res ->
        when (selectedDawisFilter) {
            "Semua" -> true
            "Dawis 1" -> res.dawisName == "Dawis 1"
            "Dawis 2" -> res.dawisName == "Dawis 2"
            "Dawis 3" -> res.dawisName == "Dawis 3"
            "Dawis 4" -> res.dawisName == "Dawis 4"
            else -> true
        }
    }
    val paidResidentsMap = selectedDateEntries.associateBy { it.residentId }
    val totalIncomeToday = selectedDateEntries.sumOf { it.amount }
    val totalExpenseToday = selectedDateExpenses.sumOf { it.amount }
    val netToday = totalIncomeToday - totalExpenseToday

    fun changeDate(offsetDays: Int) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        try {
            val date = sdf.parse(selectedDate) ?: Date()
            val cal = Calendar.getInstance()
            cal.time = date
            cal.add(Calendar.DAY_OF_YEAR, offsetDays)
            viewModel.setSelectedDate(sdf.format(cal.time))
        } catch (e: Exception) {
            viewModel.setSelectedDate(Formatters.getCurrentDateString())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("daily_checklist_screen")
    ) {
        // Admin Access Indicator Banner
        Surface(
            color = if (isAdminLoggedIn) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        if (isAdminLoggedIn) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (isAdminLoggedIn) "Mode Admin: Akses input & edit aktif" else "Mode Warga (Lihat Saja)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!isAdminLoggedIn) {
                    TextButton(
                        onClick = { showAdminLoginDialog = true },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Login Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }
                } else {
                    TextButton(
                        onClick = { viewModel.logoutAdmin() },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Text("Keluar", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        // Sub-tabs
        PrimaryTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { viewModel.setActiveChecklistTab(0) },
                text = { Text("Jimpitan", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1) },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { viewModel.setActiveChecklistTab(1) },
                text = { Text("Presensi Ronda", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1) },
                icon = { Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { viewModel.setActiveChecklistTab(2) },
                text = { Text("Kas Harian", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1) },
                icon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp)) }
            )
        }

        if (selectedTab == 0) {
            // Tab 0: Checklist Penarikan Jimpitan
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Selector Header
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(onClick = { changeDate(-1) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Hari Sebelumnya")
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        Formatters.formatDayAndDate(selectedDate),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = EmeraldPrimary
                                    )
                                    Text(
                                        selectedDate,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(onClick = { changeDate(1) }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Hari Berikutnya")
                                }
                            }

                            if (selectedDate != Formatters.getCurrentDateString()) {
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    TextButton(
                                        onClick = { viewModel.setSelectedDate(Formatters.getCurrentDateString()) },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                    ) {
                                        Text("Kembali ke Hari Ini", fontSize = 12.sp, color = EmeraldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }

                // Daily Summary Stat Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = EmeraldPrimaryDark),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        "Terkumpul Tanggal Ini",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        Formatters.formatRupiah(totalIncomeToday),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            viewModel.markAllHousesPaidForDate(selectedDate, dawis = selectedDawisFilter)
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GreenIncome),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_mark_all_paid")
                                ) {
                                    Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (selectedDawisFilter == "Semua") "Tandai Semua Lunas" else "Lunas $selectedDawisFilter", fontSize = 12.sp)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            val progress = if (activeResidents.isNotEmpty()) paidResidentsMap.size.toFloat() / activeResidents.size else 0f

                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = Color(0xFF86EFAC),
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "${paidResidentsMap.size} dari ${activeResidents.size} Rumah Terisi (${(progress * 100).toInt()}%)",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                // Dawis Filter Chips
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(dawisFilterOptions) { dawis ->
                            val isSelected = selectedDawisFilter == dawis
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedDawisFilter = dawis }
                            ) {
                                Text(
                                    text = dawis,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Header for House List
                item {
                    Text(
                        if (selectedDawisFilter == "Semua") "Daftar Rumah Warga (${displayedResidents.size}):" else "Daftar Warga $selectedDawisFilter (${displayedResidents.size} rumah):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Houses Checklist Cards
                items(displayedResidents) { resident ->
                    val entry = paidResidentsMap[resident.id]
                    val isPaid = entry != null

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isPaid) GreenIncomeBg.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isPaid) GreenIncome.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.toggleDailyJimpitan(resident, selectedDate) }
                            .testTag("house_item_${resident.houseNumber}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            if (isPaid) GreenIncome else MaterialTheme.colorScheme.surfaceVariant,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPaid) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    } else {
                                        Text(
                                            resident.houseNumber,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }

                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            resident.houseNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = if (isPaid) GreenIncome else MaterialTheme.colorScheme.onSurface
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (isPaid) GreenIncome else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                text = if (isPaid) "LUNAS" else "BELUM",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPaid) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        "${resident.residentName} • ${resident.dawisName}",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isPaid) {
                                        Text(
                                            "Setor: ${Formatters.formatRupiah(entry.amount)} (${entry.collectorName})",
                                            fontSize = 11.sp,
                                            color = GreenIncome,
                                            fontWeight = FontWeight.Medium
                                        )
                                    } else {
                                        Text(
                                            "Tarif: ${Formatters.formatRupiahCompact(resident.defaultAmount)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            residentForCustomJimpitan = resident
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Setor Nominal Lain / Keterangan",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Button(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            viewModel.toggleDailyJimpitan(resident, selectedDate)
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isPaid) RedExpenseBg else EmeraldPrimary
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isPaid) "Batal" else "Setor",
                                        color = if (isPaid) RedExpense else Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else if (selectedTab == 1) {
            // Tab 1: Daftar Hadir Ronda 7 Orang & Otomatis Denda 20.000 Terakumulasi
            val dayOfWeekName = Formatters.getDayOfWeekName(selectedDate)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("ronda_attendance_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sub-Tab Switcher: [Regu Ronda Hari Ini (7+ Petugas)] vs [Rekap Denda Tertunggak RT]
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (rondaSubView == 0) EmeraldPrimary else Color.Transparent,
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clickable { rondaSubView = 0 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = if (rondaSubView == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Regu Hari Ini (${scheduledOfficers.size})",
                                        fontSize = 12.sp,
                                        fontWeight = if (rondaSubView == 0) FontWeight.Bold else FontWeight.Medium,
                                        color = if (rondaSubView == 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (rondaSubView == 1) RedExpense else Color.Transparent,
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clickable { rondaSubView = 1 }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (rondaSubView == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Rekap Denda RT (${rondaStats.totalUnpaidResidentsCount})",
                                        fontSize = 12.sp,
                                        fontWeight = if (rondaSubView == 1) FontWeight.Bold else FontWeight.Medium,
                                        color = if (rondaSubView == 1) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                if (rondaSubView == 0) {
                    // SUBVIEW 0: DAFTAR HADIR REGU RONDA HARI INI (7 ORANG + BISA DITAMBAH)

                    // 1. Header Jadwal & Navigasi Tanggal
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { changeDate(-1) }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kemarin")
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = Formatters.formatDayAndDate(selectedDate),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                color = EmeraldPrimary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(
                                                    "Regu $dayOfWeekName • ${scheduledOfficers.size} Petugas Terjadwal",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = EmeraldPrimaryDark,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            if (selectedDate == Formatters.getCurrentDateString()) {
                                                Surface(
                                                    color = GreenIncomeBg,
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Text(
                                                        "Hari Ini",
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GreenIncome,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    IconButton(onClick = { changeDate(1) }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Besok")
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                                // Quick Actions Bar (Mobile 2x2 Layout)
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Button + Tambah Petugas
                                        OutlinedButton(
                                            onClick = {
                                                if (isAdminLoggedIn) {
                                                    showAddOfficerDialog = true
                                                } else {
                                                    showPublicNoticeDialog = true
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("+ Petugas", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        // Button Centang Semua Hadir
                                        Button(
                                            onClick = {
                                                val status = Formatters.getRondaAccessStatus(selectedDate)
                                                if (!status.isAllowed && !adminRondaOverride) {
                                                    showRondaTimeLockedDialog = true
                                                } else {
                                                    if (isAdminLoggedIn || status.isAllowed) {
                                                        viewModel.markAllScheduledPresent()
                                                    } else {
                                                        showPublicNoticeDialog = true
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            modifier = Modifier.weight(1.2f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Centang Semua", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Button Atur Jadwal Tetap
                                        OutlinedButton(
                                            onClick = {
                                                if (isAdminLoggedIn) {
                                                    showWeeklyScheduleDialog = true
                                                } else {
                                                    showPublicNoticeDialog = true
                                                }
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1.2f),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(15.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Atur Jadwal 7 Regu", fontSize = 12.sp)
                                        }

                                        // Button Lapor WA
                                        Button(
                                            onClick = { viewModel.shareRondaReport(context, selectedDate) },
                                            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                            modifier = Modifier.weight(1f),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.Black)
                                            Spacer(Modifier.width(4.dp))
                                            Text("Lapor WA", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. Status Counter Cards
                    item {
                        val presentCount = scheduledOfficers.count { it.isChecked }
                        val absentCount = scheduledOfficers.count { !it.isChecked && !it.isExcused }
                        val excusedCount = scheduledOfficers.count { it.isExcused }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = GreenIncomeBg
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Centang Hadir", fontSize = 11.sp, color = GreenIncome, fontWeight = FontWeight.Bold)
                                    Text("$presentCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GreenIncome)
                                    Text("Bebas Denda", fontSize = 9.sp, color = GreenIncome.copy(alpha = 0.8f))
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = RedExpenseBg
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Belum Centang", fontSize = 11.sp, color = RedExpense, fontWeight = FontWeight.Bold)
                                    Text("$absentCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                                    Text("Denda 20rb/org", fontSize = 9.sp, color = RedExpense.copy(alpha = 0.8f))
                                }
                            }
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = AmberSecondary.copy(alpha = 0.2f)
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Izin / Sakit", fontSize = 11.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                                    Text("$excusedCount", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                    Text("Bebas Denda", fontSize = 9.sp, color = Color(0xFFB45309).copy(alpha = 0.8f))
                                }
                            }
                        }
                    }

                    // 3. Status Akses Waktu Presensi Ronda (00:00 - 01:00)
                    item {
                        val accessStatus = Formatters.getRondaAccessStatus(selectedDate)
                        val isAccessOpen = accessStatus.isAllowed || adminRondaOverride

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isAccessOpen) GreenIncomeBg else Color(0xFFFEF3C7),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAccessOpen) GreenIncome.copy(alpha = 0.5f) else Color(0xFFF59E0B).copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            if (isAccessOpen) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isAccessOpen) GreenIncome else Color(0xFFB45309),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            if (isAccessOpen) "AKSES PRESENSI: DIBUKA" else "AKSES PRESENSI: TERKUNCI",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isAccessOpen) GreenIncome else Color(0xFFB45309)
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isAccessOpen) GreenIncome else Color(0xFFB45309)
                                    ) {
                                        Text(
                                            "00:00 - 01:00 WIB",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = if (adminRondaOverride) {
                                        "👑 Mode Pengurus RT Aktif: Akses presensi dibuka manual (Override) untuk keperluan uji coba / koreksi data."
                                    } else {
                                        accessStatus.statusMessage
                                    },
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (isAdminLoggedIn) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextButton(
                                            onClick = { adminRondaOverride = !adminRondaOverride },
                                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                        ) {
                                            Text(
                                                if (adminRondaOverride) "Matikan Override Admin" else "Buka Akses Khusus Admin (Uji Coba)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (adminRondaOverride) Color(0xFFDC2626) else EmeraldPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Ketentuan Aturan Ronda RT 06
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldPrimary.copy(alpha = 0.08f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Security,
                                    contentDescription = null,
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Daftar hadir terdiri dari 7 petugas (bisa ditambah). Setiap petugas ronda centang nama pada kolom daftar hadir sesuai hari & tanggal. Yang tidak centang otomatis terkena denda Rp 20.000 terakumulasi mingguan bila belum dibayarkan.",
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // 4. Header Kolom Daftar Hadir Ronda
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "KOLOM DAFTAR HADIR PETUGAS RONDA (${scheduledOfficers.size} ORANG)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimaryDark
                            )
                            TextButton(
                                onClick = {
                                    if (isAdminLoggedIn) {
                                        viewModel.resetScheduleToDefault()
                                    } else {
                                        showPublicNoticeDialog = true
                                    }
                                },
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldPrimary)
                                Spacer(Modifier.width(2.dp))
                                Text("Reset ke 7 Default", fontSize = 11.sp, color = EmeraldPrimary)
                            }
                        }
                    }

                    // 5. Daftar Hadir Petugas (Row per officer)
                    if (scheduledOfficers.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("Belum ada petugas ronda terjadwal hari ini.", fontWeight = FontWeight.SemiBold)
                                    Text(
                                        "Klik tombol '+ Petugas' atau 'Atur Jadwal' untuk memuat 7 petugas ronda.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = {
                                            if (isAdminLoggedIn) {
                                                viewModel.resetScheduleToDefault()
                                            } else {
                                                showPublicNoticeDialog = true
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                                    ) {
                                        Text("Muat Jadwal 7 Petugas Default")
                                    }
                                }
                            }
                        }
                    } else {
                        items(scheduledOfficers, key = { it.attendance.id }) { officer ->
                            val resident = officer.resident
                            val isChecked = officer.isChecked
                            val isExcused = officer.isExcused

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("ronda_officer_${resident.houseNumber}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = when {
                                        isChecked -> GreenIncomeBg.copy(alpha = 0.45f)
                                        isExcused -> AmberSecondary.copy(alpha = 0.18f)
                                        else -> RedExpenseBg.copy(alpha = 0.45f)
                                    }
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    when {
                                        isChecked -> GreenIncome.copy(alpha = 0.5f)
                                        isExcused -> AmberSecondary.copy(alpha = 0.6f)
                                        else -> RedExpense.copy(alpha = 0.5f)
                                    }
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    // Baris 1: Identitas Petugas & Aksi Cepat (Izin & Hapus)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            // Nomor Urut Petugas (1 s/d 7+)
                                            Surface(
                                                shape = CircleShape,
                                                color = when {
                                                    isChecked -> GreenIncome
                                                    isExcused -> AmberSecondary
                                                    else -> RedExpense
                                                },
                                                modifier = Modifier.size(30.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        "${officer.orderNumber}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp,
                                                        color = if (isExcused) Color.Black else Color.White
                                                    )
                                                }
                                            }

                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = EmeraldPrimary
                                            ) {
                                                Text(
                                                    resident.houseNumber,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }

                                            Column {
                                                Text(
                                                    resident.residentName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    resident.dawisName,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        // Tombol Izin & Hapus dari Jadwal
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    if (isAdminLoggedIn) {
                                                        officerForExcuseDialog = officer
                                                    } else {
                                                        showPublicNoticeDialog = true
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                                modifier = Modifier.height(32.dp)
                                            ) {
                                                Text(
                                                    if (isExcused) "Ubah Izin" else "Izin",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFFB45309)
                                                )
                                            }
                                            IconButton(
                                                onClick = {
                                                    if (isAdminLoggedIn) {
                                                        viewModel.removeOfficerFromSchedule(resident.id)
                                                    } else {
                                                        showPublicNoticeDialog = true
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Hapus Petugas",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                    Spacer(Modifier.height(10.dp))

                                    // Baris 2: Tombol Centang Kehadiran & Status Denda
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // Interaktif Checklist Kehadiran (Khusus Jam 00:00 - 01:00 WIB)
                                        val accessStatus = Formatters.getRondaAccessStatus(selectedDate)
                                        val isAccessOpen = accessStatus.isAllowed || adminRondaOverride
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = when {
                                                isChecked -> GreenIncome.copy(alpha = 0.2f)
                                                !isAccessOpen -> Color(0xFFFEF3C7)
                                                else -> MaterialTheme.colorScheme.surface
                                            },
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                when {
                                                    isChecked -> GreenIncome
                                                    !isAccessOpen -> Color(0xFFF59E0B).copy(alpha = 0.7f)
                                                    else -> RedExpense.copy(alpha = 0.6f)
                                                }
                                            ),
                                            modifier = Modifier
                                                .clickable {
                                                    val status = Formatters.getRondaAccessStatus(selectedDate)
                                                    if (!status.isAllowed && !adminRondaOverride) {
                                                        showRondaTimeLockedDialog = true
                                                    } else {
                                                        if (isAdminLoggedIn || status.isAllowed) {
                                                            viewModel.toggleOfficerAttendance(officer, !isChecked)
                                                        } else {
                                                            showPublicNoticeDialog = true
                                                        }
                                                    }
                                                }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Icon(
                                                    when {
                                                        isChecked -> Icons.Default.CheckBox
                                                        !isAccessOpen -> Icons.Default.Lock
                                                        else -> Icons.Default.CheckBoxOutlineBlank
                                                    },
                                                    contentDescription = if (isChecked) "Sudah Centang" else "Belum Centang",
                                                    tint = when {
                                                        isChecked -> GreenIncome
                                                        !isAccessOpen -> Color(0xFFB45309)
                                                        else -> RedExpense
                                                    },
                                                    modifier = Modifier.size(20.dp)
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    when {
                                                        isChecked -> "✓ SUDAH CENTANG"
                                                        !isAccessOpen -> "TERKUNCI (00:00-01:00)"
                                                        else -> "CENTANG HADIR"
                                                    },
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when {
                                                        isChecked -> GreenIncome
                                                        !isAccessOpen -> Color(0xFFB45309)
                                                        else -> RedExpense
                                                    }
                                                )
                                            }
                                        }

                                        // Status Denda
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when {
                                                isChecked -> GreenIncomeBg
                                                isExcused -> AmberSecondary.copy(alpha = 0.25f)
                                                else -> RedExpenseBg
                                            }
                                        ) {
                                            Text(
                                                when {
                                                    isChecked -> "✓ Bebas Denda (Rp 0)"
                                                    isExcused -> "Bebas Denda (Izin)"
                                                    else -> "⚠️ Denda Rp 20.000"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when {
                                                    isChecked -> GreenIncome
                                                    isExcused -> Color(0xFF92400E)
                                                    else -> RedExpense
                                                },
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    if (isExcused && officer.attendance.notes.isNotBlank()) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            "Alasan: ${officer.attendance.notes}",
                                            fontSize = 10.sp,
                                            color = Color(0xFF92400E)
                                        )
                                    }

                                    // Banner Denda Tertunggak dari Minggu-Minggu Sebelumnya (Terakumulasi)
                                    if (officer.accumulatedPastFines > 0) {
                                        Spacer(Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = RedExpenseBg,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, RedExpense.copy(alpha = 0.3f)),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        "⚠️ Denda Tertunggak: ${Formatters.formatRupiah(officer.accumulatedPastFines)}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = RedExpense
                                                    )
                                                    Text(
                                                        "${officer.pastUnpaidCount}x absen ronda belum lunas (terakumulasi)",
                                                        fontSize = 10.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Button(
                                                    onClick = {
                                                        // Cari summary warga ini
                                                        val sum = rondaSummaries.find { it.resident.id == resident.id }
                                                        residentForFineDialog = sum
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
                                                    shape = RoundedCornerShape(6.dp),
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(28.dp)
                                                ) {
                                                    Text("Bayar Denda", fontSize = 10.sp, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 6. Highlight Total Denda RT & Navigasi ke Rekap
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = RedExpenseBg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RedExpense.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = RedExpense,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            "TOTAL DENDA RONDA TERTUNGGAK RT 06",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = RedExpense
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = RedExpense
                                    ) {
                                        Text(
                                            "${rondaStats.totalUnpaidResidentsCount} Warga",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = Formatters.formatRupiah(rondaStats.totalAccumulatedUnpaidFines),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RedExpense
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Total denda yang sudah terbayar: ${Formatters.formatRupiah(rondaStats.totalFinesCollectedAllTime)}.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    TextButton(
                                        onClick = { rondaSubView = 1 },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Buka Rekap Lengkap ->", fontSize = 11.sp, color = RedExpense, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                } else {
                    // SUBVIEW 1: REKAP LENGKAP DENDA RONDA SELURUH WARGA RT 06 (TERAKUMULASI TIAP MINGGU)

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    "Rekap Denda Ronda Terakumulasi Warga RT 06",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Setiap warga yang tidak hadir ronda dan tidak dicentang otomatis terkena denda Rp 20.000. Denda terakumulasi setiap minggu bila belum dibayarkan kepada bendahara/pengurus RT.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider()
                                Spacer(Modifier.height(10.dp))

                                // Filter Dawis
                                Text("Filter Dawis:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    items(dawisFilterOptions) { dawis ->
                                        val isSelected = selectedRondaDawisFilter == dawis
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                            modifier = Modifier.clickable { selectedRondaDawisFilter = dawis }
                                        ) {
                                            Text(
                                                text = dawis,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    val unpaidSummaries = rondaSummaries.filter { item ->
                        val res = item.resident
                        val matchesDawis = selectedRondaDawisFilter == "Semua" || res.dawisName == selectedRondaDawisFilter
                        matchesDawis && item.accumulatedUnpaidFine > 0 && res.isActive
                    }

                    if (unpaidSummaries.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = GreenIncomeBg.copy(alpha = 0.5f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenIncome, modifier = Modifier.size(32.dp))
                                    Text("Alhamdulillah, Nihil Tunggakan Denda Ronda!", fontWeight = FontWeight.Bold, color = GreenIncome)
                                    Text(
                                        "Semua warga ${if (selectedRondaDawisFilter == "Semua") "RT 06" else selectedRondaDawisFilter} tertib hadir atau denda ronda telah lunas terbayar.",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                "Daftar Warga dengan Denda Tertunggak (${unpaidSummaries.size} Warga):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedExpense
                            )
                        }

                        items(unpaidSummaries, key = { it.resident.id }) { item ->
                            val resident = item.resident
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("unpaid_ronda_${resident.houseNumber}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, RedExpense.copy(alpha = 0.4f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = EmeraldPrimary
                                            ) {
                                                Text(
                                                    resident.houseNumber,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    resident.residentName,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    resident.dawisName,
                                                    fontSize = 10.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }

                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                Formatters.formatRupiah(item.accumulatedUnpaidFine),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = RedExpense
                                            )
                                            Text(
                                                "${item.unpaidWeeksCount}x absen ronda",
                                                fontSize = 10.sp,
                                                color = RedExpense
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(8.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(Modifier.height(6.dp))

                                    // Tanggal-tanggal absen
                                    Text("Rincian Tanggal Belum Dicentang / Tidak Hadir:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Column(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        item.unpaidRecords.take(4).forEach { rec ->
                                            Text(
                                                "• ${Formatters.formatIndonesianDate(rec.date)} (Minggu ke-${rec.weekOfYear}) - Denda Rp 20.000",
                                                fontSize = 10.sp,
                                                color = RedExpense
                                            )
                                        }
                                        if (item.unpaidRecords.size > 4) {
                                            Text(
                                                "  ... dan ${item.unpaidRecords.size - 4} tanggal lainnya",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = {
                                                if (isAdminLoggedIn) {
                                                    residentForFineDialog = item
                                                } else {
                                                    showPublicNoticeDialog = true
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(14.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text("Proses Pembayaran Denda", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Tab 2: Saldo Masing-masing Tanggal (Daily Ledger Matrix)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                "Rincian Saldo Kas per Tanggal Transaksi",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                "Menampilkan total pemasukan jimpitan dan pengeluaran kas pada masing-masing tanggal.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (dailyBalances.isEmpty()) {
                    item {
                        Text(
                            "Belum ada transaksi pada bulan yang dipilih.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                } else {
                    items(dailyBalances) { day ->
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            Formatters.formatIndonesianDate(day.date),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            "${day.jimpitanEntries.size} Rumah Setor • ${day.expenseEntries.size} Pengeluaran",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (day.netChange >= 0) GreenIncomeBg else RedExpenseBg
                                    ) {
                                        Text(
                                            text = if (day.netChange >= 0) "+ ${Formatters.formatRupiah(day.netChange)}" else "- ${Formatters.formatRupiah(-day.netChange)}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (day.netChange >= 0) GreenIncome else RedExpense,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                Spacer(Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = GreenIncome, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Masuk: ${Formatters.formatRupiah(day.totalIncome)}",
                                            fontSize = 12.sp,
                                            color = GreenIncome,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = RedExpense, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            "Keluar: ${Formatters.formatRupiah(day.totalExpense)}",
                                            fontSize = 12.sp,
                                            color = RedExpense,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                // Quick switch date
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            viewModel.setSelectedDate(day.date)
                                            viewModel.setActiveChecklistTab(0)
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text("Buka Checklist Tanggal Ini", fontSize = 11.sp, color = EmeraldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom Jimpitan Dialog
    residentForCustomJimpitan?.let { res ->
        RecordJimpitanDialog(
            resident = res,
            initialDate = selectedDate,
            onDismiss = { residentForCustomJimpitan = null },
            onSave = { amount, date, collector, notes ->
                viewModel.recordCustomJimpitan(res, date, amount, collector, notes)
                residentForCustomJimpitan = null
            }
        )
    }

    // Dialog Pelunasan Denda Ronda Terakumulasi
    residentForFineDialog?.let { summary ->
        PayRondaFineDialog(
            summary = summary,
            onDismiss = { residentForFineDialog = null },
            onPaySingle = { attendanceId ->
                viewModel.paySingleFine(attendanceId, summary.resident.residentName)
                residentForFineDialog = null
            },
            onPayAll = {
                viewModel.payAllAccumulatedFines(summary.resident)
                residentForFineDialog = null
            }
        )
    }

    // Dialog Tambah Petugas Ronda Hari Ini (7+ Orang)
    if (showAddOfficerDialog) {
        val alreadyScheduledIds = scheduledOfficers.map { it.resident.id }.toSet()
        AddOfficerToRondaDialog(
            selectedDate = selectedDate,
            allResidents = allResidents,
            alreadyScheduledIds = alreadyScheduledIds,
            onDismiss = { showAddOfficerDialog = false },
            onAddResident = { resident ->
                viewModel.addOfficerToSchedule(resident)
                showAddOfficerDialog = false
            }
        )
    }

    // Dialog Izin / Sakit Petugas Ronda
    officerForExcuseDialog?.let { officer ->
        ExcuseOfficerDialog(
            officer = officer,
            selectedDate = selectedDate,
            onDismiss = { officerForExcuseDialog = null },
            onConfirmExcuse = { reason ->
                viewModel.setOfficerExcused(officer, reason)
                officerForExcuseDialog = null
            }
        )
    }

    // Dialog Kelola Jadwal Tetap 7 Petugas (Senin - Minggu)
    if (showWeeklyScheduleDialog) {
        val currentDay = Formatters.getDayOfWeekName(selectedDate)
        ManageWeeklyRondaScheduleDialog(
            allResidents = allResidents,
            initialDay = currentDay,
            onDismiss = { showWeeklyScheduleDialog = false },
            onLoadDaySchedule = { day, callback ->
                viewModel.loadWeeklyScheduleIds(day, callback)
            },
            onSaveDaySchedule = { day, ids ->
                viewModel.saveWeeklySchedule(day, ids)
            }
        )
    }

    if (showAdminLoginDialog) {
        AdminLoginDialog(
            onDismiss = { showAdminLoginDialog = false },
            onLoginSuccess = { showAdminLoginDialog = false },
            viewModel = viewModel
        )
    }

    if (showPublicNoticeDialog) {
        PublicViewOnlyNoticeDialog(
            onDismiss = { showPublicNoticeDialog = false },
            onLoginClick = { showAdminLoginDialog = true }
        )
    }

    if (showRondaTimeLockedDialog) {
        val status = Formatters.getRondaAccessStatus(selectedDate)
        RondaTimeRestrictedDialog(
            status = status,
            selectedDate = selectedDate,
            isAdminLoggedIn = isAdminLoggedIn,
            onDismiss = { showRondaTimeLockedDialog = false },
            onLoginClick = {
                showRondaTimeLockedDialog = false
                showAdminLoginDialog = true
            },
            onAdminOverrideToggle = {
                adminRondaOverride = true
                showRondaTimeLockedDialog = false
            }
        )
    }
}
