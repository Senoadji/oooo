package com.example.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.ExpenseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expense_entries ORDER BY date DESC, timestamp DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries WHERE date = :date ORDER BY timestamp DESC")
    fun getExpensesByDate(date: String): Flow<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC")
    fun getExpensesByMonth(monthPrefix: String): Flow<List<ExpenseEntry>> // e.g. "2026-09"

    @Query("SELECT * FROM expense_entries WHERE date LIKE :yearPrefix || '%' ORDER BY date DESC")
    fun getExpensesByYear(yearPrefix: String): Flow<List<ExpenseEntry>> // e.g. "2026"

    @Query("SELECT * FROM expense_entries WHERE category = :category ORDER BY date DESC")
    fun getExpensesByCategory(category: String): Flow<List<ExpenseEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntry): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpenses(expenses: List<ExpenseEntry>)

    @Update
    suspend fun updateExpense(expense: ExpenseEntry)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntry)

    @Query("DELETE FROM expense_entries WHERE id = :id")
    suspend fun deleteExpenseById(id: Long)

    @Query("SELECT SUM(amount) FROM expense_entries WHERE date LIKE :yearPrefix || '%'")
    fun getTotalExpenseByYear(yearPrefix: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expense_entries WHERE date LIKE :monthPrefix || '%'")
    fun getTotalExpenseByMonth(monthPrefix: String): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expense_entries WHERE date = :date")
    fun getTotalExpenseByDate(date: String): Flow<Double?>

    @Query("DELETE FROM expense_entries")
    suspend fun deleteAllExpenses()

    @Query("SELECT COUNT(*) FROM expense_entries")
    suspend fun getExpenseCount(): Int
}
