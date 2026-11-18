package com.mdesigns.khata.feature.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.excel.ExcelReportGenerator
import com.mdesigns.khata.core.pdf.PdfReportGenerator
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.data.entity.BusinessProfileEntity
import com.mdesigns.khata.data.entity.TransactionEntity
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.data.repository.ProfileRepository
import com.mdesigns.khata.domain.model.PersonSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val khataRepository: KhataRepository,
    private val profileRepository: ProfileRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _activeProfile = MutableStateFlow<BusinessProfileEntity?>(null)
    val activeProfile: StateFlow<BusinessProfileEntity?> = _activeProfile.asStateFlow()

    private val _allTransactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val allTransactions: StateFlow<List<TransactionEntity>> = _allTransactions.asStateFlow()

    private val _peopleSummaries = MutableStateFlow<List<PersonSummary>>(emptyList())
    val peopleSummaries: StateFlow<List<PersonSummary>> = _peopleSummaries.asStateFlow()

    private val _isExporting = MutableStateFlow(false)
    val isExporting: StateFlow<Boolean> = _isExporting.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                _activeProfile.value = profileRepository.getProfileById(profileId) ?: profileRepository.getDefaultProfile()

                launch {
                    khataRepository.getAllTransactions(profileId).collectLatest { txs ->
                        _allTransactions.value = txs
                    }
                }

                launch {
                    khataRepository.getPeopleSummaries(profileId).collectLatest { summaries ->
                        _peopleSummaries.value = summaries
                    }
                }
            }
        }
    }

    fun exportFullPdfReport(context: Context) {
        viewModelScope.launch {
            _isExporting.value = true
            _statusMessage.value = null
            try {
                val profile = _activeProfile.value
                val txs = _allTransactions.value
                val summaries = _peopleSummaries.value
                val personMap = summaries.associate { it.person.id to it.person.name }

                val reportItems = txs.map { tx ->
                    val partyName = personMap[tx.personId] ?: "Party #${tx.personId}"
                    val desc = if (tx.description.isNotBlank()) "${tx.description} ($partyName)" else partyName
                    PdfReportGenerator.ReportItem(
                        date = tx.date,
                        description = desc,
                        debit = if (tx.type == "DEBIT") tx.amount else 0.0,
                        credit = if (tx.type == "CREDIT") tx.amount else 0.0,
                        runningBalance = tx.amount
                    )
                }

                val totalDebit = txs.filter { it.type == "DEBIT" }.sumOf { it.amount }
                val totalCredit = txs.filter { it.type == "CREDIT" }.sumOf { it.amount }

                val pdfData = PdfReportGenerator.ReportData(
                    businessName = profile?.name ?: "M Designs Khata",
                    businessPhone = profile?.mobile ?: "",
                    personName = "All Ledger Accounts",
                    personPhone = "",
                    openingBalance = 0.0,
                    isOpeningDebit = true,
                    items = reportItems,
                    totalDebit = totalDebit,
                    totalCredit = totalCredit,
                    closingBalance = totalDebit - totalCredit
                )

                val file = PdfReportGenerator.generatePersonStatement(context, pdfData)
                ShareManager.shareFile(context, file, "application/pdf", "Share Full Statement PDF")
                _statusMessage.value = "PDF statement generated successfully!"
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Failed to export PDF: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun exportExcelCsvReport(context: Context) {
        viewModelScope.launch {
            _isExporting.value = true
            _statusMessage.value = null
            try {
                val txs = _allTransactions.value
                val summaries = _peopleSummaries.value
                val personMap = summaries.associate { it.person.id to it.person.name }

                val rows = txs.map { tx ->
                    val partyName = personMap[tx.personId] ?: "Party #${tx.personId}"
                    ExcelReportGenerator.ExportRow(
                        date = tx.date,
                        personName = partyName,
                        description = tx.description,
                        type = tx.type,
                        amount = tx.amount,
                        paymentMode = tx.paymentMode,
                        balance = tx.amount
                    )
                }

                val file = ExcelReportGenerator.exportToCsv(context, "M_Designs_Khata_Export_${System.currentTimeMillis()}", rows)
                ShareManager.shareFile(context, file, "text/csv", "Share Excel / CSV Export")
                _statusMessage.value = "Spreadsheet exported successfully!"
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Failed to export CSV: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isExporting.value = false
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
