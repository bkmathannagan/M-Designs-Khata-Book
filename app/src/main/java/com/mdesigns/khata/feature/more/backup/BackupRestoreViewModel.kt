package com.mdesigns.khata.feature.more.backup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.sharing.ShareManager
import com.mdesigns.khata.data.entity.BackupRecordEntity
import com.mdesigns.khata.data.repository.BackupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val backupRepository: BackupRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _backupRecords = MutableStateFlow<List<BackupRecordEntity>>(emptyList())
    val backupRecords: StateFlow<List<BackupRecordEntity>> = _backupRecords.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private var currentProfileId: Long = 1L

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                currentProfileId = profileId
                backupRepository.getBackupRecords(profileId).collectLatest { records ->
                    _backupRecords.value = records
                }
            }
        }
    }

    fun createAndShareBackup(context: Context) {
        viewModelScope.launch {
            _isProcessing.value = true
            _statusMessage.value = null
            try {
                val backupFile = backupRepository.exportBackup(context, currentProfileId)
                ShareManager.shareFile(
                    context = context,
                    file = backupFile,
                    mimeType = "application/json",
                    title = "Share M Designs Khata Backup"
                )
                _statusMessage.value = "Backup file created successfully: ${backupFile.name}"
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Backup failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun restoreBackupFromFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            _isProcessing.value = true
            _statusMessage.value = null
            try {
                val result = backupRepository.importBackup(context, uri, currentProfileId)
                _statusMessage.value = result.message
            } catch (e: Exception) {
                e.printStackTrace()
                _statusMessage.value = "Restore failed: ${e.localizedMessage ?: "Unknown error"}"
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
