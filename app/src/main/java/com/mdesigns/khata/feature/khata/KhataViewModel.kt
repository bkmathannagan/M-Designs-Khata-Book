package com.mdesigns.khata.feature.khata

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mdesigns.khata.core.datastore.UserPreferencesDataStore
import com.mdesigns.khata.core.utils.KhataBalanceCalculator
import com.mdesigns.khata.data.repository.KhataRepository
import com.mdesigns.khata.domain.model.PersonFilter
import com.mdesigns.khata.domain.model.PersonSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class KhataViewModel @Inject constructor(
    private val repository: KhataRepository,
    private val preferences: UserPreferencesDataStore
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(PersonFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _allSummaries = MutableStateFlow<List<PersonSummary>>(emptyList())

    val filteredSummaries: StateFlow<List<PersonSummary>> = combine(
        _allSummaries,
        _searchQuery,
        _selectedFilter
    ) { summaries, query, filter ->
        summaries.filter { item ->
            val matchesQuery = query.isEmpty() ||
                    item.person.name.contains(query, ignoreCase = true) ||
                    item.person.mobile.contains(query, ignoreCase = true)

            val matchesFilter = when (filter) {
                PersonFilter.ALL -> true
                PersonFilter.RECEIVABLE -> item.status == KhataBalanceCalculator.BalanceStatus.YOU_COLLECT
                PersonFilter.PAYABLE -> item.status == KhataBalanceCalculator.BalanceStatus.YOU_GIVE
                PersonFilter.SETTLED -> item.status == KhataBalanceCalculator.BalanceStatus.SETTLED
                PersonFilter.CUSTOMERS -> item.person.category.equals("Customer", ignoreCase = true)
                PersonFilter.SUPPLIERS -> item.person.category.equals("Supplier", ignoreCase = true)
            }

            matchesQuery && matchesFilter
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            preferences.activeProfileId.collectLatest { profileId ->
                repository.getPeopleSummaries(profileId).collectLatest { summaries ->
                    _allSummaries.value = summaries
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onFilterSelected(filter: PersonFilter) {
        _selectedFilter.value = filter
    }
}
