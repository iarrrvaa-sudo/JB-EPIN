package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.UserEntity
import com.example.model.GameDataCatalog
import com.example.model.TopUpProduct
import com.example.ui.components.*
import com.example.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopUpDetailScreen(
    gameId: String,
    currentUser: UserEntity?,
    onBack: () -> Unit,
    onSubmitOrder: (
        game: String,
        gameData: String,
        productName: String,
        price: Long,
        buyerName: String,
        buyerWhatsapp: String,
        paymentMethod: String,
        proofUrl: String?,
        onSuccess: (OrderEntity) -> Unit
    ) -> Unit,
    onOrderSuccess: (String) -> Unit
) {
    val game = remember(gameId) { GameDataCatalog.getGameById(gameId) ?: GameDataCatalog.games.first() }

    val fieldValues = remember { mutableStateMapOf<String, String>() }
    var selectedProduct by remember { mutableStateOf<TopUpProduct?>(null) }
    var buyerName by remember { mutableStateOf(currentUser?.username ?: "") }
    var buyerWhatsapp by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("DANA") }
    var proofUri by remember { mutableStateOf<Uri?>(null) }

    val isFormValid = remember(fieldValues, selectedProduct, buyerName, selectedPaymentMethod, proofUri) {
        val allFieldsFilled = game.fieldsNeeded.all { field ->
            (fieldValues[field.key] ?: "").isNotBlank()
        }
        val productChosen = selectedProduct != null
        val buyerNameFilled = buyerName.isNotBlank()
        val proofUploaded = selectedPaymentMethod == "Saldo Account" || proofUri != null
        allFieldsFilled && productChosen && buyerNameFilled && proofUploaded
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = OrangeGlow)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Image(
                painter = painterResource(id = game.drawableRes),
                contentDescription = game.name,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, OrangePrimary, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = game.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
                Text(
                    text = "Top Up Instan & Garansi Resmi",
                    style = MaterialTheme.typography.bodySmall.copy(color = OrangeGlow)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Step 1: Game Data Fields
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = OrangePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("1", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Lengkapi Data Akun Game",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            game.fieldsNeeded.forEach { field ->
                OutlinedTextField(
                    value = fieldValues[field.key] ?: "",
                    onValueChange = { fieldValues[field.key] = it },
                    label = { Text(field.label) },
                    placeholder = { Text(field.placeholder, color = TextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("topup_input_${field.key}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 2: Select Nominal / Product
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = OrangePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("2", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pilih Nominal Top-Up",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                game.products.forEach { prod ->
                    val isSelected = selectedProduct?.id == prod.id
                    Card(
                        modifier = Modifier
                            .width(160.dp)
                            .clickable { selectedProduct = prod }
                            .border(
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) OrangePrimary else GamingBrownBorder
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("product_card_${prod.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) OrangePrimary.copy(alpha = 0.15f) else GamingDarkSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            if (prod.isPopular) {
                                Surface(
                                    color = RustTertiary,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "POPULER",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Text(
                                text = prod.name,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) OrangeGlow else TextPrimary
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = prod.price.toRupiah(),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = OrangePrimary
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 3: Buyer Info
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = OrangePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("3", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Informasi Pembeli",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = buyerName,
                onValueChange = { buyerName = it },
                label = { Text("Nama Pembeli") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = GamingBrownBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = buyerWhatsapp,
                onValueChange = { buyerWhatsapp = it },
                label = { Text("Nomor WhatsApp (Untuk notifikasi status)") },
                placeholder = { Text("Contoh: 08123456789") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = GamingBrownBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step 4: Payment Method Selection
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = OrangePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("4", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Pilih Metode Pembayaran",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val methods = listOf("DANA", "OVO", "GoPay", "Saldo Account")
            methods.forEach { method ->
                val isSelected = selectedPaymentMethod == method
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedPaymentMethod = method }
                        .border(
                            1.dp,
                            if (isSelected) OrangePrimary else GamingBrownBorder,
                            RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) OrangePrimary.copy(alpha = 0.15f) else GamingDarkSurfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { selectedPaymentMethod = method },
                                colors = RadioButtonDefaults.colors(selectedColor = OrangePrimary)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = method,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }

                        if (method == "Saldo Account") {
                            Text(
                                text = "Saldo: ${(currentUser?.balance ?: 0L).toRupiah()}",
                                style = MaterialTheme.typography.bodySmall.copy(color = OrangeGlow)
                            )
                        }
                    }
                }
            }

            if (selectedPaymentMethod != "Saldo Account") {
                Spacer(modifier = Modifier.height(12.dp))
                CopyableNumberCard(
                    label = "Nomor Transfer $selectedPaymentMethod",
                    number = "083176864155",
                    accountHolder = "JB EPIN OFFICIAL"
                )

                Spacer(modifier = Modifier.height(16.dp))

                PaymentProofUploader(
                    selectedImageUri = proofUri,
                    onImageSelected = { proofUri = it },
                    onRemoveImage = { proofUri = null }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary Card & Submit Button
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = OrangePrimary
        ) {
            Text(
                text = "Ringkasan Pesanan",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextPrimary)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Game:", color = TextSecondary)
                Text(game.name, color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            game.fieldsNeeded.forEach { field ->
                val valStr = fieldValues[field.key] ?: "-"
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${field.label}:", color = TextSecondary)
                    Text(valStr, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Produk:", color = TextSecondary)
                Text(selectedProduct?.name ?: "-", color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Metode Pembayaran:", color = TextSecondary)
                Text(selectedPaymentMethod, color = TextPrimary, fontWeight = FontWeight.Bold)
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp), color = GamingBrownBorder)

            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total Bayar:", style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary))
                Text(
                    text = (selectedProduct?.price ?: 0L).toRupiah(),
                    style = MaterialTheme.typography.titleLarge.copy(color = OrangeGlow, fontWeight = FontWeight.ExtraBold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            GradientGlowButton(
                text = "BAYAR & KIRIM PESANAN",
                enabled = isFormValid,
                onClick = {
                    val gameDataStr = game.fieldsNeeded.joinToString(", ") { field ->
                        "${field.label}: ${fieldValues[field.key]}"
                    }
                    val prodName = selectedProduct?.name ?: ""
                    val price = selectedProduct?.price ?: 0L

                    onSubmitOrder(
                        game.name,
                        gameDataStr,
                        prodName,
                        price,
                        buyerName,
                        buyerWhatsapp,
                        selectedPaymentMethod,
                        proofUri?.toString()
                    ) { createdOrder ->
                        onOrderSuccess(createdOrder.id)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
