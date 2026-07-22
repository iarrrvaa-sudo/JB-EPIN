package com.example.model

import com.example.R

data class GameCategory(
    val id: String,
    val name: String,
    val drawableRes: Int,
    val fieldsNeeded: List<GameField>,
    val products: List<TopUpProduct>
)

data class GameField(
    val key: String,
    val label: String,
    val placeholder: String
)

data class TopUpProduct(
    val id: String,
    val name: String,
    val price: Long,
    val isPopular: Boolean = false
)

data class PaymentMethodInfo(
    val name: String,
    val number: String = "083176864155",
    val accountHolder: String = "JB EPIN OFFICIAL",
    val iconRes: Int? = null
)

object GameDataCatalog {

    val paymentMethods = listOf(
        PaymentMethodInfo("DANA", "083176864155", "JB EPIN"),
        PaymentMethodInfo("OVO", "083176864155", "JB EPIN"),
        PaymentMethodInfo("GoPay", "083176864155", "JB EPIN")
    )

    val games = listOf(
        GameCategory(
            id = "mlbb",
            name = "Mobile Legends: Bang Bang",
            drawableRes = R.drawable.ic_game_mlbb,
            fieldsNeeded = listOf(
                GameField("user_id", "User ID", "Contoh: 12345678"),
                GameField("zone_id", "Zone ID", "Contoh: 1234")
            ),
            products = listOf(
                TopUpProduct("ml_86", "86 Diamonds", 20000),
                TopUpProduct("ml_172", "172 Diamonds", 40000),
                TopUpProduct("ml_257", "257 Diamonds (+29 Bonus)", 60000, isPopular = true),
                TopUpProduct("ml_344", "344 Diamonds", 80000),
                TopUpProduct("ml_429", "429 Diamonds", 100000),
                TopUpProduct("ml_514", "514 Diamonds (+58 Bonus)", 120000),
                TopUpProduct("ml_706", "706 Diamonds", 160000),
                TopUpProduct("ml_1050", "1050 Diamonds", 240000),
                TopUpProduct("ml_pass", "Weekly Diamond Pass", 280000, isPopular = true)
            )
        ),
        GameCategory(
            id = "free_fire",
            name = "Garena Free Fire",
            drawableRes = R.drawable.ic_game_freefire,
            fieldsNeeded = listOf(
                GameField("player_id", "Player ID", "Contoh: 987654321")
            ),
            products = listOf(
                TopUpProduct("ff_50", "50 Diamonds", 8000),
                TopUpProduct("ff_70", "70 Diamonds", 10000),
                TopUpProduct("ff_140", "140 Diamonds", 20000),
                TopUpProduct("ff_355", "355 Diamonds", 50000, isPopular = true),
                TopUpProduct("ff_720", "720 Diamonds", 100000),
                TopUpProduct("ff_1450", "1450 Diamonds", 200000),
                TopUpProduct("ff_weekly", "Weekly Membership", 35000)
            )
        ),
        GameCategory(
            id = "pubg_mobile",
            name = "PUBG Mobile",
            drawableRes = R.drawable.ic_game_pubg,
            fieldsNeeded = listOf(
                GameField("player_id", "Player ID", "Contoh: 5123456789")
            ),
            products = listOf(
                TopUpProduct("pubg_60", "60 UC", 15000),
                TopUpProduct("pubg_325", "325 UC", 75000, isPopular = true),
                TopUpProduct("pubg_660", "660 UC", 150000),
                TopUpProduct("pubg_1800", "1800 UC", 380000),
                TopUpProduct("pubg_3850", "3850 UC", 750000),
                TopUpProduct("pubg_rp", "Royale Pass Pack", 140000)
            )
        ),
        GameCategory(
            id = "valorant",
            name = "VALORANT",
            drawableRes = R.drawable.ic_game_valorant,
            fieldsNeeded = listOf(
                GameField("riot_id", "Riot ID", "Contoh: PlayerOne"),
                GameField("tagline", "Tagline", "Contoh: #ID1")
            ),
            products = listOf(
                TopUpProduct("val_475", "475 VP", 50000),
                TopUpProduct("val_1000", "1000 VP", 100000, isPopular = true),
                TopUpProduct("val_2050", "2050 VP", 200000),
                TopUpProduct("val_3650", "3650 VP", 350000),
                TopUpProduct("val_5350", "5350 VP", 500000)
            )
        ),
        GameCategory(
            id = "roblox",
            name = "Roblox",
            drawableRes = R.drawable.ic_game_roblox,
            fieldsNeeded = listOf(
                GameField("username", "Roblox Username", "Contoh: GamerX")
            ),
            products = listOf(
                TopUpProduct("rblx_80", "80 Robux", 16000),
                TopUpProduct("rblx_400", "400 Robux", 75000, isPopular = true),
                TopUpProduct("rblx_800", "800 Robux", 145000),
                TopUpProduct("rblx_1700", "1700 Robux", 300000),
                TopUpProduct("rblx_4500", "4500 Robux", 750000)
            )
        ),
        GameCategory(
            id = "genshin",
            name = "Genshin Impact",
            drawableRes = R.drawable.ic_game_genshin,
            fieldsNeeded = listOf(
                GameField("uid", "UID", "Contoh: 800123456"),
                GameField("server", "Server", "Asia / America / Europe / TW")
            ),
            products = listOf(
                TopUpProduct("genshin_60", "60 Genesis Crystals", 16000),
                TopUpProduct("genshin_300", "300+30 Genesis Crystals", 79000),
                TopUpProduct("genshin_welkin", "Blessing of the Welkin Moon", 79000, isPopular = true),
                TopUpProduct("genshin_980", "980+110 Genesis Crystals", 249000),
                TopUpProduct("genshin_1980", "1980+260 Genesis Crystals", 479000)
            )
        ),
        GameCategory(
            id = "codm",
            name = "Call of Duty: Mobile",
            drawableRes = R.drawable.ic_game_codm,
            fieldsNeeded = listOf(
                GameField("player_id", "Player ID", "Contoh: 678910"),
                GameField("open_id", "OpenID", "Contoh: 123456")
            ),
            products = listOf(
                TopUpProduct("cod_31", "31 CP", 5000),
                TopUpProduct("cod_128", "128 CP", 20000),
                TopUpProduct("cod_321", "321 CP", 50000, isPopular = true),
                TopUpProduct("cod_645", "645 CP", 100000),
                TopUpProduct("cod_bp", "Battle Pass", 60000)
            )
        ),
        GameCategory(
            id = "hok",
            name = "Honor of Kings",
            drawableRes = R.drawable.ic_game_hok,
            fieldsNeeded = listOf(
                GameField("player_id", "Player ID", "Contoh: 10928374")
            ),
            products = listOf(
                TopUpProduct("hok_80", "80 Tokens", 15000),
                TopUpProduct("hok_240", "240 Tokens", 45000),
                TopUpProduct("hok_400", "400 Tokens", 75000, isPopular = true),
                TopUpProduct("hok_800", "800 Tokens", 150000),
                TopUpProduct("hok_weekly", "Weekly Pass Plus", 30000)
            )
        ),
        GameCategory(
            id = "minecraft",
            name = "Minecraft",
            drawableRes = R.drawable.ic_game_minecraft,
            fieldsNeeded = listOf(
                GameField("username", "GamerTag / Username", "Contoh: Steve_Pro")
            ),
            products = listOf(
                TopUpProduct("mc_320", "320 Minecoins", 35000),
                TopUpProduct("mc_840", "840 Minecoins", 85000, isPopular = true),
                TopUpProduct("mc_1720", "1720 Minecoins", 170000),
                TopUpProduct("mc_bedrock", "Minecraft Java & Bedrock Edition", 399000)
            )
        )
    )

    fun getGameById(id: String): GameCategory? {
        return games.find { it.id.equals(id, ignoreCase = true) || it.name.contains(id, ignoreCase = true) }
    }
}
