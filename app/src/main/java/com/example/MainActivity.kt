package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddEditExpenseDialog
import com.example.ui.components.AddEditResidentDialog
import com.example.ui.components.AdminLoginDialog
import com.example.ui.components.AppInfoGuideDialog
import com.example.ui.components.ShareAppDialog
import com.example.ui.screens.DailyChecklistScreen
import com.example.ui.screens.ExpenseScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MonthlyCumulativeScreen
import com.example.ui.screens.ResidentLedgerScreen
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.JimpitanTheme
import com.example.viewmodel.JimpitanViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: JimpitanViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JimpitanTheme {
                val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsStateWithLifecycle()
                var selectedNavIndex by remember { mutableIntStateOf(0) }
                var showAddResidentDialog by remember { mutableStateOf(false) }
                var showAddExpenseDialog by remember { mutableStateOf(false) }
                var showShareDialog by remember { mutableStateOf(false) }
                var showInfoDialog by remember { mutableStateOf(false) }
                var showAdminLoginDialog by remember { mutableStateOf(false) }

                val navItems = listOf(
                    NavItem("Beranda", Icons.Default.Home, "nav_home"),
                    NavItem("Jimpitan", Icons.Default.CheckCircle, "nav_checklist"),
                    NavItem("Warga", Icons.Default.People, "nav_residents"),
                    NavItem("Pengeluaran", Icons.Default.ReceiptLong, "nav_expenses"),
                    NavItem("Rekap", Icons.Default.TrendingUp, "nav_reports")
                )

                // Tampilan Penuh Layar HP (Full Screen Edge-to-Edge Mobile App)
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "Jimpitan RT 06 Berkoh",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "RW 06 • Kas Warga & Ronda Malam",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.85f)
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = { showShareDialog = true }) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "Bagikan Tautan Preview",
                                        tint = Color.White
                                    )
                                }
                                IconButton(onClick = { showInfoDialog = true }) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Info & Panduan",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = EmeraldPrimary,
                                titleContentColor = Color.White
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp
                        ) {
                            navItems.forEachIndexed { index, item ->
                                NavigationBarItem(
                                    selected = selectedNavIndex == index,
                                    onClick = { selectedNavIndex = index },
                                    icon = { Icon(item.icon, contentDescription = item.label) },
                                    label = {
                                        Text(
                                            item.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (selectedNavIndex == index) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = EmeraldPrimary,
                                        selectedTextColor = EmeraldPrimary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag(item.testTag)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AppScreenContent(
                            selectedNavIndex = selectedNavIndex,
                            viewModel = viewModel,
                            onNavigateToIndex = { selectedNavIndex = it },
                            onOpenAddResident = { showAddResidentDialog = true },
                            onOpenAddExpense = { showAddExpenseDialog = true }
                        )
                    }
                }

                // Global Add Resident Dialog
                if (showAddResidentDialog) {
                    AddEditResidentDialog(
                        onDismiss = { showAddResidentDialog = false },
                        onSave = { houseNo, name, phone, status, defAmt, notes ->
                            viewModel.addResident(houseNo, name, phone, status, defAmt, notes)
                            showAddResidentDialog = false
                        }
                    )
                }

                // Global Add Expense Dialog
                if (showAddExpenseDialog) {
                    AddEditExpenseDialog(
                        onDismiss = { showAddExpenseDialog = false },
                        onSave = { date, cat, amt, desc, receipt ->
                            viewModel.addExpense(date, cat, amt, desc, receipt)
                            showAddExpenseDialog = false
                        }
                    )
                }

                if (showShareDialog) {
                    ShareAppDialog(onDismiss = { showShareDialog = false })
                }

                if (showInfoDialog) {
                    AppInfoGuideDialog(
                        isAdmin = isAdminLoggedIn,
                        onDismiss = { showInfoDialog = false },
                        onOpenLogin = { showAdminLoginDialog = true }
                    )
                }

                if (showAdminLoginDialog) {
                    AdminLoginDialog(
                        onDismiss = { showAdminLoginDialog = false },
                        onLoginSuccess = { showAdminLoginDialog = false },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun AppScreenContent(
    selectedNavIndex: Int,
    viewModel: JimpitanViewModel,
    onNavigateToIndex: (Int) -> Unit,
    onOpenAddResident: () -> Unit,
    onOpenAddExpense: () -> Unit
) {
    when (selectedNavIndex) {
        0 -> HomeScreen(
            viewModel = viewModel,
            onNavigateToChecklist = { onNavigateToIndex(1) },
            onNavigateToResidents = { onNavigateToIndex(2) },
            onNavigateToExpenses = { onNavigateToIndex(3) },
            onNavigateToReports = { onNavigateToIndex(4) },
            onOpenAddExpense = onOpenAddExpense,
            onOpenAddResident = onOpenAddResident
        )
        1 -> DailyChecklistScreen(
            viewModel = viewModel
        )
        2 -> ResidentLedgerScreen(
            viewModel = viewModel,
            onOpenAddResident = onOpenAddResident
        )
        3 -> ExpenseScreen(
            viewModel = viewModel,
            onOpenAddExpense = onOpenAddExpense
        )
        4 -> MonthlyCumulativeScreen(
            viewModel = viewModel
        )
    }
}

data class NavItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
)
