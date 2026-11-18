package com.mdesigns.khata.domain.model

import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.entity.TransactionEntity

data class PersonSummary(
    val person: LedgerPersonEntity,
    val totalDebit: Double,
    val totalCredit: Double,
    val netBalance: Double,
    val status: KhataBalanceCalculator.BalanceStatus,
    val lastTransactionDate: Long? = null
)

data class LedgerEntry(
    val transaction: TransactionEntity,
    val runningBalance: Double
)

data class DashboardSummary(
    val totalReceivable: Double = 0.0, // You Collect
    val totalPayable: Double = 0.0,    // You Give
    val netBalance: Double = 0.0,
    val todayIncome: Double = 0.0,
    val todayExpense: Double = 0.0,
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val customerCount: Int = 0,
    val supplierCount: Int = 0,
    val settledCount: Int = 0,
    val pendingCount: Int = 0
)

enum class PersonFilter {
    ALL,
    RECEIVABLE, // You Collect
    PAYABLE,    // You Give
    SETTLED,    // Zero Balance
    CUSTOMERS,
    SUPPLIERS
}
