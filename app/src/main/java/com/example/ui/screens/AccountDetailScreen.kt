package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.MarketplaceAccountEntity
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.UserEntity
import com.example.model.GameDataCatalog
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun AccountDetailScreen(
    account: MarketplaceAccountEntity?,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onBuyAccount: (
        account: MarketplaceAccountEntity,
        paymentMethod: String,
        proofUrl: String?,
        onSuccess: (OrderEntity) -> Unit
    ) -> Unit,
    onOrderSuccess: (String) -> Unit,
    onLoginRequested: () -> Unit
) {
    if (account == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Akun tidak ditemukan.", color = TextPrimary)
        }
        return
    }

    var showBuyModal by remember { mutableStateOf(false) }
    val gameCategory = remember(account.game) { GameDataCatalog.getGameById(account.game) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Top Image / Header Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(GamingDarkSurfaceVariant)
        ) {
            if (gameCategory != null) {
                Image(
                    painter = painterResource(id = gameCategory.drawableRes),
                    contentDescription = account.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, GamingDarkBackground)
                        )
                    )
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(GamingDarkBackground.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = TextPrimary)
            }

            if (account.sellerType == "official") {
                Surface(
                    color = OrangePrimary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = GamingDarkBackground, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "JB EPIN OFFICIAL STOCK",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GamingDarkBackground
                            )
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = account.game,
                style = MaterialTheme.typography.titleMedium.copy(color = OrangeGlow, fontWeight = FontWeight.Bold)
            )

            Text(
                text = account.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = account.price.toRupiah(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = OrangePrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Specs Grid
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Spesifikasi Akun",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SpecBox(label = "Level", value = account.level.toString())
                    SpecBox(label = "Rank", value = account.rank)
                    SpecBox(label = "Skin Count", value = account.skinsCount.toString())
                    SpecBox(label = "Items", value = account.itemsCount.toString())
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description Box
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Deskripsi & Keunggulan",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = account.description,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Seller Info Card
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Penjual: ${account.sellerName}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                        )
                        Text(
                            text = if (account.sellerType == "official") "Garansi seumur hidup & bantuan transfer 24/7 dari tim JB EPIN." else "User verified marketplace seller.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            GradientGlowButton(
                text = "BELI AKUN INI SEKARANG",
                onClick = {
                    if (currentUser == null) {
                        onLoginRequested()
                    } else {
                        showBuyModal = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("buy_account_confirm_button")
            )
        }
    }

    if (showBuyModal) {
        BuyAccountCheckoutModal(
            account = account,
            currentUser = currentUser,
            onDismiss = { showBuyModal = false },
            onSubmit = { method, proofUri ->
                onBuyAccount(account, method, proofUri?.toString()) { createdOrder ->
                    showBuyModal = false
                    onOrderSuccess(createdOrder.id)
                }
            }
        )
    }
}

@Composable
fun SpecBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = OrangeGlow
            )
        )
    }
}

@Composable
fun BuyAccountCheckoutModal(
    account: MarketplaceAccountEntity,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSubmit: (paymentMethod: String, proofUri: Uri?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("DANA") }
    var proofUri by remember { mutableStateOf<Uri?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Pembelian Akun Game",
                style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(account.title, style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold))
                Text("Total: ${account.price.toRupiah()}", style = MaterialTheme.typography.titleMedium.copy(color = OrangePrimary, fontWeight = FontWeight.ExtraBold))

                Spacer(modifier = Modifier.height(12.dp))

                Text("Pilih Metode Pembayaran:", color = TextSecondary)

                listOf("DANA", "OVO", "GoPay", "Saldo Account").forEach { method ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedMethod = method }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedMethod == method,
                            onClick = { selectedMethod = method },
                            colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(method, color = TextPrimary)
                    }
                }

                if (selectedMethod != "Saldo Account") {
                    Spacer(modifier = Modifier.height(8.dp))
                    CopyableNumberCard(label = "Transfer ke $selectedMethod", number = "083176864155")
                    Spacer(modifier = Modifier.height(12.dp))
                    PaymentProofUploader(
                        selectedImageUri = proofUri,
                        onImageSelected = { proofUri = it },
                        onRemoveImage = { proofUri = null }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(selectedMethod, proofUri) },
                enabled = selectedMethod == "Saldo Account" || proofUri != null,
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Konfirmasi Pembelian", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = TextMuted)
            }
        },
        containerColor = GamingDarkSurface
    )
}
