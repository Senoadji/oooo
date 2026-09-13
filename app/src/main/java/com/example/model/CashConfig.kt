package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash_config")
data class CashConfig(
    @PrimaryKey
    val key: String, // e.g. "STARTING_BALANCE_2026", "RT_NAME", "DEFAULT_JIMPITAN"
    val value: String
)
