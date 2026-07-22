package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.*
import com.example.data.repository.JBEpinRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class JBEpinViewModel(application: Application) : AndroidViewModel(application) {

    val repository = JBEpinRepository(application)

    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    val availableAccounts: StateFlow<List<MarketplaceAccountEntity>> = repository.observeAvailableAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAccounts: StateFlow<List<MarketplaceAccountEntity>> = repository.observeAllAccounts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val vouchers: StateFlow<List<VoucherEntity>> = repository.observeVouchers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.observeAllOrders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeposits: StateFlow<List<DepositEntity>> = repository.observeAllDeposits()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userOrders: StateFlow<List<OrderEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.observeUserOrders(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userDeposits: StateFlow<List<DepositEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.observeUserDeposits(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userTransactions: StateFlow<List<TransactionEntity>> = currentUser
        .flatMapLatest { user ->
            if (user != null) repository.observeUserTransactions(user.id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    fun register(username: String, email: String, pass: String, confirmPass: String, onSuccess: () -> Unit) {
        if (username.isBlank() || email.isBlank() || pass.isBlank()) {
            emitUiEvent("Harap isi semua kolom pendaftaran.")
            return
        }
        if (pass != confirmPass) {
            emitUiEvent("Konfirmasi password tidak cocok.")
            return
        }
        viewModelScope.launch {
            val res = repository.register(username, email, pass)
            res.onSuccess {
                emitUiEvent("Registrasi berhasil! Selamat datang, ${it.username}.")
                onSuccess()
            }.onFailure {
                emitUiEvent(it.message ?: "Registrasi gagal.")
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        if (email.isBlank() || pass.isBlank()) {
            emitUiEvent("Harap masukkan email dan password.")
            return
        }
        viewModelScope.launch {
            val res = repository.login(email, pass)
            res.onSuccess {
                emitUiEvent("Login berhasil. Selamat datang ${it.username}.")
                onSuccess()
            }.onFailure {
                emitUiEvent(it.message ?: "Login gagal.")
            }
        }
    }

    fun logout() {
        repository.logout()
        emitUiEvent("Anda telah keluar dari akun.")
    }

    fun submitTopUpOrder(
        game: String,
        gameData: String,
        productName: String,
        price: Long,
        buyerName: String,
        buyerWhatsapp: String,
        paymentMethod: String,
        proofUrl: String?,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.submitTopUpOrder(
                game, gameData, productName, price, buyerName, buyerWhatsapp, paymentMethod, proofUrl
            )
            res.onSuccess { order ->
                emitUiEvent("Pesanan top-up berhasil dibuat! ID: ${order.id}")
                onSuccess(order)
            }.onFailure {
                emitUiEvent(it.message ?: "Gagal memproses top-up.")
            }
        }
    }

    fun submitDeposit(
        amount: Long,
        paymentMethod: String,
        proofUrl: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.submitDeposit(amount, paymentMethod, proofUrl)
            res.onSuccess {
                emitUiEvent("Permintaan deposit berhasil dikirim! Menunggu konfirmasi admin.")
                onSuccess()
            }.onFailure {
                emitUiEvent(it.message ?: "Gagal membuat deposit.")
            }
        }
    }

    fun approveDeposit(depositId: String) {
        viewModelScope.launch {
            repository.approveDeposit(depositId)
            emitUiEvent("Deposit $depositId berhasil disetujui! Saldo pengguna telah bertambah.")
        }
    }

    fun rejectDeposit(depositId: String) {
        viewModelScope.launch {
            repository.rejectDeposit(depositId)
            emitUiEvent("Deposit $depositId telah ditolak.")
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            emitUiEvent("Status pesanan $orderId diubah menjadi $newStatus.")
        }
    }

    fun addOfficialAccountStock(
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
    ) {
        viewModelScope.launch {
            val res = repository.addOfficialStockAccount(
                game, title, price, imagesJson, level, rank, skinsCount, itemsCount, description
            )
            res.onSuccess {
                emitUiEvent("Stok akun resmi JB EPIN berhasil ditambahkan!")
                onSuccess()
            }.onFailure {
                emitUiEvent(it.message ?: "Gagal menambah stok akun.")
            }
        }
    }

    fun submitUserSellerAccount(
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
    ) {
        viewModelScope.launch {
            val res = repository.submitUserSellerAccount(
                game, title, price, imagesJson, level, rank, skinsCount, itemsCount, description
            )
            res.onSuccess {
                emitUiEvent("Listing akun Anda berhasil dikirim! Status: Pending Review.")
                onSuccess()
            }.onFailure {
                emitUiEvent(it.message ?: "Gagal mengirimkan listing akun.")
            }
        }
    }

    fun updateMarketplaceAccountStatus(accountId: String, status: String) {
        viewModelScope.launch {
            repository.updateMarketplaceAccountStatus(accountId, status)
            emitUiEvent("Status akun $accountId diperbarui: $status.")
        }
    }

    fun deleteMarketplaceAccount(accountId: String) {
        viewModelScope.launch {
            repository.deleteMarketplaceAccount(accountId)
            emitUiEvent("Listing akun $accountId telah dihapus.")
        }
    }

    fun buyMarketplaceAccount(
        account: MarketplaceAccountEntity,
        paymentMethod: String,
        proofUrl: String?,
        onSuccess: (OrderEntity) -> Unit
    ) {
        viewModelScope.launch {
            val res = repository.buyMarketplaceAccount(account, paymentMethod, proofUrl)
            res.onSuccess { order ->
                emitUiEvent("Pembelian akun berhasil dikirim! ID Pesanan: ${order.id}")
                onSuccess(order)
            }.onFailure {
                emitUiEvent(it.message ?: "Gagal membeli akun.")
            }
        }
    }

    fun buyVoucher(voucher: VoucherEntity, paymentMethod: String, proofUrl: String?, onSuccess: (OrderEntity) -> Unit) {
        viewModelScope.launch {
            val res = repository.buyVoucher(voucher, paymentMethod, proofUrl)
            res.onSuccess { order ->
                emitUiEvent("Pembelian voucher ${voucher.name} berhasil!")
                onSuccess(order)
            }.onFailure {
                emitUiEvent(it.message ?: "Gagal membeli voucher.")
            }
        }
    }

    private fun emitUiEvent(msg: String) {
        viewModelScope.launch {
            _uiEvent.emit(msg)
        }
    }
}
