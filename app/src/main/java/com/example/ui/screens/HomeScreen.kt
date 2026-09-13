package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.ExpenseEntry
import com.example.model.JimpitanEntry
import com.example.model.Resident
import com.example.model.dawisName
import com.example.ui.components.AddEditResidentDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.EditStartingBalanceDialog
import com.example.ui.components.Formatters
import com.example.ui.components.ManageVacantHousesDialog
import com.example.ui.components.MarkHouseAsVacantDialog
import com.example.ui.components.PublicViewOnlyNoticeDialog
import com.example.ui.components.RestoreHouseDialog
import com.example.ui.components.ShareAppDialog
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
import com.example.ui.theme.RedExpense
import com.example.ui.theme.RedExpenseBg
import com.example.viewmodel.JimpitanViewModel

@Composable
fun HomeScreen(
    viewModel: JimpitanViewModel,
    onNavigateToChecklist: () -> Unit,
    onNavigateToResidents: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    onNavigateToReports: () -> Unit,
    onOpenAddExpense: () -> Unit,
    onOpenAddResident: () -> Unit
) {
    val context = LocalContext.current
    val globalReport by viewModel.globalReport.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val selectedDateEntries by viewModel.selectedDateEntries.collectAsStateWithLifecycle()
    val allResidents by viewModel.allResidents.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val rondaStats by viewModel.rondaOverallStats.collectAsStateWithLifecycle()
    val vacantResidents by viewModel.vacantResidents.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var showPublicNoticeDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showEditStartingBalance by remember { mutableStateOf(false) }
    var showManageVacantDialog by remember { mutableStateOf(false) }
    var showMarkVacantDialog by remember { mutableStateOf(false) }
    var selectedHouseForRestore by remember { mutableStateOf<Resident?>(null) }
    var selectedHouseForEdit by remember { mutableStateOf<Resident?>(null) }

    val currentMonth = Formatters.getCurrentMonth()
    val currentMonthBalance = globalReport.monthlyBalances.find { it.month == currentMonth }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 0. Status Hak Akses (Mode Publik / Warga vs Mode Admin & Petugas)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_status_banner"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAdminLoggedIn) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isAdminLoggedIn) EmeraldPrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (isAdminLoggedIn) Icons.Default.LockOpen else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = if (isAdminLoggedIn) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    if (isAdminLoggedIn) "Mode Pengurus RT / Petugas" else "Mode Warga (Lihat Saja)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isAdminLoggedIn) EmeraldPrimary else MaterialTheme.colorScheme.outlineVariant
                                ) {
                                    Text(
                                        if (isAdminLoggedIn) "BISA EDIT" else "READ-ONLY",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isAdminLoggedIn) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                if (isAdminLoggedIn) "Akses penuh aktif (edit data & rekap kas)" else "Bebas dibuka siapa saja secara transparan",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isAdminLoggedIn) {
                        OutlinedButton(
                            onClick = { viewModel.logoutAdmin() },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(13.dp), tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(4.dp))
                            Text("Keluar", fontSize = 11.sp, color = MaterialTheme.colorScheme.error)
                        }
                    } else {
                        Button(
                            onClick = { showAdminLoginDialog = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("Login Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 0b. Bagikan Tautan Warga (Preview Bersih Tanpa Chat AI)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showShareDialog = true },
                colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.25f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EmeraldPrimary,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                "Bagikan Tautan ke Warga RT",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = EmeraldPrimaryDark
                            )
                            Text(
                                "Tampilan preview bersih: chat AI & panel luar tersembunyi",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { showShareDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Salin / Kirim", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary)
                    }
                }
            }
        }

        // 1. Community Header Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Paid,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                "KAS JIMPITAN RT 06 RW 06",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    "Perumahan KCVRI • Berkoh, Pwk Selatan",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text(
                            text = "$selectedYear",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // 2. Grand Saldo Kas RT Card (Hero Financial Card)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("saldo_utama_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "SALDO KAS TERKINI (GLOBAL)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 1.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = Formatters.formatRupiah(globalReport.currentBalance),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        IconButton(
                            onClick = {
                                if (isAdminLoggedIn) {
                                    showEditStartingBalance = true
                                } else {
                                    showPublicNoticeDialog = true
                                }
                            },
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.6f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Saldo Awal",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Total Income
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.55f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ArrowDownward,
                                        contentDescription = null,
                                        tint = GreenIncome,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        "Pemasukan",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    Formatters.formatRupiah(globalReport.totalIncomeYear),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenIncome
                                )
                            }
                        }

                        // Total Expense
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.55f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.ArrowUpward,
                                        contentDescription = null,
                                        tint = RedExpense,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        "Pengeluaran",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    Formatters.formatRupiah(globalReport.totalExpenseYear),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RedExpense
                                )
                            }
                        }

                        // Starting Balance
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.55f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "Saldo Awal 1 Jan",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    Formatters.formatRupiah(globalReport.startingBalanceYear),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Action Grid
        item {
            Text(
                "Aksi Cepat RT 06",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick Ronda Collection
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.setActiveChecklistTab(0)
                            onNavigateToChecklist()
                        }
                        .testTag("btn_quick_ronda"),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(EmeraldPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Tarik Jimpitan", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Checklist Harian", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Catat Pengeluaran
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (isAdminLoggedIn) {
                                onOpenAddExpense()
                            } else {
                                showPublicNoticeDialog = true
                            }
                        }
                        .testTag("btn_quick_expense"),
                    colors = CardDefaults.cardColors(containerColor = RedExpenseBg),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(RedExpense, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Pengeluaran RT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedExpense)
                        Text("Catat Pos Kas", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Tambah Warga
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            if (isAdminLoggedIn) {
                                onOpenAddResident()
                            } else {
                                showPublicNoticeDialog = true
                            }
                        }
                        .testTag("btn_quick_resident"),
                    colors = CardDefaults.cardColors(containerColor = BlueInfoBg),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(BlueInfo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = Color.White)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Buku Warga", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BlueInfo)
                        Text("${allResidents.size} Warga (4 Dawis)", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // 3.6 Kartu Daftar Hadir & Denda Ronda RT 06
        item {
            Card(
                onClick = {
                    viewModel.setActiveChecklistTab(1)
                    onNavigateToChecklist()
                },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (rondaStats.totalAccumulatedUnpaidFines > 0) RedExpenseBg else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (rondaStats.totalAccumulatedUnpaidFines > 0) RedExpense.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_ronda_summary_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (rondaStats.totalAccumulatedUnpaidFines > 0) RedExpense else EmeraldPrimary,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Security,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    "Daftar Hadir & Denda Ronda RT 06",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    "Denda Rp 20.000 / minggu jika tidak hadir (terakumulasi)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Presensi Hari Ini
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = GreenIncomeBg
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Petugas Hadir Hari Ini", fontSize = 10.sp, color = GreenIncome)
                                Text(
                                    "${rondaStats.presentTodayCount} Warga",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenIncome
                                )
                            }
                        }

                        // Total Denda Terakumulasi
                        Surface(
                            modifier = Modifier.weight(1.3f),
                            shape = RoundedCornerShape(10.dp),
                            color = if (rondaStats.totalAccumulatedUnpaidFines > 0) RedExpense.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    if (rondaStats.totalAccumulatedUnpaidFines > 0) "⚠️ Denda Terakumulasi" else "Denda Tertunggak",
                                    fontSize = 10.sp,
                                    color = if (rondaStats.totalAccumulatedUnpaidFines > 0) RedExpense else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    Formatters.formatRupiah(rondaStats.totalAccumulatedUnpaidFines),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rondaStats.totalAccumulatedUnpaidFines > 0) RedExpense else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3.5 Panel Rumah Kosong RT 06 (Pemantauan Keamanan & Ronda)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_vacant_houses_panel"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFEF3C7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    tint = Color(0xFF92400E),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        "Panel Rumah Kosong",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Text(
                                            "RT 06",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF92400E),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    "Bebas jimpitan • Sasaran patroli ronda malam",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Badge Count
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF92400E),
                            modifier = Modifier.clickable { showManageVacantDialog = true }
                        ) {
                            Text(
                                "${vacantResidents.size} Rumah",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (vacantResidents.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(24.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Semua rumah berpenghuni", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                                    Text("Tidak ada rumah yang ditinggal atau kosong di RT 06.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                OutlinedButton(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            showMarkVacantDialog = true
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("+ Tandai", fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        // Preview cards of vacant houses
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            vacantResidents.take(3).forEach { house ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            if (isAdminLoggedIn) {
                                                selectedHouseForEdit = house
                                            } else {
                                                showPublicNoticeDialog = true
                                            }
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0xFF92400E)
                                            ) {
                                                Text(
                                                    house.houseNumber,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                                )
                                            }
                                            Column {
                                                Text(
                                                    house.residentName,
                                                    fontWeight = FontWeight.SemiBold,
                                                    fontSize = 12.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    if (house.notes.isNotBlank()) house.notes else "${house.dawisName} • Rumah Kosong",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    maxLines = 1,
                                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                                )
                                            }
                                        }

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    val cleanPhone = house.phone.replace(Regex("[^0-9]"), "")
                                                    if (cleanPhone.isNotBlank()) {
                                                        val waNumber = if (cleanPhone.startsWith("0")) "62" + cleanPhone.substring(1) else cleanPhone
                                                        val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waNumber?text=Halo%20${house.residentName},%20pantauan%20ronda%20RT%2006%20KCVRI%20Berkoh."))
                                                        waIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                        try {
                                                            context.startActivity(waIntent)
                                                        } catch (e: Exception) {
                                                            val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${house.phone}"))
                                                            context.startActivity(dial)
                                                        }
                                                    } else {
                                                        if (isAdminLoggedIn) {
                                                            selectedHouseForEdit = house
                                                        } else {
                                                            showPublicNoticeDialog = true
                                                        }
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.Phone,
                                                    contentDescription = "Hubungi Pemilik",
                                                    tint = EmeraldPrimary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    if (isAdminLoggedIn) {
                                                        selectedHouseForRestore = house
                                                    } else {
                                                        showPublicNoticeDialog = true
                                                    }
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = "Ada Penghuni",
                                                    tint = GreenIncome,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (vacantResidents.size > 3) {
                                Text(
                                    "+ ${vacantResidents.size - 3} rumah kosong lainnya terdata",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier
                                        .clickable { showManageVacantDialog = true }
                                        .padding(horizontal = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(Modifier.height(10.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showManageVacantDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF92400E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .testTag("btn_manage_vacant_houses"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Home, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Kelola (${vacantResidents.size})", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                if (isAdminLoggedIn) {
                                    showMarkVacantDialog = true
                                } else {
                                    showPublicNoticeDialog = true
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("btn_add_vacant_house"),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("+ Kosong", fontSize = 12.sp)
                        }

                        FilledTonalButton(
                            onClick = { viewModel.shareVacantHousesReport(context) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_share_vacant_houses"),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color(0xFF92400E))
                            Spacer(Modifier.width(4.dp))
                            Text("WA", fontSize = 12.sp, color = Color(0xFF92400E), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // 4. Saldo Bulan Ini (Akumulasi Berantai)
        if (currentMonthBalance != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = EmeraldPrimary
                                )
                                Text(
                                    "Rekap Bulan Ini: ${currentMonthBalance.monthName} $selectedYear",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            IconButton(
                                onClick = { viewModel.shareMonthlyReport(context, currentMonthBalance) }
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share WA",
                                    tint = EmeraldPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Stepper chain visualizer
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Saldo Awal Bulan (Carryover)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(Formatters.formatRupiah(currentMonthBalance.startingBalance), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("(+) Pemasukan Jimpitan", fontSize = 12.sp, color = GreenIncome)
                                    Text("+ ${Formatters.formatRupiah(currentMonthBalance.totalIncome)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenIncome)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("(-) Pengeluaran Kas RT", fontSize = 12.sp, color = RedExpense)
                                    Text("- ${Formatters.formatRupiah(currentMonthBalance.totalExpense)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("(=) Saldo Akhir Bulan", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        Formatters.formatRupiah(currentMonthBalance.endingBalance),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EmeraldPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Jimpitan Hari Ini Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Jimpitan Hari Ini", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                Formatters.formatDayAndDate(Formatters.getCurrentDateString()),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        val todayCollected = selectedDateEntries.sumOf { it.amount }
                        Text(
                            Formatters.formatRupiah(todayCollected),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = GreenIncome
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    val activeCount = allResidents.count { it.isActive }
                    val collectedCount = selectedDateEntries.size
                    val progress = if (activeCount > 0) collectedCount.toFloat() / activeCount else 0f

                    androidx.compose.material3.LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "$collectedCount dari $activeCount Rumah telah setor (${(progress * 100).toInt()}%)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(
                            onClick = onNavigateToChecklist,
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("Buka Checklist", fontSize = 12.sp, color = EmeraldPrimary)
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // 6. Recent RT Expenses
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pengeluaran Kas Terbaru",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                TextButton(onClick = onNavigateToExpenses) {
                    Text("Lihat Semua", fontSize = 12.sp, color = EmeraldPrimary)
                }
            }
        }

        if (allExpenses.isEmpty()) {
            item {
                Text(
                    "Belum ada catatan pengeluaran kas RT.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(allExpenses.take(4)) { exp ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
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
                                    .size(36.dp)
                                    .background(RedExpenseBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ReceiptLong,
                                    contentDescription = null,
                                    tint = RedExpense,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    exp.description,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    maxLines = 1
                                )
                                Text(
                                    "${Formatters.formatIndonesianDateShort(exp.date)} • ${exp.category}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            "- ${Formatters.formatRupiah(exp.amount)}",
                            fontWeight = FontWeight.Bold,
                            color = RedExpense,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }

    if (showEditStartingBalance) {
        EditStartingBalanceDialog(
            currentYear = selectedYear,
            currentBalance = globalReport.startingBalanceYear,
            onDismiss = { showEditStartingBalance = false },
            onSave = { newBal ->
                viewModel.updateStartingBalance(selectedYear, newBal)
                showEditStartingBalance = false
            }
        )
    }

    if (showManageVacantDialog) {
        ManageVacantHousesDialog(
            vacantHouses = vacantResidents,
            allResidents = allResidents,
            onDismiss = { showManageVacantDialog = false },
            onOpenMarkVacant = {
                showManageVacantDialog = false
                if (isAdminLoggedIn) {
                    showMarkVacantDialog = true
                } else {
                    showPublicNoticeDialog = true
                }
            },
            onRestoreHouse = { house ->
                if (isAdminLoggedIn) {
                    selectedHouseForRestore = house
                } else {
                    showPublicNoticeDialog = true
                }
            },
            onEditVacantDetails = { house ->
                if (isAdminLoggedIn) {
                    selectedHouseForEdit = house
                } else {
                    showPublicNoticeDialog = true
                }
            },
            onShareReport = { viewModel.shareVacantHousesReport(context) }
        )
    }

    if (showMarkVacantDialog) {
        MarkHouseAsVacantDialog(
            allResidents = allResidents,
            onDismiss = { showMarkVacantDialog = false },
            onSave = { residentId, reason, contact, notes ->
                viewModel.markHouseVacantById(residentId, reason, contact, notes)
                showMarkVacantDialog = false
            }
        )
    }

    if (selectedHouseForRestore != null) {
        val houseToRestore = selectedHouseForRestore!!
        RestoreHouseDialog(
            resident = houseToRestore,
            onDismiss = { selectedHouseForRestore = null },
            onConfirm = { newStatus, newName, newPhone ->
                viewModel.restoreHouseFromVacant(
                    residentId = houseToRestore.id,
                    newStatus = newStatus,
                    residentName = newName,
                    phone = newPhone
                )
                selectedHouseForRestore = null
            }
        )
    }

    if (selectedHouseForEdit != null) {
        val houseToEdit = selectedHouseForEdit!!
        AddEditResidentDialog(
            resident = houseToEdit,
            onDismiss = { selectedHouseForEdit = null },
            onSave = { houseNo, name, phone, status, defAmt, notes ->
                viewModel.updateResident(
                    houseToEdit.copy(
                        houseNumber = houseNo,
                        residentName = name,
                        phone = phone,
                        status = status,
                        defaultAmount = defAmt,
                        notes = notes
                    )
                )
                selectedHouseForEdit = null
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

    if (showShareDialog) {
        ShareAppDialog(
            onDismiss = { showShareDialog = false }
        )
    }
}
