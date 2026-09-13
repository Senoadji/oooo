package com.example.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jimpitan_entries",
    indices = [
        Index(value = ["residentId"]),
        Index(value = ["date"]),
        Index(value = ["houseNumber"])
    ]
)
data class JimpitanEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val residentId: Long,
    val houseNumber: String,
    val residentName: String,
    val date: String, // Format: YYYY-MM-DD
    val amount: Double,
    val collectorName: String = "Petugas Ronda",
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
