package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.VoucherEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun VouchersScreen(
    vouchers: List<VoucherEntity>,
    currentUser: UserEntity?,
    onBuyVoucher: (
        voucher: VoucherEntity,
        paymentMethod: String,
        proofUrl: String?,
        onSuccess: (OrderEntity) -> Unit
    ) -> Unit,
    onOrderSuccess: (String) -> Unit,
    onLoginRequested: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    var voucherToBuy by remember { mutableStateOf<VoucherEntity?>(null) }

    val categories = listOf("Semua", "Game Vouchers", "Google Play", "Steam", "Other Digital Vouchers")

    val filteredVouchers = remember(vouchers, selectedCategory) {
        if (selectedCategory == "Semua") vouchers
        else vouchers.filter { it.category == selectedCategory }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 90.dp)
    ) {
        Text(
            text = "Voucher Digital & Game",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = TextPrimary)
        )
        Text(
            text = "Google Play, Steam Wallet, Garena Shells, dan Gift Card Instant",
            style = MaterialTheme.typography.bodySmall.copy(color = OrangeGlow)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = GamingDarkBackground,
                        containerColor = GamingDarkSurfaceVariant,
                        labelColor = TextPrimary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredVouchers) { voucher ->
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (currentUser == null) onLoginRequested()
                            else voucherToBuy = voucher
                        }
                        .testTag("voucher_card_${voucher.id}")
                ) {
                    Icon(
                        Icons.Default.ConfirmationNumber,
                        contentDescription = null,
                        tint = OrangePrimary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = voucher.name,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary),
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = voucher.price.toRupiah(),
                        style = MaterialTheme.typography.titleMedium.copy(color = OrangeGlow, fontWeight = FontWeight.ExtraBold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            if (currentUser == null) onLoginRequested()
                            else voucherToBuy = voucher
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Beli Sekarang", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GamingDarkBackground)
                    }
                }
            }
        }
    }

    if (voucherToBuy != null) {
        BuyVoucherModal(
            voucher = voucherToBuy!!,
            currentUser = currentUser,
            onDismiss = { voucherToBuy = null },
            onSubmit = { method, proofUri ->
                onBuyVoucher(voucherToBuy!!, method, proofUri?.toString()) { order ->
                    voucherToBuy = null
                    onOrderSuccess(order.id)
                }
            }
        )
    }
}

@Composable
fun BuyVoucherModal(
    voucher: VoucherEntity,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onSubmit: (paymentMethod: String, proofUri: Uri?) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("DANA") }
    var proofUri by remember { mutableStateOf<Uri?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Beli Voucher ${voucher.name}", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Harga: ${voucher.price.toRupiah()}", color = OrangePrimary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Metode Pembayaran:", color = TextSecondary)

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
                Text("Bayar Sekarang", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal", color = TextMuted) }
        },
        containerColor = GamingDarkSurface
    )
}
