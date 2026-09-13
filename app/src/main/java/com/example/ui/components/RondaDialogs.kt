package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.model.Resident
import com.example.model.ScheduledRondaOfficer
import com.example.model.dawisName
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.EmeraldPrimaryDark
import com.example.ui.theme.GreenIncome
import com.example.ui.theme.RedExpense
import com.example.ui.theme.RedExpenseBg

/**
 * Dialog to add a resident to today's ronda schedule (7+ people).
 */
@Composable
fun AddOfficerToRondaDialog(
    selectedDate: String,
    allResidents: List<Resident>,
    alreadyScheduledIds: Set<Long>,
    onDismiss: () -> Unit,
    onAddResident: (Resident) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDawis by remember { mutableStateOf("Semua") }
    val dawisOptions = listOf("Semua", "Dawis 1", "Dawis 2", "Dawis 3", "Dawis 4")

    val availableResidents = allResidents.filter { res ->
        res.isActive &&
        !alreadyScheduledIds.contains(res.id) &&
        (selectedDawis == "Semua" || res.dawisName == selectedDawis) &&
        (searchQuery.isBlank() ||
            res.residentName.contains(searchQuery, ignoreCase = true) ||
            res.houseNumber.contains(searchQuery, ignoreCase = true))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Tambah Petugas Ronda", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text(
                        Formatters.formatDayAndDate(selectedDate),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                Text(
                    "Pilih warga RT 06 untuk ditambahkan ke daftar hadir hari ini. Setiap petugas yang ditambahkan wajib centang nama, jika tidak centang otomatis terkena denda Rp 20.000.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))

                // Search field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari nama atau no rumah...", fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // Dawis filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(dawisOptions) { dawis ->
                        val isSelected = selectedDawis == dawis
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedDawis = dawis }
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

                Spacer(Modifier.height(10.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))

                if (availableResidents.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tidak ada warga yang dapat ditambahkan.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Text(
                        "Daftar Warga Tersedia (${availableResidents.size}):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(availableResidents, key = { it.id }) { resident ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pick_resident_${resident.houseNumber}"),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
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
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                resident.dawisName,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = { onAddResident(resident) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(Modifier.width(2.dp))
                                        Text("Tambah", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )
}

/**
 * Dialog to record an excused absence (Izin/Sakit) so the officer is exempted from fine.
 */
@Composable
fun ExcuseOfficerDialog(
    officer: ScheduledRondaOfficer,
    selectedDate: String,
    onDismiss: () -> Unit,
    onConfirmExcuse: (reason: String) -> Unit
) {
    val presets = listOf(
        "Izin Keperluan Keluarga / Mendesak",
        "Sakit / Tidak Enak Badan",
        "Dinas Luar Kota / Tugas Kerja",
        "Tukar Jadwal / Piket Pengganti",
        "Lainnya"
    )
    var selectedPreset by remember { mutableStateOf(presets.first()) }
    var customReason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = AmberSecondary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Form Izin / Sakit Ronda", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text(
                        "${officer.resident.houseNumber} - ${officer.resident.residentName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = AmberSecondary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Petugas yang berstatus 'IZIN' dibebaskan dari denda ronda Rp 20.000 untuk tanggal ${Formatters.formatDayAndDate(selectedDate)}.",
                        fontSize = 12.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Text("Pilih Alasan Izin:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                presets.forEach { preset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPreset = preset }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (selectedPreset == preset) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(18.dp)
                        ) {
                            if (selectedPreset == preset) {
                                Box(contentAlignment = Alignment.Center) {
                                    Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(6.dp)) {}
                                }
                            }
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(preset, fontSize = 13.sp)
                    }
                }

                if (selectedPreset == "Lainnya") {
                    OutlinedTextField(
                        value = customReason,
                        onValueChange = { customReason = it },
                        label = { Text("Tuliskan Alasan Izin") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalReason = if (selectedPreset == "Lainnya" && customReason.isNotBlank()) {
                        customReason.trim()
                    } else {
                        selectedPreset
                    }
                    onConfirmExcuse(finalReason)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary)
            ) {
                Text("Konfirmasi Izin (Bebas Denda)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

/**
 * Dialog to view and manage the 7-person weekly fixed schedule (Senin - Minggu).
 */
@Composable
fun ManageWeeklyRondaScheduleDialog(
    allResidents: List<Resident>,
    initialDay: String,
    onDismiss: () -> Unit,
    onLoadDaySchedule: (day: String, onResult: (List<Long>) -> Unit) -> Unit,
    onSaveDaySchedule: (day: String, residentIds: List<Long>) -> Unit
) {
    val daysOfWeek = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu", "Minggu")
    var selectedDay by remember { mutableStateOf(initialDay) }
    val currentOfficerIds = remember { mutableStateListOf<Long>() }
    var showAddResidentModal by remember { mutableStateOf(false) }

    fun refreshSchedule(day: String) {
        onLoadDaySchedule(day) { ids ->
            currentOfficerIds.clear()
            currentOfficerIds.addAll(ids)
        }
    }

    LaunchedEffect(selectedDay) {
        refreshSchedule(selectedDay)
    }

    val residentMap = remember(allResidents) { allResidents.associateBy { it.id } }
    val scheduledOfficers = currentOfficerIds.mapNotNull { residentMap[it] }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Security,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Jadwal Tetap Ronda 7 Orang", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Susun 7 Petugas Tetap Tiap Hari", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 440.dp)
            ) {
                Text(
                    "Pilih hari untuk mengatur 7 orang petugas ronda tetap. Setiap kali tanggal jatuh pada hari tersebut, sistem otomatis memuat daftar hadir petugas ini.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                // Day selector chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(daysOfWeek) { day ->
                        val isSelected = selectedDay.equals(day, ignoreCase = true)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EmeraldPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { selectedDay = day }
                        ) {
                            Text(
                                text = day,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Petugas Regu $selectedDay (${scheduledOfficers.size} Orang):",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    OutlinedButton(
                        onClick = { showAddResidentModal = true },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(2.dp))
                        Text("+ Petugas", fontSize = 11.sp)
                    }
                }

                Spacer(Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(Modifier.height(6.dp))

                if (scheduledOfficers.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Belum ada petugas untuk hari $selectedDay.\nKlik '+ Petugas' untuk menambahkan.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        itemsIndexed(scheduledOfficers, key = { _, r -> r.id }) { index, res ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = EmeraldPrimary.copy(alpha = 0.15f),
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    "${index + 1}",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EmeraldPrimaryDark
                                                )
                                            }
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = EmeraldPrimary
                                        ) {
                                            Text(
                                                res.houseNumber,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                            )
                                        }
                                        Column {
                                            Text(res.residentName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text(res.dawisName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }

                                    IconButton(
                                        onClick = {
                                            currentOfficerIds.remove(res.id)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Hapus",
                                            tint = RedExpense,
                                            modifier = Modifier.size(16.dp)
                                        )
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
                onClick = {
                    onSaveDaySchedule(selectedDay, currentOfficerIds.toList())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
            ) {
                Text("Simpan Jadwal Regu $selectedDay")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup")
            }
        }
    )

    // Sub-dialog to pick a resident to add to this day's fixed schedule
    if (showAddResidentModal) {
        val alreadyPicked = currentOfficerIds.toSet()
        val pickable = allResidents.filter { it.isActive && !alreadyPicked.contains(it.id) }
        var searchQ by remember { mutableStateOf("") }
        val filtered = pickable.filter {
            searchQ.isBlank() ||
            it.residentName.contains(searchQ, ignoreCase = true) ||
            it.houseNumber.contains(searchQ, ignoreCase = true)
        }

        AlertDialog(
            onDismissRequest = { showAddResidentModal = false },
            title = { Text("Pilih Petugas untuk $selectedDay", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp)) {
                    OutlinedTextField(
                        value = searchQ,
                        onValueChange = { searchQ = it },
                        placeholder = { Text("Cari warga...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        items(filtered) { r ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        currentOfficerIds.add(r.id)
                                        showAddResidentModal = false
                                    }
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = EmeraldPrimary) {
                                        Text(r.houseNumber, color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                    }
                                    Text(r.residentName, fontSize = 12.sp)
                                }
                                Text(r.dawisName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAddResidentModal = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun RondaTimeRestrictedDialog(
    status: Formatters.RondaAccessStatus,
    selectedDate: String,
    isAdminLoggedIn: Boolean,
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit,
    onAdminOverrideToggle: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFFEF3C7),
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Column {
                    Text(
                        "Akses Presensi Terkunci",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        "Khusus Jam 00:00 - 01:00 WIB",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jam Buka Presensi:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("00:00 s/d 01:00 WIB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jam Sekarang:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(status.currentTimeFormatted, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Hari & Tanggal:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(status.currentDateFormatted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Jadwal Terpilih:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatIndonesianDateShort(selectedDate), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Text(
                    "Sesuai ketentuan RT 06, ceklist daftar hadir petugas ronda hanya dapat diakses saat pos ronda aktif (pukul 00:00 - 01:00 WIB) tepat sesuai hari dan tanggal pelaksanaan ronda agar data kehadiran tertib dan akurat.",
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                if (isAdminLoggedIn) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = EmeraldPrimary.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldPrimary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                "👑 Khusus Pengurus RT (Admin Terverifikasi):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary
                            )
                            Text(
                                "Sebagai Pengurus RT, Anda dapat mengaktifkan 'Override Akses' untuk keperluan uji coba, koreksi presensi, atau penyesuaian data darurat.",
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    onAdminOverrideToggle()
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(vertical = 6.dp)
                            ) {
                                Text("Buka Akses Sekarang (Mode Pengurus)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isAdminLoggedIn) {
                Button(
                    onClick = {
                        onDismiss()
                        onLoginClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Masuk Admin RT (Koreksi)")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (isAdminLoggedIn) "Tutup" else "Mengerti")
            }
        }
    )
}

