package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val email: String,
    val avatarUrl: String = "",
    val balance: Long = 0L,
    val role: String = "user", // "user" or "admin"
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val orderType: String, // "topup", "voucher", "account_purchase"
    val game: String,
    val gameData: String,
    val product: String,
    val price: Long,
    val buyerName: String,
    val buyerWhatsapp: String,
    val paymentMethod: String,
    val paymentProofUrl: String? = null,
    val status: String = "Waiting for Verification", // "Waiting for Verification", "Processing", "Completed", "Rejected"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "deposits")
data class DepositEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val amount: Long,
    val paymentMethod: String,
    val proofUrl: String? = null,
    val status: String = "Waiting for Verification", // "Waiting for Verification", "Approved", "Rejected"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "marketplace_accounts")
data class MarketplaceAccountEntity(
    @PrimaryKey val id: String,
    val sellerId: String,
    val sellerType: String, // "official" or "user"
    val sellerName: String,
    val game: String,
    val title: String,
    val price: Long,
    val imagesJson: String, // Pipe/comma separated or JSON
    val level: Int,
    val rank: String,
    val skinsCount: Int,
    val itemsCount: Int,
    val description: String,
    val status: String = "Available", // "Available", "Pending Review", "Reserved", "Sold", "Hidden"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "vouchers")
data class VoucherEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // "Game Vouchers", "Google Play", "Steam", "Other Digital Vouchers"
    val amount: String,
    val price: Long,
    val stock: Int = 100,
    val status: String = "Available"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val amount: Long,
    val status: String,
    val relatedOrderId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
