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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.model.ExpenseEntry
import com.example.ui.components.AddEditExpenseDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.Formatters
import com.example.ui.components.PublicViewOnlyNoticeDialog
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.RedExpense
import com.example.ui.theme.RedExpenseBg
import com.example.viewmodel.JimpitanViewModel

@Composable
fun ExpenseScreen(
    viewModel: JimpitanViewModel,
    onOpenAddExpense: () -> Unit
) {
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()

    var showAdminLoginDialog by remember { mutableStateOf(false) }
    var showPublicNoticeDialog by remember { mutableStateOf(false) }
    var selectedCategoryFilter by remember { mutableStateOf<String?>("Semua") }
    var expenseToEdit by remember { mutableStateOf<ExpenseEntry?>(null) }
    var expenseToDelete by remember { mutableStateOf<ExpenseEntry?>(null) }

    val categories = listOf(
        "Semua",
        "Ronda & Keamanan",
        "Kebersihan & Lingkungan",
        "Kegiatan RT",
        "Sosial & Kematian",
        "Konsumsi Pertemuan",
        "Sarana & Prasarana",
        "Lain-lain"
    )

    val filteredExpenses = allExpenses.filter { exp ->
        val inYear = exp.date.startsWith(selectedYear.toString())
        val inCat = selectedCategoryFilter == "Semua" || exp.category == selectedCategoryFilter
        inYear && inCat
    }

    val totalExpenseYear = filteredExpenses.sumOf { it.amount }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("expense_screen"),
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
                                if (isAdminLoggedIn) "Mode Admin: Input & edit pengeluaran aktif" else "Mode Warga (Lihat Saja)",
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

            // Header
            item {
                Text(
                    "Pengeluaran Kas Jimpitan RT 06",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = RedExpense
                )
                Text(
                    "Catatan pos pengeluaran dana jimpitan untuk ronda, kebersihan, sosial, dan sarpras.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = RedExpenseBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Total Pengeluaran $selectedYear",
                                fontSize = 12.sp,
                                color = RedExpense,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                Formatters.formatRupiah(totalExpenseYear),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = RedExpense
                            )
                        }
                        Text(
                            "${filteredExpenses.size} Transaksi",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RedExpense
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
                        val isSelected = selectedCategoryFilter == cat
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSelected) RedExpense else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedCategoryFilter = cat }
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // List of Expenses
            if (filteredExpenses.isEmpty()) {
                item {
                    Text(
                        "Belum ada data pengeluaran untuk kategori yang dipilih.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            } else {
                items(filteredExpenses) { exp ->
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
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(RedExpenseBg, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.ReceiptLong,
                                            contentDescription = null,
                                            tint = RedExpense,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            exp.description,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = exp.category,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Text(
                                                "• ${Formatters.formatIndonesianDate(exp.date)}",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Text(
                                    "- ${Formatters.formatRupiah(exp.amount)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = RedExpense
                                )
                            }

                            if (exp.receiptNotes.isNotBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "PJ / Nota: ${exp.receiptNotes}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            expenseToEdit = exp
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = EmeraldPrimary)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Edit", fontSize = 12.sp, color = EmeraldPrimary)
                                }
                                TextButton(
                                    onClick = {
                                        if (isAdminLoggedIn) {
                                            expenseToDelete = exp
                                        } else {
                                            showPublicNoticeDialog = true
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.width(4.dp))
                                    Text("Hapus", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
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
                    onOpenAddExpense()
                } else {
                    showPublicNoticeDialog = true
                }
            },
            containerColor = RedExpense,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_expense")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Catat Pengeluaran")
        }
    }

    // Edit Expense Dialog
    expenseToEdit?.let { exp ->
        AddEditExpenseDialog(
            expense = exp,
            onDismiss = { expenseToEdit = null },
            onSave = { date, cat, amt, desc, receipt ->
                viewModel.updateExpense(
                    exp.copy(
                        date = date,
                        category = cat,
                        amount = amt,
                        description = desc,
                        receiptNotes = receipt
                    )
                )
                expenseToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    expenseToDelete?.let { exp ->
        AlertDialog(
            onDismissRequest = { expenseToDelete = null },
            title = { Text("Hapus Catatan Pengeluaran?") },
            text = { Text("Yakin ingin menghapus pengeluaran '${exp.description}' senilai ${Formatters.formatRupiah(exp.amount)}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteExpense(exp.id)
                        expenseToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedExpense)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { expenseToDelete = null }) {
                    Text("Batal")
                }
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
