package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.UserEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DepositScreen(
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onSubmitDeposit: (amount: Long, paymentMethod: String, proofUrl: String?, onSuccess: () -> Unit) -> Unit,
    onLoginRequested: () -> Unit
) {
    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Anda belum login.", color = TextPrimary)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onLoginRequested, colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)) {
                    Text("Login Sekarang", color = GamingDarkBackground)
                }
            }
        }
        return
    }

    val depositAmounts = listOf(10000L, 20000L, 50000L, 100000L, 250000L, 500000L)
    var selectedAmount by remember { mutableStateOf(100000L) }
    var selectedPaymentMethod by remember { mutableStateOf("DANA") }
    var proofUri by remember { mutableStateOf<Uri?>(null) }

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
            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = OrangePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Deposit Saldo Akun",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current Balance Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("Saldo Anda Saat Ini", style = MaterialTheme.typography.labelMedium.copy(color = TextSecondary))
            Text(
                text = currentUser.balance.toRupiah(),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Black, color = OrangePrimary)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 1: Select Amount
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("1. Pilih Nominal Deposit", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                depositAmounts.forEach { amount ->
                    val isSelected = selectedAmount == amount
                    Card(
                        modifier = Modifier
                            .width(100.dp)
                            .clickable { selectedAmount = amount }
                            .border(
                                1.dp,
                                if (isSelected) OrangePrimary else GamingBrownBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .testTag("deposit_amount_${amount}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) OrangePrimary.copy(alpha = 0.2f) else GamingDarkSurfaceVariant
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = amount.toRupiah(),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) OrangeGlow else TextPrimary,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 2: Payment Method
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("2. Pilih Metode Transfer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Spacer(modifier = Modifier.height(12.dp))

            listOf("DANA", "OVO", "GoPay").forEach { method ->
                val isSelected = selectedPaymentMethod == method
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedPaymentMethod = method }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedPaymentMethod = method },
                        colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(method, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CopyableNumberCard(
                label = "Nomor Transfer $selectedPaymentMethod",
                number = "083176864155"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 3: Payment Proof
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text("3. Unggah Bukti Transfer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary))
            Spacer(modifier = Modifier.height(12.dp))

            PaymentProofUploader(
                selectedImageUri = proofUri,
                onImageSelected = { proofUri = it },
                onRemoveImage = { proofUri = null }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        GradientGlowButton(
            text = "KIRIM DEPOSIT (MENUNGGU ADMIN)",
            enabled = proofUri != null,
            onClick = {
                onSubmitDeposit(selectedAmount, selectedPaymentMethod, proofUri?.toString()) {
                    onBack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
