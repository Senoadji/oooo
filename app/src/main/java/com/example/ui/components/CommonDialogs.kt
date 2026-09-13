package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExpenseEntry
import com.example.model.MonthlyBalance
import com.example.model.Resident
import com.example.model.ResidentFinancialSummary
import com.example.model.ResidentRondaAttendanceSummary
import com.example.model.RondaAttendance
import com.example.model.dawisName
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BlueInfo
import com.example.ui.theme.BlueInfoBg
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.GreenIncomeBg
import com.example.ui.theme.RedExpense
import com.example.ui.theme.RedExpenseBg

@Composable
fun AddEditResidentDialog(
    resident: Resident? = null,
    onDismiss: () -> Unit,
    onSave: (houseNumber: String, name: String, phone: String, status: String, defaultAmount: Double, notes: String) -> Unit
) {
    var houseNumber by remember { mutableStateOf(resident?.houseNumber ?: "") }
    var residentName by remember { mutableStateOf(resident?.residentName ?: "") }
    var phone by remember { mutableStateOf(resident?.phone ?: "") }
    var status by remember { mutableStateOf(resident?.status ?: "Tetap") }
    var defaultAmountStr by remember { mutableStateOf(resident?.defaultAmount?.toLong()?.toString() ?: "2000") }
    var notes by remember { mutableStateOf(resident?.notes ?: "") }
    var isError by remember { mutableStateOf(false) }

    val statusOptions = listOf("Tetap", "Kontrak", "Kosong")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (resident == null) "Tambah Warga Baru" else "Edit Data Warga",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = houseNumber,
                    onValueChange = { houseNumber = it; isError = false },
                    label = { Text("Nomor Rumah * (contoh: DW1-01, DW2-05)") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                    singleLine = true,
                    isError = isError && houseNumber.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("input_house_number")
                )

                OutlinedTextField(
                    value = residentName,
                    onValueChange = { residentName = it; isError = false },
                    label = { Text("Nama Penghuni / Kepala Keluarga *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    isError = isError && residentName.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("input_resident_name")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor WhatsApp / HP") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_phone")
                )

                Text("Status Kepemilikan Rumah:", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOptions.forEach { opt ->
                        val isSelected = status == opt
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { status = opt }
                        ) {
                            Text(
                                text = opt,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = defaultAmountStr,
                    onValueChange = { defaultAmountStr = it.filter { char -> char.isDigit() } },
                    label = { Text("Nominal Jimpitan Rutin (Rp)") },
                    leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_default_amount")
                )

                Text("Pilihan Nominal Cepat (Mulai Rp 500):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(500L, 1000L, 2000L, 3000L, 5000L).forEach { amt ->
                        val isSelected = defaultAmountStr == amt.toString()
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { defaultAmountStr = amt.toString() }
                        ) {
                            Text(
                                text = Formatters.formatRupiahCompact(amt.toDouble()),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan / Jabatan RT (opsional)") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_notes")
                )

                if (isError) {
                    Text(
                        text = "Nomor rumah dan nama penghuni wajib diisi!",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (houseNumber.isBlank() || residentName.isBlank()) {
                        isError = true
                    } else {
                        val amount = defaultAmountStr.toDoubleOrNull() ?: 500.0
                        onSave(houseNumber, residentName, phone, status, amount, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("save_resident_button")
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun RecordJimpitanDialog(
    resident: Resident,
    initialDate: String = Formatters.getCurrentDateString(),
    onDismiss: () -> Unit,
    onSave: (amount: Double, date: String, collector: String, notes: String) -> Unit
) {
    var amountStr by remember { mutableStateOf(resident.defaultAmount.toLong().toString()) }
    var dateStr by remember { mutableStateOf(initialDate) }
    var collector by remember { mutableStateOf("Petugas Ronda RT 06") }
    var notes by remember { mutableStateOf("Jimpitan harian") }
    val quickAmounts = listOf(500L, 1000L, 2000L, 5000L, 10000L, 20000L, 30000L, 60000L)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Catat Jimpitan", fontWeight = FontWeight.Bold)
                Text(
                    "${resident.houseNumber} - ${resident.residentName}",
                    fontSize = 13.sp,
                    color = EmeraldPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Tanggal Jimpitan (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_jimpitan_date")
                )

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Jumlah Jimpitan (Rp) *") },
                    leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_jimpitan_amount")
                )

                Text("Pilihan Nominal Cepat (Mulai Rp 500):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.take(4).forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (amountStr == amt.toString()) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { amountStr = amt.toString() }
                        ) {
                            Text(
                                text = Formatters.formatRupiahCompact(amt.toDouble()),
                                color = if (amountStr == amt.toString()) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = if (amountStr == amt.toString()) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.drop(4).forEach { amt ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (amountStr == amt.toString()) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { amountStr = amt.toString() }
                        ) {
                            Text(
                                text = Formatters.formatRupiahCompact(amt.toDouble()),
                                color = if (amountStr == amt.toString()) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = if (amountStr == amt.toString()) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = collector,
                    onValueChange = { collector = it },
                    label = { Text("Petugas Penarik") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_collector")
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan (opsional)") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_jimpitan_notes")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountStr.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && dateStr.isNotBlank()) {
                        onSave(amount, dateStr.trim(), collector.trim(), notes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                modifier = Modifier.testTag("submit_jimpitan_button")
            ) {
                Text("Simpan Setoran")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditExpenseDialog(
    expense: ExpenseEntry? = null,
    onDismiss: () -> Unit,
    onSave: (date: String, category: String, amount: Double, description: String, receiptNotes: String) -> Unit
) {
    var dateStr by remember { mutableStateOf(expense?.date ?: Formatters.getCurrentDateString()) }
    var category by remember { mutableStateOf(expense?.category ?: "Ronda & Keamanan") }
    var amountStr by remember { mutableStateOf(expense?.amount?.toLong()?.toString() ?: "") }
    var description by remember { mutableStateOf(expense?.description ?: "") }
    var receiptNotes by remember { mutableStateOf(expense?.receiptNotes ?: "") }
    var isExpanded by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val categories = listOf(
        "Ronda & Keamanan",
        "Kebersihan & Lingkungan",
        "Kegiatan RT",
        "Sosial & Kematian",
        "Konsumsi Pertemuan",
        "Sarana & Prasarana",
        "Lain-lain"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (expense == null) "Catat Pengeluaran Kas RT" else "Edit Pengeluaran",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Tanggal Pengeluaran (YYYY-MM-DD) *") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_expense_date")
                )

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = !isExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Pengeluaran *") },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    isExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() }; isError = false },
                    label = { Text("Jumlah Pengeluaran (Rp) *") },
                    leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold, color = RedExpense) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = isError && amountStr.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("input_expense_amount")
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; isError = false },
                    label = { Text("Keperluan / Keterangan *") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    singleLine = false,
                    maxLines = 3,
                    isError = isError && description.isBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("input_expense_description")
                )

                OutlinedTextField(
                    value = receiptNotes,
                    onValueChange = { receiptNotes = it },
                    label = { Text("Penanggung Jawab / No. Nota (opsional)") },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_expense_receipt")
                )

                if (isError) {
                    Text(
                        text = "Jumlah dan keperluan pengeluaran wajib diisi!",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    if (amt <= 0 || description.isBlank()) {
                        isError = true
                    } else {
                        onSave(dateStr.trim(), category, amt, description.trim(), receiptNotes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
                modifier = Modifier.testTag("save_expense_button")
            ) {
                Text("Simpan Pengeluaran", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditStartingBalanceDialog(
    currentYear: Int,
    currentBalance: Double,
    onDismiss: () -> Unit,
    onSave: (amount: Double) -> Unit
) {
    var amountStr by remember { mutableStateOf(currentBalance.toLong().toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Saldo Kas Awal Tahun $currentYear", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Masukkan saldo modal awal kas RT 06 per 1 Januari $currentYear. Saldo ini menjadi dasar akumulasi berantai setiap bulan.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Saldo Awal Kas (Rp)") },
                    leadingIcon = { Text("Rp", modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    onSave(amt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditMonthlyDepositDialog(
    resident: Resident,
    year: Int,
    month: Int,
    currentAmount: Double,
    onDismiss: () -> Unit,
    onSave: (amount: Double, date: String, collector: String, notes: String) -> Unit
) {
    val monthName = Formatters.getMonthName(month)
    var amountStr by remember { mutableStateOf(if (currentAmount > 0) currentAmount.toLong().toString() else "") }
    var dateStr by remember {
        mutableStateOf(
            if (month == Formatters.getCurrentMonth() && year == Formatters.getCurrentYear()) {
                Formatters.getCurrentDateString()
            } else {
                String.format(java.util.Locale.US, "%d-%02d-15", year, month)
            }
        )
    }
    var collector by remember { mutableStateOf("Bendahara RT 06") }
    var notes by remember { mutableStateOf("Setoran rekap $monthName $year") }

    val dailyFee = if (resident.defaultAmount > 0) resident.defaultAmount else 1000.0
    val fee30Days = dailyFee * 30
    val fee31Days = dailyFee * 31

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(EmeraldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text("Edit Setoran $monthName $year", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "${resident.residentName} • ${resident.houseNumber}",
                        fontSize = 12.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Warga
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tarif Jimpitan Warga:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${Formatters.formatRupiahCompact(dailyFee)} / hari", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Tercatat Saat Ini:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                Formatters.formatRupiah(currentAmount),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentAmount > 0) GreenIncome else RedExpense
                            )
                        }
                    }
                }

                // Nominal Input
                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Nominal Setoran Bulan Ini (Rp) *") },
                    leadingIcon = {
                        Text(
                            "Rp",
                            modifier = Modifier.padding(start = 12.dp),
                            fontWeight = FontWeight.Bold,
                            color = EmeraldPrimary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Quick Presets
                Text(
                    "Pilihan Cepat Pengisian:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = { amountStr = fee30Days.toLong().toString() },
                        label = { Text("30 Hari (${Formatters.formatRupiahCompact(fee30Days)})", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    SuggestionChip(
                        onClick = { amountStr = fee31Days.toLong().toString() },
                        label = { Text("31 Hari (${Formatters.formatRupiahCompact(fee31Days)})", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SuggestionChip(
                        onClick = { amountStr = "50000" },
                        label = { Text("Rp 50.000", fontSize = 10.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    SuggestionChip(
                        onClick = { amountStr = "0" },
                        label = { Text("Reset (Rp 0)", fontSize = 10.sp, color = RedExpense) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Tanggal Catatan
                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Tanggal Catatan (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Penerima
                OutlinedTextField(
                    value = collector,
                    onValueChange = { collector = it },
                    label = { Text("Penerima / Petugas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Catatan
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull() ?: 0.0
                    onSave(amt, dateStr.trim(), collector.trim(), notes.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan Setoran", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun EditMonthlyCumulativeDialog(
    monthlyBalance: MonthlyBalance,
    selectedYear: Int,
    onDismiss: () -> Unit,
    onSaveAdjustment: (incomeAdj: Double, expenseAdj: Double, notes: String) -> Unit,
    onEditStartingBalance: (() -> Unit)? = null
) {
    var incomeAdjStr by remember { mutableStateOf("") }
    var expenseAdjStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("Penyesuaian kas rekap ${monthlyBalance.monthName} $selectedYear") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(EmeraldPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text("Edit Rekapitulasi Kas", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        "${monthlyBalance.monthName} $selectedYear",
                        fontSize = 13.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Info Saldo Saat Ini
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Saldo Awal Bulan:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatRupiah(monthlyBalance.startingBalance), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("(+) Pemasukan Jimpitan:", fontSize = 11.sp, color = GreenIncome)
                            Text(Formatters.formatRupiah(monthlyBalance.totalIncome), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenIncome)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("(-) Pengeluaran Kas RT:", fontSize = 11.sp, color = RedExpense)
                            Text(Formatters.formatRupiah(monthlyBalance.totalExpense), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = RedExpense)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("(=) Saldo Akhir Bulan:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                            Text(Formatters.formatRupiah(monthlyBalance.endingBalance), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                    }
                }

                if (monthlyBalance.month == 1 && onEditStartingBalance != null) {
                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onEditStartingBalance()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Ubah Saldo Modal Awal 1 Jan $selectedYear", fontSize = 12.sp)
                    }
                }

                Text(
                    "Tambah Koreksi / Penyesuaian Kas Masuk / Keluar:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = incomeAdjStr,
                    onValueChange = { incomeAdjStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Koreksi Tambahan Pemasukan (Rp)") },
                    leadingIcon = {
                        Text(
                            "+ Rp",
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Bold,
                            color = GreenIncome
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = expenseAdjStr,
                    onValueChange = { expenseAdjStr = it.filter { c -> c.isDigit() } },
                    label = { Text("Koreksi Tambahan Pengeluaran (Rp)") },
                    leadingIcon = {
                        Text(
                            "- Rp",
                            modifier = Modifier.padding(start = 10.dp),
                            fontWeight = FontWeight.Bold,
                            color = RedExpense
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Keterangan Penyesuaian") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val inc = incomeAdjStr.toDoubleOrNull() ?: 0.0
                    val exp = expenseAdjStr.toDoubleOrNull() ?: 0.0
                    onSaveAdjustment(inc, exp, notes.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan Perubahan", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResidentDetailBottomSheet(
    summary: ResidentFinancialSummary,
    selectedYear: Int,
    onDismiss: () -> Unit,
    onRecordJimpitan: () -> Unit,
    onEditResident: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onEditMonthlyDeposit: (month: Int, currentAmount: Double) -> Unit = { _, _ -> }
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(EmeraldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = summary.resident.houseNumber,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Column {
                        Text(
                            text = summary.resident.residentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = summary.resident.dawisName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (summary.resident.status == "Tetap") GreenIncomeBg else AmberSecondary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = summary.resident.status,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (summary.resident.status == "Tetap") GreenIncome else AmberSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (summary.resident.phone.isNotBlank()) {
                                Text(
                                    text = "• ${summary.resident.phone}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                IconButton(onClick = onEditResident) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Warga", tint = EmeraldPrimary)
                }
            }

            // Financial Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = GreenIncomeBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Total Terkumpul $selectedYear", fontSize = 11.sp, color = GreenIncome, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            Formatters.formatRupiah(summary.totalPaidYear),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenIncome
                        )
                        Text("${summary.paidDaysCount}x Setoran", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("Bulan Ini", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            Formatters.formatRupiah(summary.totalPaidMonth),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Tarif: ${Formatters.formatRupiahCompact(summary.resident.defaultAmount)}/hari", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Action button
            Button(
                onClick = onRecordJimpitan,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Input Setoran Jimpitan untuk Rumah Ini")
            }

            // Monthly Breakdown Grid (Bisa Diedit per Bulan)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Rekapitulasi Setoran per Bulan ($selectedYear):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "Ketuk salah satu bulan di bawah untuk mengedit nominal setoran",
                        fontSize = 11.sp,
                        color = EmeraldPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    (1..12).chunked(3).forEach { chunk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            chunk.forEach { m ->
                                val paid = summary.monthlyBreakdown[m] ?: 0.0
                                val hasPaid = paid > 0
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (hasPaid) GreenIncomeBg else Color.White,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (hasPaid) GreenIncome.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onEditMonthlyDeposit(m, paid) }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(3.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                                        ) {
                                            Text(
                                                Formatters.getMonthName(m).take(3),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (hasPaid) GreenIncome else MaterialTheme.colorScheme.onSurface
                                            )
                                            Icon(
                                                Icons.Default.Edit,
                                                contentDescription = "Edit setoran",
                                                modifier = Modifier.size(11.dp),
                                                tint = if (hasPaid) GreenIncome else EmeraldPrimary
                                            )
                                        }
                                        Text(
                                            if (hasPaid) Formatters.formatRupiahCompact(paid) else "Rp 0",
                                            fontSize = 11.sp,
                                            fontWeight = if (hasPaid) FontWeight.Bold else FontWeight.Normal,
                                            color = if (hasPaid) GreenIncome else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = if (hasPaid) GreenIncome.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                        ) {
                                            Text(
                                                if (hasPaid) "Ubah" else "+ Setor",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (hasPaid) GreenIncome else EmeraldPrimary,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Transaction History
            Text(
                "Riwayat Transaksi (${summary.transactions.size}):",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            if (summary.transactions.isEmpty()) {
                Text(
                    "Belum ada riwayat transaksi jimpitan di tahun $selectedYear.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    summary.transactions.take(10).forEach { tr ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        Formatters.formatIndonesianDate(tr.date),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "${tr.collectorName} ${if (tr.notes.isNotBlank()) "• ${tr.notes}" else ""}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        Formatters.formatRupiah(tr.amount),
                                        fontWeight = FontWeight.Bold,
                                        color = GreenIncome,
                                        fontSize = 14.sp
                                    )
                                    IconButton(
                                        onClick = { onDeleteEntry(tr.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Hapus",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PayRondaFineDialog(
    summary: ResidentRondaAttendanceSummary,
    onDismiss: () -> Unit,
    onPaySingle: (attendanceId: Long) -> Unit,
    onPayAll: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Receipt,
                    contentDescription = null,
                    tint = RedExpense,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("Pelunasan Denda Ronda", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Resident Info Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = summary.resident.residentName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "Rumah: ${summary.resident.houseNumber} • ${summary.resident.dawisName}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Accumulated Fine Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = RedExpenseBg,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Total Denda Terakumulasi",
                            fontSize = 11.sp,
                            color = RedExpense,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            Formatters.formatRupiah(summary.accumulatedUnpaidFine),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = RedExpense
                        )
                        Text(
                            "Tarif: Rp 20.000 / minggu ketidakhadiran (${summary.unpaidWeeksCount}x absen)",
                            fontSize = 11.sp,
                            color = RedExpense.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    "Daftar Minggu Ketidakhadiran:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (summary.unpaidRecords.isEmpty()) {
                    Text(
                        "Tidak ada tunggakan denda ronda untuk warga ini.",
                        fontSize = 12.sp,
                        color = GreenIncome
                    )
                } else {
                    summary.unpaidRecords.forEach { record ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, RedExpense.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        Formatters.formatIndonesianDate(record.date),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "Minggu ke-${record.weekOfYear} • Denda Rp 20.000",
                                        fontSize = 11.sp,
                                        color = RedExpense
                                    )
                                    if (record.notes.isNotBlank()) {
                                        Text(
                                            record.notes,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Button(
                                    onClick = { onPaySingle(record.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Text("Bayar 20rb", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (summary.accumulatedUnpaidFine > 0) {
                Button(
                    onClick = onPayAll,
                    colors = ButtonDefaults.buttonColors(containerColor = RedExpense),
                    modifier = Modifier.testTag("pay_all_fines_button")
                ) {
                    Text("Lunasi Semua (${Formatters.formatRupiahCompact(summary.accumulatedUnpaidFine)})")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun ManageVacantHousesDialog(
    vacantHouses: List<Resident>,
    allResidents: List<Resident>,
    onDismiss: () -> Unit,
    onOpenMarkVacant: () -> Unit,
    onRestoreHouse: (Resident) -> Unit,
    onEditVacantDetails: (Resident) -> Unit,
    onShareReport: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Rumah Kosong RT 06",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        "${vacantHouses.size} Rumah Tidak Berpenghuni / Ditinggal",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onShareReport,
                    modifier = Modifier
                        .background(AmberSecondary.copy(alpha = 0.2f), CircleShape)
                        .size(36.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Bagikan ke WA Ronda",
                        tint = Color(0xFF92400E),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                Text(
                    "Rumah-rumah ini dibebaskan dari kewajiban jimpitan harian dan menjadi fokus patroli petugas ronda setiap malam.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
                Spacer(Modifier.height(10.dp))

                if (vacantHouses.isEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = EmeraldPrimary,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Semua Rumah Berpenghuni",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                "Belum ada rumah yang ditandai kosong di lingkungan RT 06.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = onOpenMarkVacant,
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Tandai Rumah Kosong", fontSize = 12.sp)
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(vacantHouses) { house ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = Color(0xFFFEF3C7)
                                            ) {
                                                Text(
                                                    house.dawisName,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF92400E),
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFEE2E2)
                                        ) {
                                            Text(
                                                "🏠 KOSONG",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF991B1B),
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    Text(
                                        "Pemilik / Kontak: ${house.residentName}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    if (house.phone.isNotBlank()) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .padding(top = 2.dp)
                                                .clickable {
                                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${house.phone}"))
                                                    context.startActivity(dialIntent)
                                                }
                                        ) {
                                            Icon(
                                                Icons.Default.Phone,
                                                contentDescription = null,
                                                tint = EmeraldPrimary,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                house.phone,
                                                fontSize = 11.sp,
                                                color = EmeraldPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }

                                    if (house.notes.isNotBlank()) {
                                        Spacer(Modifier.height(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.Top
                                            ) {
                                                Icon(
                                                    Icons.Default.Notes,
                                                    contentDescription = null,
                                                    tint = Color(0xFF92400E),
                                                    modifier = Modifier.size(14.dp).padding(top = 1.dp)
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    house.notes,
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    lineHeight = 14.sp
                                                )
                                            }
                                        }
                                    }

                                    Spacer(Modifier.height(10.dp))
                                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    Spacer(Modifier.height(8.dp))

                                    // Baris Tombol Aksi Rumah Kosong
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        // Edit Catatan / Status
                                        OutlinedButton(
                                            onClick = { onEditVacantDetails(house) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1f),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(Modifier.width(3.dp))
                                            Text("Edit", fontSize = 11.sp)
                                        }

                                        // Hubungi Pemilik via WA
                                        Button(
                                            onClick = {
                                                val cleanPhone = house.phone.replace(Regex("[^0-9]"), "")
                                                val waNumber = if (cleanPhone.startsWith("0")) "62" + cleanPhone.substring(1) else cleanPhone
                                                val waIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$waNumber?text=Halo%20${house.residentName},%20kami%20dari%20Pengurus/Ronda%20RT%2006%20RW%2006%20KCVRI%20Berkoh."))
                                                waIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                try {
                                                    context.startActivity(waIntent)
                                                } catch (e: Exception) {
                                                    val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${house.phone}"))
                                                    context.startActivity(dial)
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1.1f),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(13.dp))
                                            Spacer(Modifier.width(3.dp))
                                            Text("WA/Telp", fontSize = 11.sp)
                                        }

                                        // Bebaskan (Sudah dihuni kembali)
                                        OutlinedButton(
                                            onClick = { onRestoreHouse(house) },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.weight(1.2f),
                                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp), tint = GreenIncome)
                                            Spacer(Modifier.width(3.dp))
                                            Text("Ada Warga", fontSize = 11.sp, color = GreenIncome, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenMarkVacant,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF92400E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("+ Tandai Rumah Kosong")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun MarkHouseAsVacantDialog(
    allResidents: List<Resident>,
    initialSelectedResident: Resident? = null,
    onDismiss: () -> Unit,
    onSave: (residentId: Long, reason: String, emergencyContact: String, notes: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedResident by remember { mutableStateOf(initialSelectedResident) }
    var selectedReason by remember { mutableStateOf("Pemilik Luar Kota / Jakarta") }
    var emergencyContact by remember { mutableStateOf(initialSelectedResident?.phone ?: "") }
    var securityNotes by remember { mutableStateOf("Kunci dititip ke Pak RT. Pantau gembok depan & lampu teras.") }
    var customReason by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val quickReasons = listOf(
        "Pemilik Luar Kota / Jakarta",
        "Tahap Renovasi",
        "Dijual / Disewakan",
        "Pindah Sementara",
        "Lainnya"
    )

    val candidateResidents = allResidents.filter { resident ->
        val q = searchQuery.trim().lowercase()
        val matchesQ = q.isEmpty() ||
                resident.houseNumber.lowercase().contains(q) ||
                resident.residentName.lowercase().contains(q)
        matchesQ
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Tandai Sebagai Rumah Kosong", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    "Pilih rumah di RT 06 yang sedang tidak berpenghuni",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (selectedResident == null) {
                    Text("Pilih Rumah / Warga:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Ketik nomor rumah / nama warga...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 160.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(candidateResidents.take(30)) { resident ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedResident = resident
                                            if (emergencyContact.isBlank()) emergencyContact = resident.phone
                                        }
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = EmeraldPrimary
                                        ) {
                                            Text(
                                                resident.houseNumber,
                                                fontSize = 10.sp,
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Text(resident.residentName, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                    Text(resident.dawisName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            }
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF92400E)) {
                                        Text(
                                            selectedResident!!.houseNumber,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        selectedResident!!.residentName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                                Text(
                                    "${selectedResident!!.dawisName} • Status saat ini: ${selectedResident!!.status}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E).copy(alpha = 0.8f)
                                )
                            }
                            TextButton(onClick = { selectedResident = null }) {
                                Text("Ganti", fontSize = 11.sp, color = Color(0xFF92400E))
                            }
                        }
                    }
                }

                // Pilihan Alasan Rumah Kosong
                Text("Alasan Rumah Kosong:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    quickReasons.forEach { reason ->
                        val isSel = selectedReason == reason
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) Color(0xFF92400E) else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReason = reason }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    if (isSel) Icons.Default.Check else Icons.Default.Home,
                                    contentDescription = null,
                                    tint = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    reason,
                                    fontSize = 11.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                if (selectedReason == "Lainnya") {
                    OutlinedTextField(
                        value = customReason,
                        onValueChange = { customReason = it },
                        label = { Text("Tuliskan alasan/kondisi rumah") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Kontak Darurat Pemilik
                OutlinedTextField(
                    value = emergencyContact,
                    onValueChange = { emergencyContact = it },
                    label = { Text("Kontak HP / WA Pemilik / Keluarga") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Catatan Keamanan Ronda (Kunci / Lampu / Listrik)
                OutlinedTextField(
                    value = securityNotes,
                    onValueChange = { securityNotes = it },
                    label = { Text("Catatan Patroli Ronda (Kunci, Lampu, dll)") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth()
                )

                if (isError && selectedResident == null) {
                    Text(
                        "Pilih nomor rumah yang ingin ditandai sebagai rumah kosong terlebih dahulu.",
                        fontSize = 11.sp,
                        color = RedExpense
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedResident == null) {
                        isError = true
                    } else {
                        val reasonText = if (selectedReason == "Lainnya") customReason.ifBlank { "Rumah Kosong" } else selectedReason
                        onSave(selectedResident!!.id, reasonText, emergencyContact.trim(), securityNotes.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF92400E)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan Status Kosong")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun RestoreHouseDialog(
    resident: Resident,
    onDismiss: () -> Unit,
    onConfirm: (newStatus: String, newName: String, newPhone: String) -> Unit
) {
    var newStatus by remember { mutableStateOf("Tetap") }
    var residentName by remember { mutableStateOf(resident.residentName) }
    var phone by remember { mutableStateOf(resident.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Kembalikan Status Berpenghuni", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Rumah ${resident.houseNumber} kini sudah ditempati kembali. Silakan perbarui nama dan status penghuni:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Tetap", "Kontrak").forEach { opt ->
                        val isSel = newStatus == opt
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { newStatus = opt }
                        ) {
                            Text(
                                "Warga $opt",
                                fontSize = 12.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = residentName,
                    onValueChange = { residentName = it },
                    label = { Text("Nama Penghuni Baru *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor HP / WhatsApp") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(newStatus, residentName.trim(), phone.trim())
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan Status Aktif")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: com.example.viewmodel.JimpitanViewModel
) {
    var pin by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Masuk Mode Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Pengurus RT & Petugas Ronda", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Mode Warga bersifat Lihat Saja (Read-Only) agar transparan bagi seluruh warga. Untuk menginput jimpitan, kas, presensi ronda, atau mengedit data, silakan masukkan PIN Admin/Petugas:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldPrimary.copy(alpha = 0.08f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "🔑 PIN Pengurus RT / Petugas: 0606",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 6) {
                            pin = it
                            showError = false
                        }
                    },
                    label = { Text("Masukkan PIN Petugas / Admin") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    placeholder = { Text("Contoh: 0606") },
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("PIN tidak sesuai! Silakan coba lagi.", color = MaterialTheme.colorScheme.error, fontSize = 11.sp) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (viewModel.loginAdmin(pin)) {
                        onLoginSuccess()
                    } else {
                        showError = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Buka Akses Edit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun PublicViewOnlyNoticeDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text("Mode Warga (Lihat Saja)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Link aplikasi ini dapat dibuka oleh siapa saja untuk melihat transparansi kas RT 06, jadwal ronda, dan rekap jimpitan.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Untuk menjaga keamanan data agar tidak sembarangan diubah, fitur input dan edit data hanya bisa diakses oleh Pengurus RT & Petugas Ronda yang memasukkan PIN (0606).",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onLoginClick()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Masuk Sebagai Petugas (PIN)")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mengerti (Hanya Lihat)")
            }
        }
    )
}

@Composable
fun ShareAppDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val cleanSharedUrl = "https://ais-pre-6dlvdopre2lwf2rurnsiqd-395089113134.asia-southeast1.run.app"
    val shareMessage = """
📢 *Aplikasi Kas & Ronda RT 06 RW 06 Perum KCVRI Berkoh*

Bapak/Ibu Warga RT 06, berikut tautan resmi untuk memantau kas warga, jimpitan harian, presensi ronda malam, dan rumah kosong secara transparan dan realtime:

🌐 $cleanSharedUrl

*(Catatan: Tautan ini langsung menampilkan aplikasi secara bersih tanpa panel chat AI atau panel editor).*
    """.trimIndent()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = EmeraldPrimary.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = null,
                            tint = EmeraldPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Bagikan Tautan Warga", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("Tampilan Preview Bersih (Tanpa Chat AI)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                            Text("Preview Bersih (Full Screen)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldPrimary)
                        }
                        Text(
                            "Tautan ini khusus disiapkan agar saat dibuka di HP/komputer warga, layar langsung menampilkan aplikasi secara penuh. Panel chat AI dan editor di luar aplikasi otomatis tersembunyi.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = BlueInfoBg),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BlueInfo.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = BlueInfo, modifier = Modifier.size(16.dp))
                            Text("Penting: Cara Mengaktifkan Tautan", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BlueInfo)
                        }
                        Text(
                            "Jika saat link dibuka muncul 'Page Not Found', Anda perlu menekan tombol 'Publish' atau 'Share' di pojok kanan atas layar AI Studio satu kali terlebih dahulu agar server publik aktif.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "💡 Tips Terbaik: Anda juga dapat mendownload APK (Menu Pengaturan AI Studio -> Generate APK) lalu membagikan file aplikasinya langsung ke grup WA warga untuk diinstall di HP.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Tautan Preview Bersih:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            cleanSharedUrl,
                            fontSize = 11.sp,
                            color = EmeraldPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val clip = ClipData.newPlainText("Link Preview RT 06", cleanSharedUrl)
                            clipboard?.setPrimaryClip(clip)
                            Toast.makeText(context, "Tautan preview bersih berhasil disalin!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Salin Link", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareMessage)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan ke WhatsApp Warga RT")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                        modifier = Modifier.weight(1.3f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Kirim WhatsApp", fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

@Composable
fun AppInfoGuideDialog(
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onOpenLogin: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = BlueInfo.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Notes,
                            contentDescription = null,
                            tint = BlueInfo,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column {
                    Text("Info & Panduan RT 06", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text("RW 06 Perum KCVRI Berkoh", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("1. Mode Tampilan Warga (Transparan)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "Warga umum dapat melihat seluruh mutasi kas, saldo, jimpitan Rp 1.000/hari, jadwal ronda 7 petugas, denda ronda Rp 20.000, serta status rumah kosong tanpa login.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldPrimary.copy(alpha = 0.08f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("2. Mode Pengurus RT & Petugas (PIN: 0606)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldPrimary)
                        Text(
                            if (isAdmin) "Status saat ini: ADMIN LOGIN AKTIF. Anda memiliki akses penuh mengubah data kas, presensi ronda, dan warga."
                            else "Status saat ini: MODE LIHAT SAJA. Klik tombol 'Login Admin' dan masukkan PIN 0606 untuk mengaktifkan akses edit.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("3. Pembagian Tautan Bersih", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            "Gunakan tombol bagikan (icon Share) di bar atas aplikasi. Tautan preview otomatis menyembunyikan panel chat AI sehingga warga menikmati tampilan aplikasi murni tanpa panel luar.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isAdmin) {
                Button(
                    onClick = {
                        onDismiss()
                        onOpenLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Login Admin (PIN)")
                }
            } else {
                Button(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                    Text("Tutup")
                }
            }
        },
        dismissButton = {
            if (!isAdmin) {
                TextButton(onClick = onDismiss) {
                    Text("Tutup")
                }
            }
        }
    )
}


