package com.example.ui.screens

import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.MarketplaceAccountEntity
import com.example.data.local.entities.UserEntity
import com.example.model.GameDataCatalog
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MarketplaceScreen(
    accounts: List<MarketplaceAccountEntity>,
    currentUser: UserEntity?,
    onAccountClicked: (String) -> Unit,
    onSubmitUserSellerAccount: (
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
    onLoginRequested: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedGameFilter by remember { mutableStateOf("Semua") }
    var selectedPriceFilter by remember { mutableStateOf("Semua") } // "Semua", "< 100k", "100k - 500k", "500k - 1M", "> 1M"
    var selectedSellerFilter by remember { mutableStateOf("Semua") } // "Semua", "JB EPIN Official", "User Seller"
    var showSellDialog by remember { mutableStateOf(false) }

    val gameOptions = listOf("Semua") + GameDataCatalog.games.map { it.name }
    val priceOptions = listOf("Semua", "< Rp100.000", "Rp100.000 - Rp500.000", "Rp500.000 - Rp1.000.000", "> Rp1.000.000")
    val sellerOptions = listOf("Semua", "JB EPIN Official", "User Seller")

    val filteredAccounts = remember(accounts, searchQuery, selectedGameFilter, selectedPriceFilter, selectedSellerFilter) {
        accounts.filter { acc ->
            val matchSearch = searchQuery.isBlank() || acc.title.contains(searchQuery, ignoreCase = true) || acc.game.contains(searchQuery, ignoreCase = true)
            val matchGame = selectedGameFilter == "Semua" || acc.game.contains(selectedGameFilter, ignoreCase = true)
            val matchSeller = when (selectedSellerFilter) {
                "JB EPIN Official" -> acc.sellerType == "official"
                "User Seller" -> acc.sellerType == "user"
                else -> true
            }
            val matchPrice = when (selectedPriceFilter) {
                "< Rp100.000" -> acc.price < 100000L
                "Rp100.000 - Rp500.000" -> acc.price in 100000L..500000L
                "Rp500.000 - Rp1.000.000" -> acc.price in 500000L..1000000L
                "> Rp1.000.000" -> acc.price > 1000000L
                else -> true
            }
            matchSearch && matchGame && matchSeller && matchPrice
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 90.dp)
    ) {
        // Top Banner / Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GamingDarkSurfaceVariant, GamingDarkBackground)))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Jual-Beli Akun Game",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Marketplace Terpercaya JB EPIN Official & User Sellers",
                            style = MaterialTheme.typography.bodySmall.copy(color = OrangeGlow)
                        )
                    }

                    Button(
                        onClick = {
                            if (currentUser == null) {
                                onLoginRequested()
                            } else {
                                showSellDialog = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("sell_account_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Jual Akun", fontWeight = FontWeight.Bold, color = GamingDarkBackground)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Cari judul akun, skin, atau level...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = OrangePrimary) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = GamingBrownBorder,
                        focusedContainerColor = GamingDarkSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_marketplace_input")
                )
            }
        }

        // Filters horizontal row
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            // Game Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(gameOptions) { option ->
                    FilterChip(
                        selected = selectedGameFilter == option,
                        onClick = { selectedGameFilter = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = GamingDarkBackground,
                            containerColor = GamingDarkSurfaceVariant,
                            labelColor = TextPrimary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Seller Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sellerOptions) { option ->
                    FilterChip(
                        selected = selectedSellerFilter == option,
                        onClick = { selectedSellerFilter = option },
                        label = { Text(option) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimaryVariant,
                            selectedLabelColor = GamingDarkBackground,
                            containerColor = GamingDarkSurface,
                            labelColor = TextPrimary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Listing Grid / List
        if (filteredAccounts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Tidak ada akun game yang cocok.",
                        style = MaterialTheme.typography.titleMedium.copy(color = TextSecondary)
                    )
                    Text(
                        text = "Coba ubah kata kunci atau filter pencarian Anda.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredAccounts) { account ->
                    MarketplaceAccountCard(
                        account = account,
                        onClick = { onAccountClicked(account.id) }
                    )
                }
            }
        }
    }

    // Modal Dialog to List User Account for Sale
    if (showSellDialog) {
        SellAccountModal(
            onDismiss = { showSellDialog = false },
            onSubmit = { game, title, price, level, rank, skins, items, desc ->
                onSubmitUserSellerAccount(
                    game, title, price, "ic_launcher_foreground", level, rank, skins, items, desc
                ) {
                    showSellDialog = false
                }
            }
        )
    }
}

@Composable
fun MarketplaceAccountCard(
    account: MarketplaceAccountEntity,
    onClick: () -> Unit
) {
    val gameCategory = remember(account.game) { GameDataCatalog.getGameById(account.game) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                1.dp,
                if (account.sellerType == "official") OrangePrimary else GamingBrownBorder,
                RoundedCornerShape(16.dp)
            )
            .testTag("account_card_${account.id}"),
        colors = CardDefaults.cardColors(containerColor = GamingDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (account.sellerType == "official") {
                    Surface(
                        color = OrangePrimary,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "JB EPIN OFFICIAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = GamingDarkBackground,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = GamingDarkSurfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Seller: ${account.sellerName}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = OrangeGlow,
                                fontSize = 9.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                if (gameCategory != null) {
                    Image(
                        painter = painterResource(id = gameCategory.drawableRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = account.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.height(38.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = GamingDarkSurfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "Lvl ${account.level}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                Surface(
                    color = GamingDarkSurfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = account.rank,
                        style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = account.price.toRupiah(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = OrangeGlow
                    )
                )

                Surface(
                    color = if (account.status == "Available") StatusSuccess.copy(alpha = 0.2f) else StatusPending.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (account.status == "Available") "Ready" else account.status,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (account.status == "Available") StatusSuccess else StatusPending,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SellAccountModal(
    onDismiss: () -> Unit,
    onSubmit: (
        game: String,
        title: String,
        price: Long,
        level: Int,
        rank: String,
        skinsCount: Int,
        itemsCount: Int,
        description: String
    ) -> Unit
) {
    var game by remember { mutableStateOf(GameDataCatalog.games.first().name) }
    var title by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var levelText by remember { mutableStateOf("") }
    var rank by remember { mutableStateOf("") }
    var skinsText by remember { mutableStateOf("") }
    var itemsText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Jual Akun Game Anda",
                style = MaterialTheme.typography.titleLarge.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                Text("Pilih Game:", color = TextSecondary)
                // Game selection buttons
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(GameDataCatalog.games) { g ->
                        FilterChip(
                            selected = game == g.name,
                            onClick = { game = g.name },
                            label = { Text(g.name, fontSize = 11.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Listing") },
                    placeholder = { Text("Contoh: Akun MLBB Collector Full Skin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Harga Jual (Rp)") },
                    placeholder = { Text("Contoh: 350000") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = levelText,
                        onValueChange = { levelText = it },
                        label = { Text("Level") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = rank,
                        onValueChange = { rank = it },
                        label = { Text("Rank Akun") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = skinsText,
                        onValueChange = { skinsText = it },
                        label = { Text("Jumlah Skin") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = itemsText,
                        onValueChange = { itemsText = it },
                        label = { Text("Jumlah Item") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Deskripsi Akun") },
                    placeholder = { Text("Jelaskan spesifikasi, keunggulan, dan riwayat akun...") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.toLongOrNull() ?: 0L
                    val level = levelText.toIntOrNull() ?: 1
                    val skins = skinsText.toIntOrNull() ?: 0
                    val items = itemsText.toIntOrNull() ?: 0

                    if (title.isNotBlank() && price > 0L) {
                        onSubmit(game, title, price, level, rank, skins, items, description)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                Text("Kirim Listing (Review Admin)", color = GamingDarkBackground, fontWeight = FontWeight.Bold)
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
