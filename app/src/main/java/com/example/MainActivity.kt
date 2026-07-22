package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.local.entities.MarketplaceAccountEntity
import com.example.ui.components.JBEpinLogoHeader
import com.example.ui.components.toRupiah
import com.example.ui.screens.*
import com.example.ui.theme.GamingDarkBackground
import com.example.ui.theme.GamingDarkSurface
import com.example.ui.theme.JBEpinTheme
import com.example.ui.theme.OrangeGlow
import com.example.ui.theme.OrangePrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.JBEpinViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: JBEpinViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            JBEpinTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                // Observe ViewModel UI Toast Events
                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                }

                val currentUser by viewModel.currentUser.collectAsState()
                val availableAccounts by viewModel.availableAccounts.collectAsState()
                val allAccounts by viewModel.allAccounts.collectAsState()
                val vouchers by viewModel.vouchers.collectAsState()
                val userOrders by viewModel.userOrders.collectAsState()
                val userDeposits by viewModel.userDeposits.collectAsState()
                val userTransactions by viewModel.userTransactions.collectAsState()
                val allOrders by viewModel.allOrders.collectAsState()
                val allDeposits by viewModel.allDeposits.collectAsState()

                val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = currentNavBackStackEntry?.destination?.route

                Scaffold(
                    topBar = {
                        // Top Header Bar with JB EPIN Logo & Balance Badge
                        TopAppBar(
                            title = {
                                JBEpinLogoHeader(
                                    logoSize = 36.dp,
                                    showSubtext = true,
                                    onLogoClick = {
                                        navController.navigate("home") {
                                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            },
                            actions = {
                                if (currentUser != null) {
                                    Surface(
                                        color = Color(0x1AFFFFFF),
                                        shape = RoundedCornerShape(20.dp),
                                        border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.4f)),
                                        modifier = Modifier
                                            .padding(end = 12.dp)
                                            .testTag("user_balance_badge")
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = currentUser?.balance?.toRupiah() ?: "Rp 0",
                                                style = MaterialTheme.typography.labelMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }
                                } else {
                                    TextButton(
                                        onClick = { navController.navigate("auth") },
                                        modifier = Modifier.testTag("header_login_button")
                                    ) {
                                        Text("Login", color = OrangePrimary, fontWeight = FontWeight.Bold)
                                    }
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = GamingDarkBackground)
                        )
                    },
                    bottomBar = {
                        // Show bottom navigation bar on main tabs
                        val mainRoutes = listOf("home", "marketplace", "vouchers", "profile")
                        if (currentRoute in mainRoutes || currentRoute?.startsWith("home") == true) {
                            Surface(
                                color = GamingDarkSurface,
                                border = BorderStroke(1.dp, Color(0x0DFFFFFF))
                            ) {
                                NavigationBar(
                                    containerColor = Color.Transparent,
                                    contentColor = OrangePrimary,
                                    modifier = Modifier.testTag("bottom_navigation_bar")
                                ) {
                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                        label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                        selected = currentRoute == "home",
                                        onClick = {
                                            navController.navigate("home") {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            selectedTextColor = OrangePrimary,
                                            indicatorColor = OrangePrimary,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_item_home")
                                    )

                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Storefront, contentDescription = "Marketplace") },
                                        label = { Text("Market", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                        selected = currentRoute == "marketplace",
                                        onClick = {
                                            navController.navigate("marketplace") {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            selectedTextColor = OrangePrimary,
                                            indicatorColor = OrangePrimary,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_item_marketplace")
                                    )

                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "Vouchers") },
                                        label = { Text("Vouchers", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                        selected = currentRoute == "vouchers",
                                        onClick = {
                                            navController.navigate("vouchers") {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            selectedTextColor = OrangePrimary,
                                            indicatorColor = OrangePrimary,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_item_vouchers")
                                    )

                                    NavigationBarItem(
                                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                        label = { Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                                        selected = currentRoute == "profile",
                                        onClick = {
                                            navController.navigate("profile") {
                                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = Color.Black,
                                            selectedTextColor = OrangePrimary,
                                            indicatorColor = OrangePrimary,
                                            unselectedIconColor = TextMuted,
                                            unselectedTextColor = TextMuted
                                        ),
                                        modifier = Modifier.testTag("nav_item_profile")
                                    )
                                }
                            }
                        }
                    },
                    containerColor = GamingDarkBackground
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 1. Home Screen
                        composable("home") {
                            HomeScreen(
                                onGameSelected = { gameId ->
                                    navController.navigate("topup/$gameId")
                                },
                                onMarketplaceClicked = {
                                    navController.navigate("marketplace")
                                },
                                onAccountClicked = { accountId ->
                                    navController.navigate("account/$accountId")
                                },
                                availableAccounts = availableAccounts,
                                currentUserBalance = currentUser?.balance,
                                userOrdersCount = userOrders.size,
                                onNavigateToDeposit = { navController.navigate("deposit") },
                                onVouchersClicked = { navController.navigate("vouchers") }
                            )
                        }

                        // 2. Marketplace Screen
                        composable("marketplace") {
                            MarketplaceScreen(
                                accounts = availableAccounts,
                                currentUser = currentUser,
                                onAccountClicked = { accountId ->
                                    navController.navigate("account/$accountId")
                                },
                                onSubmitUserSellerAccount = { game, title, price, img, lvl, rank, skins, items, desc, onSuccess ->
                                    viewModel.submitUserSellerAccount(
                                        game, title, price, img, lvl, rank, skins, items, desc, onSuccess
                                    )
                                },
                                onLoginRequested = { navController.navigate("auth") }
                            )
                        }

                        // 3. Vouchers Screen
                        composable("vouchers") {
                            VouchersScreen(
                                vouchers = vouchers,
                                currentUser = currentUser,
                                onBuyVoucher = { voucher, method, proofUrl, onSuccess ->
                                    viewModel.buyVoucher(voucher, method, proofUrl, onSuccess)
                                },
                                onOrderSuccess = { orderId ->
                                    navController.navigate("order_success/$orderId")
                                },
                                onLoginRequested = { navController.navigate("auth") }
                            )
                        }

                        // 4. Profile Screen
                        composable("profile") {
                            ProfileScreen(
                                currentUser = currentUser,
                                userOrders = userOrders,
                                userDeposits = userDeposits,
                                userTransactions = userTransactions,
                                onNavigateToDeposit = { navController.navigate("deposit") },
                                onNavigateToAdmin = { navController.navigate("admin") },
                                onLoginRequested = { navController.navigate("auth") },
                                onLogoutRequested = { viewModel.logout() }
                            )
                        }

                        // 5. Game Top Up Detail Screen
                        composable(
                            route = "topup/{gameId}",
                            arguments = listOf(navArgument("gameId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val gameId = backStackEntry.arguments?.getString("gameId") ?: "mlbb"
                            TopUpDetailScreen(
                                gameId = gameId,
                                currentUser = currentUser,
                                onBack = { navController.popBackStack() },
                                onSubmitOrder = { game, gameData, prod, price, name, wa, method, proofUrl, onSuccess ->
                                    viewModel.submitTopUpOrder(
                                        game, gameData, prod, price, name, wa, method, proofUrl, onSuccess
                                    )
                                },
                                onOrderSuccess = { orderId ->
                                    navController.navigate("order_success/$orderId") {
                                        popUpTo("home")
                                    }
                                }
                            )
                        }

                        // 6. Account Detail Screen
                        composable(
                            route = "account/{accountId}",
                            arguments = listOf(navArgument("accountId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val accountId = backStackEntry.arguments?.getString("accountId") ?: ""
                            val account = availableAccounts.find { it.id == accountId } ?: allAccounts.find { it.id == accountId }
                            AccountDetailScreen(
                                account = account,
                                currentUser = currentUser,
                                onBack = { navController.popBackStack() },
                                onBuyAccount = { acc, method, proofUrl, onSuccess ->
                                    viewModel.buyMarketplaceAccount(acc, method, proofUrl, onSuccess)
                                },
                                onOrderSuccess = { orderId ->
                                    navController.navigate("order_success/$orderId") {
                                        popUpTo("home")
                                    }
                                },
                                onLoginRequested = { navController.navigate("auth") }
                            )
                        }

                        // 7. Deposit Screen
                        composable("deposit") {
                            DepositScreen(
                                currentUser = currentUser,
                                onBack = { navController.popBackStack() },
                                onSubmitDeposit = { amount, method, proofUrl, onSuccess ->
                                    viewModel.submitDeposit(amount, method, proofUrl, onSuccess)
                                },
                                onLoginRequested = { navController.navigate("auth") }
                            )
                        }

                        // 8. Auth Screen (Login / Register)
                        composable("auth") {
                            AuthScreen(
                                onBack = { navController.popBackStack() },
                                onLogin = { email, pass ->
                                    viewModel.login(email, pass) {
                                        navController.popBackStack()
                                    }
                                },
                                onRegister = { username, email, pass, confirmPass ->
                                    viewModel.register(username, email, pass, confirmPass) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }

                        // 9. Admin Dashboard Screen
                        composable("admin") {
                            AdminDashboardScreen(
                                currentUser = currentUser,
                                allOrders = allOrders,
                                allDeposits = allDeposits,
                                allAccounts = allAccounts,
                                onBack = { navController.popBackStack() },
                                onApproveDeposit = { depId -> viewModel.approveDeposit(depId) },
                                onRejectDeposit = { depId -> viewModel.rejectDeposit(depId) },
                                onUpdateOrderStatus = { orderId, status -> viewModel.updateOrderStatus(orderId, status) },
                                onAddOfficialAccount = { game, title, price, img, lvl, rank, skins, items, desc, onSuccess ->
                                    viewModel.addOfficialAccountStock(game, title, price, img, lvl, rank, skins, items, desc, onSuccess)
                                },
                                onUpdateAccountStatus = { accId, status -> viewModel.updateMarketplaceAccountStatus(accId, status) },
                                onDeleteAccount = { accId -> viewModel.deleteMarketplaceAccount(accId) }
                            )
                        }

                        // 10. Order Success Receipt Screen
                        composable(
                            route = "order_success/{orderId}",
                            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
                            OrderSuccessScreen(
                                orderId = orderId,
                                onBackHome = {
                                    navController.navigate("home") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                },
                                onViewProfile = {
                                    navController.navigate("profile") {
                                        popUpTo("home")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
