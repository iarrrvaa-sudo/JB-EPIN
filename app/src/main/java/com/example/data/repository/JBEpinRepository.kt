package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.entities.*
import com.example.model.GameDataCatalog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class JBEpinRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val orderDao = db.orderDao()
    private val depositDao = db.depositDao()
    private val marketplaceDao = db.marketplaceDao()
    private val voucherDao = db.voucherDao()
    private val transactionDao = db.transactionDao()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            seedDatabaseIfEmpty()
            // Auto login default demo user or admin
            val admin = userDao.getUserByEmail("admin@jbepin.com")
            if (admin != null) {
                _currentUser.value = admin
            }
        }
    }

    private suspend fun seedDatabaseIfEmpty() {
        // Seed Admin user
        var admin = userDao.getUserByEmail("admin@jbepin.com")
        if (admin == null) {
            admin = UserEntity(
                id = "usr_admin_001",
                username = "JB EPIN Admin",
                email = "admin@jbepin.com",
                avatarUrl = "",
                balance = 5000000L,
                role = "admin",
                passwordHash = "admin123"
            )
            userDao.insertUser(admin)
        }

        // Seed Sample User
        var sampleUser = userDao.getUserByEmail("user@jbepin.com")
        if (sampleUser == null) {
            sampleUser = UserEntity(
                id = "usr_demo_101",
                username = "ProGamer99",
                email = "user@jbepin.com",
                avatarUrl = "",
                balance = 250000L,
                role = "user",
                passwordHash = "user123"
            )
            userDao.insertUser(sampleUser)
        }

        // Seed Initial JB EPIN Official Stock Accounts
        val existingStock = marketplaceDao.getAccountById("ACC-OFFICIAL-101")
        if (existingStock == null) {
            val officialStock = listOf(
                MarketplaceAccountEntity(
                    id = "ACC-OFFICIAL-101",
                    sellerId = admin.id,
                    sellerType = "official",
                    sellerName = "JB EPIN",
                    game = "Mobile Legends: Bang Bang",
                    title = "Akun Sultan MLBB Full Skin Epic & Collector",
                    price = 850000L,
                    imagesJson = "ic_game_mlbb",
                    level = 120,
                    rank = "Mythic Immortal 150★",
                    skinsCount = 380,
                    itemsCount = 45,
                    description = "Akun official terjamin 100% aman anti hackback! Full skin Collector, Legend, and Prime. Emblem All Max, Winrate 68%+",
                    status = "Available"
                ),
                MarketplaceAccountEntity(
                    id = "ACC-OFFICIAL-102",
                    sellerId = admin.id,
                    sellerType = "official",
                    sellerName = "JB EPIN",
                    game = "Garena Free Fire",
                    title = "Akun Master FF Season Old Full Bandel & Shotgun 2",
                    price = 450000L,
                    imagesJson = "ic_game_freefire",
                    level = 78,
                    rank = "Grandmaster",
                    skinsCount = 210,
                    itemsCount = 30,
                    description = "Akun langka S1-S5 Old! Senjata M1887 Incubator, Bundle Cobra, Katana Glow. Garansi seumur hidup dari JB EPIN.",
                    status = "Available"
                ),
                MarketplaceAccountEntity(
                    id = "ACC-OFFICIAL-103",
                    sellerId = admin.id,
                    sellerType = "official",
                    sellerName = "JB EPIN",
                    game = "PUBG Mobile",
                    title = "Akun PUBG Glacier Level Max & M416 Fool",
                    price = 1250000L,
                    imagesJson = "ic_game_pubg",
                    level = 85,
                    rank = "Conqueror",
                    skinsCount = 190,
                    itemsCount = 25,
                    description = "PUBG Mobile Sultan Account: M416 Glacier Max Level (Hit Effect), X-Suit Poseidon, Lamborghini Skin Car.",
                    status = "Available"
                ),
                MarketplaceAccountEntity(
                    id = "ACC-OFFICIAL-104",
                    sellerId = admin.id,
                    sellerType = "official",
                    sellerName = "JB EPIN",
                    game = "VALORANT",
                    title = "Valorant Radiant Account Vandal Kuronami & Prime",
                    price = 950000L,
                    imagesJson = "ic_game_valorant",
                    level = 145,
                    rank = "Radiant 450 RR",
                    skinsCount = 85,
                    itemsCount = 12,
                    description = "Valorant Official Stock: Vandal Kuronami, Phantom Reaver, Karambit Champions, Operator Elderflame. First Hand Email Passed.",
                    status = "Available"
                ),
                MarketplaceAccountEntity(
                    id = "ACC-OFFICIAL-105",
                    sellerId = admin.id,
                    sellerType = "official",
                    sellerName = "JB EPIN",
                    game = "Genshin Impact",
                    title = "Genshin AR 60 All Archon C6 & Signature Weapons",
                    price = 1500000L,
                    imagesJson = "ic_game_genshin",
                    level = 60,
                    rank = "Adventure Rank 60",
                    skinsCount = 42,
                    itemsCount = 150,
                    description = "Sultan Genshin Impact: Raiden C6R5, Furina C6, Neuvillette C6. Exploration 100% all maps. Ready for Fontaine/Natlan.",
                    status = "Available"
                )
            )
            officialStock.forEach { marketplaceDao.insertAccount(it) }
        }

        // Seed Vouchers
        val vouchers = listOf(
            VoucherEntity("VOUCH-GP-50", "Google Play Voucher Rp50.000", "Google Play", "Rp 50,000", 52000L, 50),
            VoucherEntity("VOUCH-GP-100", "Google Play Voucher Rp100.000", "Google Play", "Rp 100,000", 103000L, 30),
            VoucherEntity("VOUCH-STEAM-60", "Steam Wallet Code IDR 60,000", "Steam", "Rp 60,000", 63000L, 40),
            VoucherEntity("VOUCH-STEAM-120", "Steam Wallet Code IDR 120,000", "Steam", "Rp 120,000", 124000L, 25),
            VoucherEntity("VOUCH-GARENA-330", "Garena Shells 330 Shells", "Game Vouchers", "330 Shells", 100000L, 100),
            VoucherEntity("VOUCH-PSN-100", "PlayStation Network Gift Card", "Other Digital Vouchers", "Rp 100,000", 105000L, 20)
        )
        vouchers.forEach { voucherDao.insertVoucher(it) }
    }

    // --- AUTHENTICATION ---
    suspend fun register(username: String, email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val existing = userDao.getUserByEmail(email)
        if (existing != null) {
            return@withContext Result.failure(Exception("Email sudah terdaftar. Silakan login."))
        }
        val newUser = UserEntity(
            id = "usr_" + UUID.randomUUID().toString().take(8),
            username = username,
            email = email,
            passwordHash = password,
            role = "user",
            balance = 0L
        )
        userDao.insertUser(newUser)
        _currentUser.value = newUser
        Result.success(newUser)
    }

    suspend fun login(email: String, password: String): Result<UserEntity> = withContext(Dispatchers.IO) {
        val user = userDao.getUserByEmail(email)
        if (user == null || user.passwordHash != password) {
            return@withContext Result.failure(Exception("Email atau password salah."))
        }
        _currentUser.value = user
        Result.success(user)
    }

    fun logout() {
        _currentUser.value = null
    }

    suspend fun refreshCurrentUser() = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val updated = userDao.getUserById(current.id)
        if (updated != null) {
            _currentUser.value = updated
        }
    }

    // --- ORDERS & TOP-UP ---
    fun observeUserOrders(userId: String): Flow<List<OrderEntity>> = orderDao.getOrdersByUserId(userId)
    fun observeAllOrders(): Flow<List<OrderEntity>> = orderDao.getAllOrders()

    suspend fun submitTopUpOrder(
        game: String,
        gameData: String,
        productName: String,
        price: Long,
        buyerName: String,
        buyerWhatsapp: String,
        paymentMethod: String,
        proofUrl: String?
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Harap login terlebih dahulu."))
        val orderId = "ORD-" + System.currentTimeMillis().toString().takeLast(6) + (10..99).random()

        if (paymentMethod == "Saldo Account") {
            if (user.balance < price) {
                return@withContext Result.failure(Exception("Saldo akun tidak mencukupi (Rp${user.balance}). Harap deposit terlebih dahulu."))
            }
            // Deduct balance
            userDao.addBalance(user.id, -price)
            refreshCurrentUser()
        }

        val order = OrderEntity(
            id = orderId,
            userId = user.id,
            orderType = "topup",
            game = game,
            gameData = gameData,
            product = productName,
            price = price,
            buyerName = buyerName,
            buyerWhatsapp = buyerWhatsapp,
            paymentMethod = paymentMethod,
            paymentProofUrl = proofUrl,
            status = if (paymentMethod == "Saldo Account") "Processing" else "Waiting for Verification"
        )
        orderDao.insertOrder(order)

        // Record transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                id = "TX-" + UUID.randomUUID().toString().take(8),
                userId = user.id,
                type = "Top-Up $game",
                amount = price,
                status = order.status,
                relatedOrderId = order.id
            )
        )

        Result.success(order)
    }

    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        orderDao.updateOrderStatus(orderId, status)
    }

    // --- DEPOSITS ---
    fun observeUserDeposits(userId: String): Flow<List<DepositEntity>> = depositDao.getDepositsByUserId(userId)
    fun observeAllDeposits(): Flow<List<DepositEntity>> = depositDao.getAllDeposits()

    suspend fun submitDeposit(
        amount: Long,
        paymentMethod: String,
        proofUrl: String?
    ): Result<DepositEntity> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Harap login terlebih dahulu."))
        val depId = "DEP-" + System.currentTimeMillis().toString().takeLast(5) + (10..99).random()

        val deposit = DepositEntity(
            id = depId,
            userId = user.id,
            userName = user.username,
            amount = amount,
            paymentMethod = paymentMethod,
            proofUrl = proofUrl,
            status = "Waiting for Verification"
        )
        depositDao.insertDeposit(deposit)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = "TX-DEP-" + UUID.randomUUID().toString().take(6),
                userId = user.id,
                type = "Deposit Saldo",
                amount = amount,
                status = "Waiting for Verification",
                relatedOrderId = depId
            )
        )

        Result.success(deposit)
    }

    suspend fun approveDeposit(depositId: String) = withContext(Dispatchers.IO) {
        val deposit = depositDao.getDepositById(depositId) ?: return@withContext
        if (deposit.status == "Approved") return@withContext

        depositDao.updateDepositStatus(depositId, "Approved")
        // Increase user balance
        userDao.addBalance(deposit.userId, deposit.amount)

        // Record transaction
        transactionDao.insertTransaction(
            TransactionEntity(
                id = "TX-APP-" + UUID.randomUUID().toString().take(6),
                userId = deposit.userId,
                type = "Deposit Approved",
                amount = deposit.amount,
                status = "Completed",
                relatedOrderId = depositId
            )
        )
        refreshCurrentUser()
    }

    suspend fun rejectDeposit(depositId: String) = withContext(Dispatchers.IO) {
        depositDao.updateDepositStatus(depositId, "Rejected")
    }

    // --- GAME ACCOUNT MARKETPLACE & JB EPIN OFFICIAL STOCK ---
    fun observeAvailableAccounts(): Flow<List<MarketplaceAccountEntity>> = marketplaceDao.getAvailableAccounts()
    fun observeAllAccounts(): Flow<List<MarketplaceAccountEntity>> = marketplaceDao.getAllAccounts()

    suspend fun getAccountById(id: String): MarketplaceAccountEntity? = withContext(Dispatchers.IO) {
        marketplaceDao.getAccountById(id)
    }

    suspend fun addOfficialStockAccount(
        game: String,
        title: String,
        price: Long,
        imagesJson: String,
        level: Int,
        rank: String,
        skinsCount: Int,
        itemsCount: Int,
        description: String
    ): Result<MarketplaceAccountEntity> = withContext(Dispatchers.IO) {
        val admin = _currentUser.value ?: return@withContext Result.failure(Exception("Hanya Admin yang dapat menambah stok official."))
        if (admin.role != "admin") {
            return@withContext Result.failure(Exception("Akses ditolak. Peran Anda bukan admin."))
        }

        val newAccount = MarketplaceAccountEntity(
            id = "ACC-OFFICIAL-" + System.currentTimeMillis().toString().takeLast(6),
            sellerId = admin.id,
            sellerType = "official",
            sellerName = "JB EPIN",
            game = game,
            title = title,
            price = price,
            imagesJson = imagesJson,
            level = level,
            rank = rank,
            skinsCount = skinsCount,
            itemsCount = itemsCount,
            description = description,
            status = "Available"
        )
        marketplaceDao.insertAccount(newAccount)
        Result.success(newAccount)
    }

    suspend fun submitUserSellerAccount(
        game: String,
        title: String,
        price: Long,
        imagesJson: String,
        level: Int,
        rank: String,
        skinsCount: Int,
        itemsCount: Int,
        description: String
    ): Result<MarketplaceAccountEntity> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Harap login untuk menjual akun."))

        val newAccount = MarketplaceAccountEntity(
            id = "ACC-USER-" + System.currentTimeMillis().toString().takeLast(6),
            sellerId = user.id,
            sellerType = "user",
            sellerName = user.username,
            game = game,
            title = title,
            price = price,
            imagesJson = imagesJson,
            level = level,
            rank = rank,
            skinsCount = skinsCount,
            itemsCount = itemsCount,
            description = description,
            status = "Pending Review" // Needs admin approval!
        )
        marketplaceDao.insertAccount(newAccount)
        Result.success(newAccount)
    }

    suspend fun updateMarketplaceAccountStatus(accountId: String, status: String) = withContext(Dispatchers.IO) {
        marketplaceDao.updateAccountStatus(accountId, status)
    }

    suspend fun deleteMarketplaceAccount(accountId: String) = withContext(Dispatchers.IO) {
        marketplaceDao.deleteAccount(accountId)
    }

    suspend fun buyMarketplaceAccount(
        account: MarketplaceAccountEntity,
        paymentMethod: String,
        proofUrl: String?
    ): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Harap login terlebih dahulu."))
        val orderId = "ORD-ACC-" + System.currentTimeMillis().toString().takeLast(6)

        if (paymentMethod == "Saldo Account") {
            if (user.balance < account.price) {
                return@withContext Result.failure(Exception("Saldo akun tidak mencukupi."))
            }
            userDao.addBalance(user.id, -account.price)
            refreshCurrentUser()
        }

        // Reserve or mark sold
        marketplaceDao.updateAccountStatus(account.id, "Reserved")

        val order = OrderEntity(
            id = orderId,
            userId = user.id,
            orderType = "account_purchase",
            game = account.game,
            gameData = "Account Listing ID: ${account.id} (${account.title})",
            product = account.title,
            price = account.price,
            buyerName = user.username,
            buyerWhatsapp = "081234567890",
            paymentMethod = paymentMethod,
            paymentProofUrl = proofUrl,
            status = if (paymentMethod == "Saldo Account") "Processing" else "Waiting for Verification"
        )
        orderDao.insertOrder(order)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = "TX-ACC-" + UUID.randomUUID().toString().take(6),
                userId = user.id,
                type = "Beli Akun ${account.game}",
                amount = account.price,
                status = order.status,
                relatedOrderId = order.id
            )
        )

        Result.success(order)
    }

    // --- VOUCHERS ---
    fun observeVouchers(): Flow<List<VoucherEntity>> = voucherDao.getAllVouchers()

    suspend fun buyVoucher(voucher: VoucherEntity, paymentMethod: String, proofUrl: String?): Result<OrderEntity> = withContext(Dispatchers.IO) {
        val user = _currentUser.value ?: return@withContext Result.failure(Exception("Harap login terlebih dahulu."))
        val orderId = "ORD-VCH-" + System.currentTimeMillis().toString().takeLast(6)

        if (paymentMethod == "Saldo Account") {
            if (user.balance < voucher.price) {
                return@withContext Result.failure(Exception("Saldo akun tidak cukup."))
            }
            userDao.addBalance(user.id, -voucher.price)
            refreshCurrentUser()
        }

        val order = OrderEntity(
            id = orderId,
            userId = user.id,
            orderType = "voucher",
            game = voucher.category,
            gameData = "Voucher: ${voucher.name}",
            product = voucher.name,
            price = voucher.price,
            buyerName = user.username,
            buyerWhatsapp = "081234567890",
            paymentMethod = paymentMethod,
            paymentProofUrl = proofUrl,
            status = if (paymentMethod == "Saldo Account") "Completed" else "Waiting for Verification"
        )
        orderDao.insertOrder(order)

        transactionDao.insertTransaction(
            TransactionEntity(
                id = "TX-VCH-" + UUID.randomUUID().toString().take(6),
                userId = user.id,
                type = "Voucher ${voucher.name}",
                amount = voucher.price,
                status = order.status,
                relatedOrderId = order.id
            )
        )

        Result.success(order)
    }

    // --- TRANSACTIONS ---
    fun observeUserTransactions(userId: String): Flow<List<TransactionEntity>> = transactionDao.getTransactionsByUserId(userId)
}
