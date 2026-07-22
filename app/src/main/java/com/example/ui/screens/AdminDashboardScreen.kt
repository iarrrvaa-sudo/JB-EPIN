package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DepositEntity
import com.example.data.local.entities.MarketplaceAccountEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.UserEntity
import com.example.model.GameDataCatalog
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    currentUser: UserEntity?,
    allOrders: List<OrderEntity>,
    allDeposits: List<DepositEntity>,
    allAccounts: List<MarketplaceAccountEntity>,
    onBack: () -> Unit,
    onApproveDeposit: (String) -> Unit,
    onRejectDeposit: (String) -> Unit,
    onUpdateOrderStatus: (String, String) -> Unit,
    onAddOfficialAccount: (
        game: String,
        title: String,
        price: Long,
        imagesJson: String,
        level: Int,
        rank: String,
        skinsCount: Int,
        itemsCount: Int,
        description: String,
        onSuccess: () -> Unit
    ) -> Unit,
    onUpdateAccountStatus: (String, String) -> Unit,
    onDeleteAccount: (String) -> Unit
) {
    if (currentUser == null || currentUser.role != "admin") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Akses Ditolak. Halaman khusus Administrator JB EPIN.", color = StatusError, fontWeight = FontWeight.Bold)
        }
        return
    }

    var selectedAdminSection by remember { mutableIntStateOf(0) } // 0: Deposits, 1: Orders, 2: Stock Management, 3: User Listings
    var showAddStockModal by remember { mutableStateOf(false) }

    val pendingDeposits = remember(allDeposits) { allDeposits.filter { it.status == "Waiting for Verification" } }
    val pendingOrders = remember(allOrders) { allOrders.filter { it.status == "Waiting for Verification" || it.status == "Processing" } }
    val pendingUserListings = remember(allAccounts) { allAccounts.filter { it.sellerType == "user" && it.status == "Pending Review" } }
    val officialStockAccounts = remember(allAccounts) { allAccounts.filter { it.sellerType == "official" } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = OrangeGlow)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = OrangePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Dashboard Admin JB EPIN",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Grid
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StatCard(
                title = "Deposit Pending",
                count = pendingDeposits.size.toString(),
                color = StatusPending,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Pesanan Top-Up",
                count = pendingOrders.size.toString(),
                color = OrangePrimary,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Stok Official",
                count = officialStockAccounts.size.toString(),
                color = StatusSuccess,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Admin Navigation Tabs
        TabRow(
            selectedTabIndex = selectedAdminSection,
            containerColor = GamingDarkSurface,
            contentColor = OrangePrimary
        ) {
            Tab(selected = selectedAdminSection == 0, onClick = { selectedAdminSection = 0 }, text = { Text("Deposit (${pendingDeposits.size})") })
            Tab(selected = selectedAdminSection == 1, onClick = { selectedAdminSection = 1 }, text = { Text("Pesanan (${pendingOrders.size})") })
            Tab(selected = selectedAdminSection == 2, onClick = { selectedAdminSection = 2 }, text = { Text("Stok Official") })
            Tab(selected = selectedAdminSection == 3, onClick = { selectedAdminSection = 3 }, text = { Text("Review Seller (${pendingUserListings.size})") })
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedAdminSection) {
            0 -> {
                // Deposit Management Section
                if (allDeposits.isEmpty()) {
                    Text("Belum ada data deposit.", color = TextMuted)
                } else {
                    allDeposits.forEach { dep ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(dep.id, style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow))
                                    Text("User: ${dep.userName}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                    Text("Metode: ${dep.paymentMethod}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                    Text(dep.amount.toRupiah(), style = MaterialTheme.typography.titleMedium.copy(color = StatusSuccess, fontWeight = FontWeight.ExtraBold))
                                }

                                Surface(color = GamingDarkSurfaceVariant, shape = RoundedCornerShape(4.dp)) {
                                    Text(dep.status, color = OrangeGlow, modifier = Modifier.padding(6.dp), style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (dep.status == "Waiting for Verification") {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onApproveDeposit(dep.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Setujui (Tambah Saldo)", color = GamingDarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }

                                    OutlinedButton(
                                        onClick = { onRejectDeposit(dep.id) },
                                        border = BorderStroke(1.dp, StatusError),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Tolak", color = StatusError, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            1 -> {
                // Orders Management
                if (allOrders.isEmpty()) {
                    Text("Belum ada pesanan.", color = TextMuted)
                } else {
                    allOrders.forEach { order ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text(order.id, style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow))
                            Text("${order.game} - ${order.product}", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                            Text("Data: ${order.gameData}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            Text("Pembeli: ${order.buyerName} (${order.buyerWhatsapp})", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                            Text(order.price.toRupiah(), style = MaterialTheme.typography.titleMedium.copy(color = OrangePrimary, fontWeight = FontWeight.ExtraBold))

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = { onUpdateOrderStatus(order.id, "Completed") },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Selesai", fontSize = 10.sp, color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { onUpdateOrderStatus(order.id, "Processing") },
                                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Proses", fontSize = 10.sp, color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onUpdateOrderStatus(order.id, "Rejected") },
                                    border = BorderStroke(1.dp, StatusError),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Tolak", fontSize = 10.sp, color = StatusError, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            2 -> {
                // Official JB EPIN Account Inventory Management
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Stok Akun Resmi JB EPIN", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                    Button(
                        onClick = { showAddStockModal = true },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        modifier = Modifier.testTag("add_official_stock_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tambah Stok", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                officialStockAccounts.forEach { acc ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(acc.game, style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow))
                                Text(acc.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                                Text("Lvl ${acc.level} | Rank: ${acc.rank} | Skins: ${acc.skinsCount}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                                Text(acc.price.toRupiah(), style = MaterialTheme.typography.titleMedium.copy(color = OrangePrimary, fontWeight = FontWeight.Bold))
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(acc.status, color = if (acc.status == "Available") StatusSuccess else StatusPending, fontWeight = FontWeight.Bold)
                                Row {
                                    IconButton(onClick = {
                                        val newStatus = if (acc.status == "Available") "Sold" else "Available"
                                        onUpdateAccountStatus(acc.id, newStatus)
                                    }) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Toggle Status", tint = OrangeGlow)
                                    }
                                    IconButton(onClick = { onDeleteAccount(acc.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = StatusError)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            3 -> {
                // User Seller Submissions
                if (pendingUserListings.isEmpty()) {
                    Text("Tidak ada pengajuan akun user seller.", color = TextMuted)
                } else {
                    pendingUserListings.forEach { acc ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Text("Seller: ${acc.sellerName}", style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow))
                            Text(acc.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                            Text("Game: ${acc.game} | Harga: ${acc.price.toRupiah()}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { onUpdateAccountStatus(acc.id, "Available") },
                                    colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Setujui (Tayangkan)", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                                }

                                OutlinedButton(
                                    onClick = { onUpdateAccountStatus(acc.id, "Rejected") },
                                    border = BorderStroke(1.dp, StatusError),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Tolak", color = StatusError, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }

    if (showAddStockModal) {
        SellAccountModal(
            onDismiss = { showAddStockModal = false },
            onSubmit = { game, title, price, level, rank, skins, items, desc ->
                onAddOfficialAccount(
                    game, title, price, "ic_launcher_foreground", level, rank, skins, items, desc
                ) {
                    showAddStockModal = false
                }
            }
        )
    }
}

@Composable
fun StatCard(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = GamingDarkSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted), maxLines = 1)
            Text(count, style = MaterialTheme.typography.headlineMedium.copy(color = color, fontWeight = FontWeight.ExtraBold))
        }
    }
}
