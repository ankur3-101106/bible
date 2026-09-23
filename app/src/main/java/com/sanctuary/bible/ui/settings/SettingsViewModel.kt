package com.sanctuary.bible.ui.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sanctuary.bible.data.backup.BackupManager
import com.sanctuary.bible.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val selectedTheme: String = "Midnight (Dark)",
    val backupMessage: String? = null,
    val showRestoreConfirmDialog: Boolean = false,
    val pendingImportUri: Uri? = null,
    val isProcessingBackup: Boolean = false
)

class SettingsViewModel(
    private val planRepository: PlanRepository,
    private val backupManager: BackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun setReminderTime(hour: Int, minute: Int) {
        _uiState.update { it.copy(reminderHour = hour, reminderMinute = minute) }
    }

    fun exportBackup(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessingBackup = true, backupMessage = null) }
            val result = backupManager.exportBackupToUri(uri)
            val msg = if (result.isSuccess) "Backup successfully exported!" else "Backup export failed: ${result.exceptionOrNull()?.message}"
            _uiState.update { it.copy(isProcessingBackup = false, backupMessage = msg) }
        }
    }

    fun requestImportBackup(uri: Uri) {
        _uiState.update { it.copy(pendingImportUri = uri, showRestoreConfirmDialog = true) }
    }

    fun confirmImportBackup() {
        val uri = _uiState.value.pendingImportUri ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(showRestoreConfirmDialog = false, isProcessingBackup = true, backupMessage = null) }
            val result = backupManager.importBackupFromUri(uri)
            val msg = if (result.isSuccess) "Backup successfully restored!" else "Restore failed: ${result.exceptionOrNull()?.message}"
            _uiState.update { it.copy(isProcessingBackup = false, backupMessage = msg, pendingImportUri = null) }
        }
    }

    fun cancelImportBackup() {
        _uiState.update { it.copy(showRestoreConfirmDialog = false, pendingImportUri = null) }
    }

    fun clearBackupMessage() {
        _uiState.update { it.copy(backupMessage = null) }
    }

    class Factory(
        private val planRepository: PlanRepository,
        private val backupManager: BackupManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(planRepository, backupManager) as T
        }
    }
}
