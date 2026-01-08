package com.bazaar.vm

import androidx.lifecycle.ViewModel
import com.bazaar.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {
    private val _isSignedOut = MutableStateFlow(false)
    val isSignedOut = _isSignedOut.asStateFlow()

    init {
        // Do initial setup here
    }
}