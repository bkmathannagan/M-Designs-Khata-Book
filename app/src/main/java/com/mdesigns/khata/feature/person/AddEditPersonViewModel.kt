package com.mdesigns.khata.feature.person

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.data.entity.LedgerPersonEntity
import com.mdesigns.khata.data.repository.KhataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditPersonViewModel @Inject constructor(
    private val repository: KhataRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    fun savePerson(
        personId: Long?,
        name: String,
        mobile: String,
        category: String,
        openingBalance: Double,
        isOpeningDebit: Boolean,
        address: String,
        notes: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val profileId = preferences.activeProfileId.first()
            val entity = LedgerPersonEntity(
                id = personId ?: 0L,
                profileId = profileId,
                name = name.trim(),
                mobile = mobile.trim(),
                category = category,
                openingBalance = openingBalance,
                isOpeningBalanceDebit = isOpeningDebit,
                address = address.trim(),
                notes = notes.trim(),
                updatedAt = System.currentTimeMillis()
            )

            val id = if (personId == null || personId == 0L) {
                repository.insertPerson(entity)
            } else {
                repository.updatePerson(entity)
                personId
            }
            onSuccess(id)
        }
    }
}
