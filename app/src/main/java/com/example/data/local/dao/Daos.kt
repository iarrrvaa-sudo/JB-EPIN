package com.example.data.local.dao

import androidx.room.*
import com.example.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("UPDATE users SET balance = balance + :amount WHERE id = :userId")
    suspend fun addBalance(userId: String, amount: Long)

    @Query("UPDATE users SET balance = :newBalance WHERE id = :userId")
    suspend fun setBalance(userId: String, newBalance: Long)
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUserId(userId: String): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)
}

@Dao
interface DepositDao {
    @Query("SELECT * FROM deposits ORDER BY createdAt DESC")
    fun getAllDeposits(): Flow<List<DepositEntity>>

    @Query("SELECT * FROM deposits WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDepositsByUserId(userId: String): Flow<List<DepositEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: DepositEntity)

    @Query("UPDATE deposits SET status = :status WHERE id = :depositId")
    suspend fun updateDepositStatus(depositId: String, status: String)

    @Query("SELECT * FROM deposits WHERE id = :depositId LIMIT 1")
    suspend fun getDepositById(depositId: String): DepositEntity?
}

@Dao
interface MarketplaceDao {
    @Query("SELECT * FROM marketplace_accounts ORDER BY createdAt DESC")
    fun getAllAccounts(): Flow<List<MarketplaceAccountEntity>>

    @Query("SELECT * FROM marketplace_accounts WHERE status = 'Available' ORDER BY createdAt DESC")
    fun getAvailableAccounts(): Flow<List<MarketplaceAccountEntity>>

    @Query("SELECT * FROM marketplace_accounts WHERE id = :id LIMIT 1")
    suspend fun getAccountById(id: String): MarketplaceAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: MarketplaceAccountEntity)

    @Query("UPDATE marketplace_accounts SET status = :status WHERE id = :id")
    suspend fun updateAccountStatus(id: String, status: String)

    @Query("DELETE FROM marketplace_accounts WHERE id = :id")
    suspend fun deleteAccount(id: String)
}

@Dao
interface VoucherDao {
    @Query("SELECT * FROM vouchers ORDER BY name ASC")
    fun getAllVouchers(): Flow<List<VoucherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVoucher(voucher: VoucherEntity)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY createdAt DESC")
    fun getTransactionsByUserId(userId: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY createdAt DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)
}
