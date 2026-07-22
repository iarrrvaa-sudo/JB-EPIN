package com.example.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.DepositEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
    currentUser: UserEntity?,
    userOrders: List<OrderEntity>,
    userDeposits: List<DepositEntity>,
    userTransactions: List<TransactionEntity>,
    onNavigateToDeposit: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onLoginRequested: () -> Unit,
    onLogoutRequested: () -> Unit
) {
    if (currentUser == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Anda Belum Login",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                    )
                    Text(
                        text = "Login atau daftar akun untuk melakukan top up, deposit, dan jual beli akun.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    GradientGlowButton(
                        text = "LOGIN / REGISTRASI",
                        onClick = onLoginRequested,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_register_button")
                    )
                }
            }
        }
        return
    }

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Top-Up Orders, 1: Deposits, 2: Transactions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
    ) {
        // User Info Card
        GlassCard(modifier = Modifier.fillMaxWidth(), borderColor = OrangePrimary) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser.username.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = OrangePrimary)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentUser.username,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                            )
                            if (currentUser.role == "admin") {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(color = RustTertiary, shape = RoundedCornerShape(4.dp)) {
                                    Text(
                                        text = "ADMIN",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                        Text(text = currentUser.email, style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                    }
                }

                IconButton(onClick = onLogoutRequested) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = StatusError)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Balance & Deposit Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GamingDarkSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Saldo JB EPIN", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
                    Text(
                        text = currentUser.balance.toRupiah(),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black, color = OrangePrimary)
                    )
                }

                Button(
                    onClick = onNavigateToDeposit,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("deposit_shortcut_button")
                ) {
                    Icon(Icons.Default.AddCard, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Deposit", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                }
            }

            if (currentUser.role == "admin") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onNavigateToAdmin,
                    border = BorderStroke(1.dp, OrangeGlow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_dashboard_button")
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = OrangeGlow)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BUKA DASHBOARD ADMIN", color = OrangeGlow, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = GamingDarkSurface,
            contentColor = OrangePrimary
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Pesanan (${userOrders.size})") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Deposit (${userDeposits.size})") }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Riwayat Transaksi") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTab) {
            0 -> {
                if (userOrders.isEmpty()) {
                    Text("Belum ada pesanan top-up.", color = TextMuted, modifier = Modifier.padding(16.dp))
                } else {
                    userOrders.forEach { order ->
                        OrderItemCard(order = order)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            1 -> {
                if (userDeposits.isEmpty()) {
                    Text("Belum ada riwayat deposit.", color = TextMuted, modifier = Modifier.padding(16.dp))
                } else {
                    userDeposits.forEach { dep ->
                        DepositItemCard(deposit = dep)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            2 -> {
                if (userTransactions.isEmpty()) {
                    Text("Belum ada riwayat transaksi.", color = TextMuted, modifier = Modifier.padding(16.dp))
                } else {
                    userTransactions.forEach { tx ->
                        TransactionItemCard(tx = tx)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemCard(order: OrderEntity) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(order.id, style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow, fontWeight = FontWeight.Bold))
                Text(order.product, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text("Game: ${order.game}", style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary))
                Text(order.price.toRupiah(), style = MaterialTheme.typography.bodyMedium.copy(color = OrangePrimary, fontWeight = FontWeight.Bold))
            }

            Surface(
                color = when (order.status) {
                    "Completed" -> StatusSuccess.copy(alpha = 0.2f)
                    "Rejected" -> StatusError.copy(alpha = 0.2f)
                    else -> StatusPending.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = order.status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = when (order.status) {
                            "Completed" -> StatusSuccess
                            "Rejected" -> StatusError
                            else -> StatusPending
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun DepositItemCard(deposit: DepositEntity) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(deposit.id, style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow, fontWeight = FontWeight.Bold))
                Text("Deposit Saldo (${deposit.paymentMethod})", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text(deposit.amount.toRupiah(), style = MaterialTheme.typography.titleMedium.copy(color = StatusSuccess, fontWeight = FontWeight.Bold))
            }

            Surface(
                color = when (deposit.status) {
                    "Approved" -> StatusSuccess.copy(alpha = 0.2f)
                    "Rejected" -> StatusError.copy(alpha = 0.2f)
                    else -> StatusPending.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = deposit.status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = when (deposit.status) {
                            "Approved" -> StatusSuccess
                            "Rejected" -> StatusError
                            else -> StatusPending
                        },
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionItemCard(tx: TransactionEntity) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(tx.type, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
                Text(tx.id, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            }

            Text(
                text = tx.amount.toRupiah(),
                style = MaterialTheme.typography.bodyLarge.copy(color = OrangeGlow, fontWeight = FontWeight.Bold)
            )
        }
    }
}
