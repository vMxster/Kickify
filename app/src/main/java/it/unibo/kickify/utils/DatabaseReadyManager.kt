package it.unibo.kickify.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DatabaseReadyManager {
    private val _isDatabaseReady = MutableStateFlow(false)
    val isDatabaseReady: StateFlow<Boolean> = _isDatabaseReady

    fun setDatabaseReady() {
        _isDatabaseReady.value = true
    }
}