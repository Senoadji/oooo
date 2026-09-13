package com.example.model

data class MonthlyBalance(
    val year: Int,
    val month: Int, // 1 to 12
    val monthName: String, // "Januari", "Februari", etc.
    val startingBalance: Double, // Saldo awal bulan (saldo akhir bulan sebelumnya)
    val totalIncome: Double, // Total pemasukan jimpitan bulan ini
    val totalExpense: Double, // Total pengeluaran kas jimpitan bulan ini
    val netChange: Double = totalIncome - totalExpense,
    val endingBalance: Double = startingBalance + totalIncome - totalExpense, // Saldo akhir bulan
    val jimpitanCount: Int = 0,
    val expenseCount: Int = 0
)

data class DailyBalance(
    val date: String, // YYYY-MM-DD
    val totalIncome: Double,
    val totalExpense: Double,
    val netChange: Double = totalIncome - totalExpense,
    val jimpitanEntries: List<JimpitanEntry> = emptyList(),
    val expenseEntries: List<ExpenseEntry> = emptyList()
)

data class ResidentFinancialSummary(
    val resident: Resident,
    val totalPaidYear: Double,
    val totalPaidMonth: Double,
    val paidDaysCount: Int,
    val lastPaymentDate: String?,
    val monthlyBreakdown: Map<Int, Double>, // Month (1..12) -> Amount
    val transactions: List<JimpitanEntry> = emptyList()
)

data class GlobalFinancialReport(
    val year: Int,
    val startingBalanceYear: Double,
    val totalIncomeYear: Double,
    val totalExpenseYear: Double,
    val currentBalance: Double,
    val monthlyBalances: List<MonthlyBalance>,
    val totalHouseholds: Int,
    val activeHouseholds: Int
)
