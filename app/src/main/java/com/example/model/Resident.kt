package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "residents")
data class Resident(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val houseNumber: String,
    val residentName: String,
    val phone: String = "",
    val status: String = "Tetap", // Tetap, Kontrak, Kosong
    val defaultAmount: Double = 2000.0,
    val notes: String = "",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

val Resident.dawisName: String
    get() = when {
        houseNumber.startsWith("DW1", ignoreCase = true) || houseNumber.startsWith("D1", ignoreCase = true) || notes.contains("Dawis 1", ignoreCase = true) -> "Dawis 1"
        houseNumber.startsWith("DW2", ignoreCase = true) || houseNumber.startsWith("D2", ignoreCase = true) || notes.contains("Dawis 2", ignoreCase = true) -> "Dawis 2"
        houseNumber.startsWith("DW3", ignoreCase = true) || houseNumber.startsWith("D3", ignoreCase = true) || notes.contains("Dawis 3", ignoreCase = true) -> "Dawis 3"
        houseNumber.startsWith("DW4", ignoreCase = true) || houseNumber.startsWith("D4", ignoreCase = true) || notes.contains("Dawis 4", ignoreCase = true) -> "Dawis 4"
        else -> "Lainnya"
    }
