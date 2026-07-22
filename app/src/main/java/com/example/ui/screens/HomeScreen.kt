package com.example.ui.screens

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
import com.example.model.GameCategory
import com.example.model.GameDataCatalog
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onGameSelected: (String) -> Unit,
    onMarketplaceClicked: () -> Unit,
    onAccountClicked: (String) -> Unit,
    availableAccounts: List<MarketplaceAccountEntity>,
    currentUserBalance: Long? = null,
    userOrdersCount: Int = 12,
    onNavigateToDeposit: (() -> Unit)? = null,
    onVouchersClicked: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredGames = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            GameDataCatalog.games
        } else {
            GameDataCatalog.games.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    val officialAccounts = remember(availableAccounts) {
        availableAccounts.filter { it.sellerType == "official" }.take(5)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        // Balance Hero Card (from Elegant Dark Design HTML)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0x26FFFFFF),
                backgroundColor = Color(0x0CFFFFFF), // ~5% translucent glass
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Current Balance",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentUserBalance?.toRupiah() ?: "Rp 1.450.000",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Button(
                        onClick = { onNavigateToDeposit?.invoke() },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OrangePrimary,
                            contentColor = Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("deposit_hero_button")
                    ) {
                        Text(
                            text = "DEPOSIT",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0x0DFFFFFF), thickness = 1.dp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ACTIVE ORDERS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = if (userOrdersCount > 0) "$userOrdersCount Items" else "0 Items",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SELLER RATING",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Text(
                            text = "⭐ 4.9",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OrangeGlow
                            )
                        )
                    }
                }
            }
        }

        // Main Categories Grid (Matching Design HTML)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Instant Top Up Tall Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(150.dp)
                        .clickable {
                            if (filteredGames.isNotEmpty()) onGameSelected(filteredGames.first().id)
                        }
                        .border(
                            BorderStroke(1.dp, Brush.horizontalGradient(listOf(OrangePrimary, Color(0x20FFFFFF)))),
                            RoundedCornerShape(24.dp)
                        )
                        .testTag("category_instant_topup"),
                    colors = CardDefaults.cardColors(containerColor = Color(0x0CFFFFFF)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(OrangePrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = OrangePrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Instant\nTop Up",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    lineHeight = 20.sp
                                )
                            )
                            Text(
                                text = "MLBB, FF, PUBG",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Right Column Cards: Marketplace & Vouchers
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Marketplace Category
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(69.dp)
                            .clickable { onMarketplaceClicked() }
                            .border(BorderStroke(1.dp, Color(0x1AFFFFFF)), RoundedCornerShape(24.dp))
                            .testTag("category_marketplace"),
                        colors = CardDefaults.cardColors(containerColor = Color(0x0CFFFFFF)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x333B82F6)), // Light blue highlight
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Storefront,
                                    contentDescription = null,
                                    tint = Color(0xFF60A5FA),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Marketplace",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }

                    // Vouchers Category
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(69.dp)
                            .clickable { onVouchersClicked?.invoke() ?: onMarketplaceClicked() }
                            .border(BorderStroke(1.dp, Color(0x1AFFFFFF)), RoundedCornerShape(24.dp))
                            .testTag("category_vouchers"),
                        colors = CardDefaults.cardColors(containerColor = Color(0x0CFFFFFF)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x33A855F7)), // Light purple highlight
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = Color(0xFFC084FC),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Vouchers",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Hot Deals / Popular Games Row Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (filteredGames.isNotEmpty()) onGameSelected(filteredGames.first().id)
                    }
                    .border(BorderStroke(1.dp, Color(0x1AFFFFFF)), RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0x0CFFFFFF)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        filteredGames.take(3).forEach { game ->
                            Image(
                                painter = painterResource(id = game.drawableRes),
                                contentDescription = game.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, GamingDarkBackground, CircleShape)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "HOT DEALS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Popular Games",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                    }

                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Cari game favorit Anda...", color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = OrangePrimary) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = Color(0x20FFFFFF),
                focusedContainerColor = Color(0x0FFFFFFF),
                unfocusedContainerColor = Color(0x0FFFFFFF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("search_game_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Game Top-Up Section Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.SportsEsports, contentDescription = null, tint = OrangePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Game Catalog",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                )
            }
            Text(
                text = "${filteredGames.size} Games",
                style = MaterialTheme.typography.bodyMedium.copy(color = OrangeGlow)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Scrollable Cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredGames) { game ->
                GameCardItem(
                    game = game,
                    onSelect = { onGameSelected(game.id) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Featured JB EPIN Official Accounts Header
        if (officialAccounts.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = OrangePrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Akun Official JB EPIN",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                }
                TextTextButton(text = "Lihat Semua") { onMarketplaceClicked() }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(officialAccounts) { account ->
                    FeaturedAccountCard(
                        account = account,
                        onClick = { onAccountClicked(account.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun GameCardItem(
    game: GameCategory,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable { onSelect() }
            .border(1.dp, GamingBrownBorder, RoundedCornerShape(16.dp))
            .testTag("game_card_${game.id}"),
        colors = CardDefaults.cardColors(containerColor = GamingDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = game.drawableRes),
                contentDescription = game.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.dp, OrangePrimary.copy(alpha = 0.5f), CircleShape)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = game.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    fontSize = 13.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onSelect,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Top Up",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GamingDarkBackground
                    )
                )
            }
        }
    }
}

@Composable
fun FeaturedAccountCard(
    account: MarketplaceAccountEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable { onClick() }
            .border(1.dp, OrangePrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = GamingDarkSurface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = account.game,
                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                modifier = Modifier.height(40.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Rank: ${account.rank}",
                    style = MaterialTheme.typography.labelSmall.copy(color = OrangeGlow)
                )
                Text(
                    text = "Skins: ${account.skinsCount}",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = account.price.toRupiah(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = OrangeGlow
                )
            )
        }
    }
}

@Composable
fun TextTextButton(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = OrangeGlow,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier
            .clickable { onClick() }
            .padding(4.dp)
    )
}
