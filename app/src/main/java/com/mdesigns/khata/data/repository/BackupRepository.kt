package com.mdesigns.khata.data.repository

import android.content.Context
import android.net.Uri
import com.mdesigns.khata.data.dao.*
import com.mdesigns.khata.data.entity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Singleton

data class BackupImportResult(
    val success: Boolean,
    val message: String,
    val peopleCount: Int = 0,
    val transactionsCount: Int = 0,
    val moneyCount: Int = 0
)

@Singleton
class BackupRepository @Inject constructor(
    private val backupDao: BackupRecordDao,
    private val profileDao: BusinessProfileDao,
    private val personDao: LedgerPersonDao,
    private val transactionDao: TransactionDao,
    private val moneyTransactionDao: MoneyTransactionDao,
    private val categoryDao: CategoryDao
) {
    fun getBackupRecords(profileId: Long): Flow<List<BackupRecordEntity>> =
        backupDao.getBackupRecords(profileId)

    suspend fun insertBackupRecord(record: BackupRecordEntity): Long =
        backupDao.insertBackupRecord(record)

    suspend fun exportBackup(context: Context, profileId: Long): File = withContext(Dispatchers.IO) {
        val profile = profileDao.getProfileById(profileId) ?: profileDao.getDefaultProfile()
        val people = personDao.getAllPeopleList(profileId)
        val transactions = transactionDao.getAllTransactionsList(profileId)
        val moneyTransactions = moneyTransactionDao.getAllMoneyTransactionsList(profileId)
        val categories = categoryDao.getAllCategoriesList(profileId)

        val rootJson = JSONObject().apply {
            put("format", "M_DESIGNS_KHATA_BACKUP")
            put("version", 1)
            put("appName", "M Designs Khata Book")
            put("exportTimestamp", System.currentTimeMillis())

            // Profile
            if (profile != null) {
                put("profile", JSONObject().apply {
                    put("id", profile.id)
                    put("name", profile.name)
                    put("ownerName", profile.ownerName)
                    put("mobile", profile.mobile)
                    put("alternateMobile", profile.alternateMobile)
                    put("address", profile.address)
                    put("city", profile.city)
                    put("state", profile.state)
                    put("pincode", profile.pincode)
                    put("email", profile.email)
                    put("website", profile.website)
                    put("gstin", profile.gstin)
                    put("isDefault", profile.isDefault)
                })
            }

            // People
            val peopleArray = JSONArray()
            for (p in people) {
                peopleArray.put(JSONObject().apply {
                    put("id", p.id)
                    put("profileId", p.profileId)
                    put("name", p.name)
                    put("mobile", p.mobile)
                    put("category", p.category)
                    put("openingBalance", p.openingBalance)
                    put("isOpeningBalanceDebit", p.isOpeningBalanceDebit)
                    put("address", p.address)
                    put("notes", p.notes)
                    put("isSettled", p.isSettled)
                    put("isDeleted", p.isDeleted)
                    put("createdAt", p.createdAt)
                    put("updatedAt", p.updatedAt)
                })
            }
            put("people", peopleArray)

            // Transactions
            val txArray = JSONArray()
            for (tx in transactions) {
                txArray.put(JSONObject().apply {
                    put("id", tx.id)
                    put("profileId", tx.profileId)
                    put("personId", tx.personId)
                    put("type", tx.type)
                    put("amount", tx.amount)
                    put("description", tx.description)
                    put("categoryId", tx.categoryId)
                    put("paymentMode", tx.paymentMode)
                    put("date", tx.date)
                    put("dueDate", tx.dueDate)
                    put("attachmentUri", tx.attachmentUri)
                    put("referenceNumber", tx.referenceNumber)
                    put("isDeleted", tx.isDeleted)
                    put("createdAt", tx.createdAt)
                    put("updatedAt", tx.updatedAt)
                })
            }
            put("transactions", txArray)

            // Money Transactions
            val moneyArray = JSONArray()
            for (m in moneyTransactions) {
                moneyArray.put(JSONObject().apply {
                    put("id", m.id)
                    put("profileId", m.profileId)
                    put("type", m.type)
                    put("amount", m.amount)
                    put("category", m.category)
                    put("description", m.description)
                    put("paymentMode", m.paymentMode)
                    put("date", m.date)
                    put("attachmentUri", m.attachmentUri)
                    put("isDeleted", m.isDeleted)
                    put("createdAt", m.createdAt)
                    put("updatedAt", m.updatedAt)
                })
            }
            put("moneyTransactions", moneyArray)

            // Categories
            val catArray = JSONArray()
            for (c in categories) {
                catArray.put(JSONObject().apply {
                    put("id", c.id)
                    put("profileId", c.profileId)
                    put("name", c.name)
                    put("type", c.type)
                    put("icon", c.icon)
                    put("sortOrder", c.sortOrder)
                    put("isActive", c.isActive)
                })
            }
            put("categories", catArray)
        }

        val outputDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val backupFile = File(outputDir, "M_Designs_Khata_Backup_${System.currentTimeMillis()}.json")
        val writer = FileWriter(backupFile)
        writer.write(rootJson.toString(2))
        writer.flush()
        writer.close()

        // Record history
        backupDao.insertBackupRecord(
            BackupRecordEntity(
                profileId = profileId,
                driveFileId = "LOCAL_EXPORT",
                backupDate = System.currentTimeMillis(),
                status = "SUCCESS",
                checksum = "${people.size} parties, ${transactions.size} records",
                version = 1
            )
        )

        backupFile
    }

    suspend fun importBackup(context: Context, uri: Uri, targetProfileId: Long): BackupImportResult = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext BackupImportResult(false, "Could not open selected file")
            val content = inputStream.bufferedReader().use { it.readText() }
            val rootJson = JSONObject(content)

            if (!rootJson.has("format") || rootJson.getString("format") != "M_DESIGNS_KHATA_BACKUP") {
                return@withContext BackupImportResult(false, "Invalid file format. Please select a valid M Designs Khata backup file.")
            }

            // Restore Profile if present
            if (rootJson.has("profile")) {
                val pJson = rootJson.getJSONObject("profile")
                val existing = profileDao.getProfileById(targetProfileId)
                if (existing != null) {
                    profileDao.updateProfile(
                        existing.copy(
                            name = pJson.optString("name", existing.name),
                            ownerName = pJson.optString("ownerName", existing.ownerName),
                            mobile = pJson.optString("mobile", existing.mobile),
                            email = pJson.optString("email", existing.email),
                            gstin = pJson.optString("gstin", existing.gstin),
                            address = pJson.optString("address", existing.address),
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            }

            // Restore Categories
            if (rootJson.has("categories")) {
                val catArray = rootJson.getJSONArray("categories")
                for (i in 0 until catArray.length()) {
                    val c = catArray.getJSONObject(i)
                    categoryDao.insertCategory(
                        CategoryEntity(
                            id = c.optLong("id", 0),
                            profileId = targetProfileId,
                            name = c.optString("name", "General"),
                            type = c.optString("type", "EXPENSE"),
                            icon = c.optString("icon", "default"),
                            sortOrder = c.optInt("sortOrder", 0),
                            isActive = c.optBoolean("isActive", true)
                        )
                    )
                }
            }

            // Restore People
            var peopleRestored = 0
            if (rootJson.has("people")) {
                val peopleArray = rootJson.getJSONArray("people")
                for (i in 0 until peopleArray.length()) {
                    val p = peopleArray.getJSONObject(i)
                    personDao.insertPerson(
                        LedgerPersonEntity(
                            id = p.optLong("id", 0),
                            profileId = targetProfileId,
                            name = p.optString("name", "Unknown"),
                            mobile = p.optString("mobile", ""),
                            category = p.optString("category", "Customer"),
                            openingBalance = p.optDouble("openingBalance", 0.0),
                            isOpeningBalanceDebit = p.optBoolean("isOpeningBalanceDebit", true),
                            address = p.optString("address", ""),
                            notes = p.optString("notes", ""),
                            isSettled = p.optBoolean("isSettled", false),
                            isDeleted = p.optBoolean("isDeleted", false),
                            createdAt = p.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = p.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                    peopleRestored++
                }
            }

            // Restore Transactions
            var txRestored = 0
            if (rootJson.has("transactions")) {
                val txArray = rootJson.getJSONArray("transactions")
                for (i in 0 until txArray.length()) {
                    val tx = txArray.getJSONObject(i)
                    transactionDao.insertTransaction(
                        TransactionEntity(
                            id = tx.optLong("id", 0),
                            profileId = targetProfileId,
                            personId = tx.optLong("personId", 0),
                            type = tx.optString("type", "DEBIT"),
                            amount = tx.optDouble("amount", 0.0),
                            description = tx.optString("description", ""),
                            categoryId = if (tx.has("categoryId") && !tx.isNull("categoryId")) tx.getLong("categoryId") else null,
                            paymentMode = tx.optString("paymentMode", "Cash"),
                            date = tx.optLong("date", System.currentTimeMillis()),
                            dueDate = if (tx.has("dueDate") && !tx.isNull("dueDate")) tx.getLong("dueDate") else null,
                            attachmentUri = if (tx.has("attachmentUri") && !tx.isNull("attachmentUri")) tx.getString("attachmentUri") else null,
                            referenceNumber = tx.optString("referenceNumber", ""),
                            isDeleted = tx.optBoolean("isDeleted", false),
                            createdAt = tx.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = tx.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                    txRestored++
                }
            }

            // Restore Money Transactions
            var moneyRestored = 0
            if (rootJson.has("moneyTransactions")) {
                val moneyArray = rootJson.getJSONArray("moneyTransactions")
                for (i in 0 until moneyArray.length()) {
                    val m = moneyArray.getJSONObject(i)
                    moneyTransactionDao.insertMoneyTransaction(
                        MoneyTransactionEntity(
                            id = m.optLong("id", 0),
                            profileId = targetProfileId,
                            type = m.optString("type", "INCOME"),
                            amount = m.optDouble("amount", 0.0),
                            category = m.optString("category", "General"),
                            description = m.optString("description", ""),
                            paymentMode = m.optString("paymentMode", "Cash"),
                            date = m.optLong("date", System.currentTimeMillis()),
                            attachmentUri = if (m.has("attachmentUri") && !m.isNull("attachmentUri")) m.getString("attachmentUri") else null,
                            isDeleted = m.optBoolean("isDeleted", false),
                            createdAt = m.optLong("createdAt", System.currentTimeMillis()),
                            updatedAt = m.optLong("updatedAt", System.currentTimeMillis())
                        )
                    )
                    moneyRestored++
                }
            }

            // Record history
            backupDao.insertBackupRecord(
                BackupRecordEntity(
                    profileId = targetProfileId,
                    driveFileId = "LOCAL_IMPORT",
                    backupDate = System.currentTimeMillis(),
                    status = "RESTORED",
                    checksum = "$peopleRestored parties, $txRestored records",
                    version = 1
                )
            )

            BackupImportResult(
                success = true,
                message = "Database successfully restored: $peopleRestored parties and $txRestored transaction entries.",
                peopleCount = peopleRestored,
                transactionsCount = txRestored,
                moneyCount = moneyRestored
            )
        } catch (e: Exception) {
            e.printStackTrace()
            BackupImportResult(false, "Failed to import backup: ${e.localizedMessage ?: "Unknown error"}")
        }
    }
}
