package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.MonthlyBalance
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.EditMonthlyCumulativeDialog
import com.example.ui.components.EditStartingBalanceDialog
import com.example.ui.components.Formatters
import com.example.ui.components.PublicViewOnlyNoticeDialog
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.GreenIncomeBg
import com.example.ui.theme.RedExpense
import com.example.ui.theme.RedExpenseBg
import com.example.viewmodel.JimpitanViewModel

@Composable
fun MonthlyCumulativeScreen(
    viewModel: JimpitanViewModel
) {
    val context = LocalContext.current
    val globalReport by viewModel.globalReport.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    var editingMonthlyBalance by remember { mutableStateOf<MonthlyBalance?>(null) }
    var showStartingBalanceDialog by remember { mutableStateOf(false) }
    var showPublicNoticeDialog by remember { mutableStateOf(false) }
    var showAdminLoginDialog by remember { mutableStateOf(false) }

    val currentMonth = Formatters.getCurrentMonth()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("monthly_cumulative_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Rekapitulasi Saldo Bulanan Berantai",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = EmeraldPrimary
                        )
                        Text(
                            "Saldo akhir setiap bulan otomatis menjadi saldo awal bulan berikutnya dan dapat diedit/disesuaikan.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (isAdminLoggedIn) {
                                showStartingBalanceDialog = true
                            } else {
                                showPublicNoticeDialog = true
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text("Saldo Awal", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Formula / Chain Rule Info Banner
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                "Aturan Akuntansi Jimpitan RT 06",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                "Saldo Awal + Total Setoran - Total Pengeluaran = Saldo Akhir. Klik tombol 'Edit Rekap' pada tiap bulan untuk mengoreksi kas.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Annual Summary Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimaryDark),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "TOTAL KAS TAHUNAN $selectedYear",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    Formatters.formatRupiah(globalReport.currentBalance),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { viewModel.shareYearlyReport(context, globalReport) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Bagikan WA", fontSize = 12.sp)
                                }
                            }
                        }

                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = Color.White.copy(alpha = 0.25f))
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Saldo Awal 1 Jan", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                                Text(
                                    Formatters.formatRupiah(globalReport.startingBalanceYear),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFDE68A)
                                )
                            }
                            Column {
                                Text("Total Pemasukan", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                                Text(
                                    Formatters.formatRupiah(globalReport.totalIncomeYear),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF86EFAC)
                                )
                            }
                            Column {
                                Text("Total Pengeluaran", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                                Text(
                                    Formatters.formatRupiah(globalReport.totalExpenseYear),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFCA5A5)
                                )
                            }
                        }
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Rincian 12 Bulan Terakumulasi ($selectedYear):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Bisa Diedit",
                        fontSize = 11.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 12 Monthly Cards
            items(globalReport.monthlyBalances) { mb ->
                val isCurrentMonth = mb.month == currentMonth

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        if (isCurrentMonth) 2.dp else 1.dp,
                        if (isCurrentMonth) EmeraldPrimary else MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Month Header Row
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
                                        .size(34.dp)
                                        .background(
                                            if (isCurrentMonth) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${mb.month}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isCurrentMonth) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Column {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            mb.monthName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (isCurrentMonth) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = EmeraldPrimary
                                            ) {
                                                Text(
                                                    "BULAN INI",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        "${mb.jimpitanCount} setoran • ${mb.expenseCount} pos keluar",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            editingMonthlyBalance = mb
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp), tint = EmeraldPrimary)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit Rekap", fontSize = 11.sp, color = EmeraldPrimary, fontWeight = FontWeight.Bold)
                                }

                                IconButton(
                                    onClick = { viewModel.shareMonthlyReport(context, mb) },
                                    modifier = Modifier.size(34.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "Share WA",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(10.dp))

                        // Step breakdown: Saldo Awal -> (+) Masuk -> (-) Keluar -> Saldo Akhir
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .clickable {
                                    if (isAdminLoggedIn) {
                                        editingMonthlyBalance = mb
                                    } else {
                                        showPublicNoticeDialog = true
                                    }
                                }
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("1. Saldo Awal Bulan (dari akhir bulan lalu)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(Formatters.formatRupiah(mb.startingBalance), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = GreenIncome, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("2. (+) Pemasukan Jimpitan", fontSize = 11.sp, color = GreenIncome)
                                }
                                Text("+ ${Formatters.formatRupiah(mb.totalIncome)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GreenIncome)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = RedExpense, modifier = Modifier.size(12.dp))
                                    Spacer(Modifier.width(2.dp))
                                    Text("3. (-) Pengeluaran Kas RT", fontSize = 11.sp, color = RedExpense)
                                }
                                Text("- ${Formatters.formatRupiah(mb.totalExpense)}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp), color = MaterialTheme.colorScheme.outline)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("4. (=) SALDO AKHIR BULAN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                                    if (mb.month < 12) {
                                        Text("➔ Masuk ke Saldo Awal ${Formatters.getMonthName(mb.month + 1)}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    } else {
                                        Text("➔ Saldo Tutup Buku Tahun $selectedYear", fontSize = 10.sp, color = AmberSecondary, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Text(
                                    Formatters.formatRupiah(mb.endingBalance),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(Modifier.height(20.dp))
            }
        }

        // Edit Monthly Cumulative Dialog
        editingMonthlyBalance?.let { mb ->
            EditMonthlyCumulativeDialog(
                monthlyBalance = mb,
                selectedYear = selectedYear,
                onDismiss = { editingMonthlyBalance = null },
                onSaveAdjustment = { inc, exp, notes ->
                    viewModel.recordMonthlyAdjustment(selectedYear, mb.month, inc, exp, notes)
                    editingMonthlyBalance = null
                },
                onEditStartingBalance = {
                    showStartingBalanceDialog = true
                }
            )
        }

        // Edit Starting Balance Dialog (1 Jan)
        if (showStartingBalanceDialog) {
            EditStartingBalanceDialog(
                currentYear = selectedYear,
                currentBalance = globalReport.startingBalanceYear,
                onDismiss = { showStartingBalanceDialog = false },
                onSave = { newBal ->
                    viewModel.updateStartingBalance(selectedYear, newBal)
                    showStartingBalanceDialog = false
                }
            )
        }

        // Public notice dialog
        if (showPublicNoticeDialog) {
            PublicViewOnlyNoticeDialog(
                onDismiss = { showPublicNoticeDialog = false },
                onLoginClick = {
                    showPublicNoticeDialog = false
                    showAdminLoginDialog = true
                }
            )
        }

        // Admin login dialog
        if (showAdminLoginDialog) {
            AdminLoginDialog(
                onDismiss = { showAdminLoginDialog = false },
                onLoginSuccess = { showAdminLoginDialog = false },
                viewModel = viewModel
            )
        }
    }
}
