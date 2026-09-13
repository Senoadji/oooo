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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Resident
import com.example.model.ResidentFinancialSummary
import com.example.model.dawisName
import com.example.ui.components.AddEditResidentDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.EditMonthlyDepositDialog
import com.example.ui.components.Formatters
import com.example.ui.components.PublicViewOnlyNoticeDialog
import com.example.ui.components.RecordJimpitanDialog
import com.example.ui.components.ResidentDetailBottomSheet
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.GreenIncomeBg
import com.example.viewmodel.JimpitanViewModel

@Composable
fun ResidentLedgerScreen(
    viewModel: JimpitanViewModel,
    onOpenAddResident: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val residentSummaries by viewModel.residentSummaries.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var showPublicNoticeDialog by remember { mutableStateOf(false) }
    var selectedBlockFilter by remember { mutableStateOf("Semua") }
    var selectedSummaryForDetail by remember { mutableStateOf<ResidentFinancialSummary?>(null) }
    var residentForPayment by remember { mutableStateOf<Resident?>(null) }
    var residentForEdit by remember { mutableStateOf<Resident?>(null) }
    var editingMonthlyDepositForResident by remember { mutableStateOf<Triple<Resident, Int, Double>?>(null) }

    val filterBlocks = listOf("Semua", "Dawis 1", "Dawis 2", "Dawis 3", "Dawis 4", "Tetap", "Kontrak")

    val filteredSummaries = residentSummaries.filter { summary ->
        val query = searchQuery.trim().lowercase()
        val matchesQuery = query.isEmpty() ||
                summary.resident.houseNumber.lowercase().contains(query) ||
                summary.resident.residentName.lowercase().contains(query)

        val matchesFilter = when (selectedBlockFilter) {
            "Semua" -> true
            "Dawis 1" -> summary.resident.dawisName == "Dawis 1"
            "Dawis 2" -> summary.resident.dawisName == "Dawis 2"
            "Dawis 3" -> summary.resident.dawisName == "Dawis 3"
            "Dawis 4" -> summary.resident.dawisName == "Dawis 4"
            "Tetap" -> summary.resident.status == "Tetap"
            "Kontrak" -> summary.resident.status == "Kontrak"
            else -> true
        }
        matchesQuery && matchesFilter
    }

    val totalCollectedAllResidents = filteredSummaries.sumOf { it.totalPaidYear }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("resident_ledger_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Admin Status Banner
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isAdminLoggedIn) EmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isAdminLoggedIn) EmeraldPrimary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
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
                                if (isAdminLoggedIn) "Mode Admin: Input & edit warga aktif" else "Mode Warga (Lihat Saja)",
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
            }

            // Header Search & Title
            item {
                Text(
                    "Buku Kas Jimpitan per Warga / Rumah",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = EmeraldPrimary
                )
                Text(
                    "Lihat saldo pemasukan & riwayat setor masing-masing nama penghuni atau nomor rumah.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Cari nomor rumah atau nama warga...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("search_warga_input")
                )
            }

            // Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterBlocks) { block ->
                        val isSelected = selectedBlockFilter == block
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedBlockFilter = block }
                        ) {
                            Text(
                                text = block,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // Summary Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Terdaftar", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${filteredSummaries.size} Rumah / KK", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Saldo Terkumpul $selectedYear", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                Formatters.formatRupiah(totalCollectedAllResidents),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenIncome
                            )
                        }
                    }
                }
            }

            // List of Residents Cards
            if (filteredSummaries.isEmpty()) {
                item {
                    Text(
                        "Tidak ada data warga yang cocok dengan pencarian.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(filteredSummaries) { summary ->
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedSummaryForDetail = summary }
                            .testTag("resident_card_${summary.resident.houseNumber}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                            .size(42.dp)
                                            .background(EmeraldPrimary, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            summary.resident.houseNumber,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color.White
                                        )
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                summary.resident.residentName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                        Text(
                                            "${summary.resident.dawisName} • ${summary.resident.status} • Tarif ${Formatters.formatRupiahCompact(summary.resident.defaultAmount)}/hari",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        "Saldo Terkumpul",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        Formatters.formatRupiah(summary.totalPaidYear),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = GreenIncome
                                    )
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    if (summary.lastPaymentDate != null)
                                        "Setoran terakhir: ${Formatters.formatIndonesianDateShort(summary.lastPaymentDate)} (${summary.paidDaysCount}x)"
                                    else "Belum ada catatan setoran",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TextButton(
                                        onClick = {
                                            if (isAdminLoggedIn) {
                                                residentForPayment = summary.resident
                                            } else {
                                                showPublicNoticeDialog = true
                                            }
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Setor", fontSize = 12.sp, color = EmeraldPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(Modifier.height(60.dp))
            }
        }

        FloatingActionButton(
            onClick = {
                if (isAdminLoggedIn) {
                    onOpenAddResident()
                } else {
                    showPublicNoticeDialog = true
                }
            },
            containerColor = EmeraldPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_resident")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Tambah Warga")
        }
    }

    // Detail BottomSheet
    val currentDetailSummary = selectedSummaryForDetail?.let { current ->
        residentSummaries.find { it.resident.id == current.resident.id } ?: current
    }
    currentDetailSummary?.let { sum ->
        ResidentDetailBottomSheet(
            summary = sum,
            selectedYear = selectedYear,
            onDismiss = { selectedSummaryForDetail = null },
            onRecordJimpitan = {
                val r = sum.resident
                selectedSummaryForDetail = null
                if (isAdminLoggedIn) {
                    residentForPayment = r
                } else {
                    showPublicNoticeDialog = true
                }
            },
            onEditResident = {
                val r = sum.resident
                selectedSummaryForDetail = null
                if (isAdminLoggedIn) {
                    residentForEdit = r
                } else {
                    showPublicNoticeDialog = true
                }
            },
            onDeleteEntry = { entryId ->
                if (isAdminLoggedIn) {
                    viewModel.deleteJimpitanEntry(entryId)
                } else {
                    showPublicNoticeDialog = true
                }
            },
            onEditMonthlyDeposit = { month, currentAmount ->
                if (isAdminLoggedIn) {
                    editingMonthlyDepositForResident = Triple(sum.resident, month, currentAmount)
                } else {
                    showPublicNoticeDialog = true
                }
            }
        )
    }

    // Edit Monthly Deposit Dialog (Rekapitulasi Setoran per Bulan Warga)
    editingMonthlyDepositForResident?.let { (resident, month, currentAmount) ->
        EditMonthlyDepositDialog(
            resident = resident,
            year = selectedYear,
            month = month,
            currentAmount = currentAmount,
            onDismiss = { editingMonthlyDepositForResident = null },
            onSave = { amount, date, collector, notes ->
                viewModel.updateResidentMonthlyDeposit(
                    resident = resident,
                    year = selectedYear,
                    month = month,
                    amount = amount,
                    date = date,
                    collector = collector,
                    notes = notes
                )
                editingMonthlyDepositForResident = null
            }
        )
    }

    // Quick Payment Dialog
    residentForPayment?.let { res ->
        RecordJimpitanDialog(
            resident = res,
            onDismiss = { residentForPayment = null },
            onSave = { amount, date, collector, notes ->
                viewModel.recordCustomJimpitan(res, date, amount, collector, notes)
                residentForPayment = null
            }
        )
    }

    // Edit Resident Dialog
    residentForEdit?.let { res ->
        AddEditResidentDialog(
            resident = res,
            onDismiss = { residentForEdit = null },
            onSave = { houseNo, name, phone, status, defAmt, notes ->
                viewModel.updateResident(
                    res.copy(
                        houseNumber = houseNo,
                        residentName = name,
                        phone = phone,
                        status = status,
                        defaultAmount = defAmt,
                        notes = notes
                    )
                )
                residentForEdit = null
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
}
