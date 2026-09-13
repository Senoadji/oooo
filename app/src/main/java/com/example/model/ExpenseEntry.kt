package com.example.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expense_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["category"])
    ]
)
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // Format: YYYY-MM-DD
    val category: String, // Ronda & Keamanan, Kegiatan RT, Kebersihan, Sosial & Kematian, Konsumsi, Sarpras, Lain-lain
    val amount: Double,
    val description: String,
    val receiptNotes: String = "", // Penanggung Jawab / Bukti
    val timestamp: Long = System.currentTimeMillis()
)
