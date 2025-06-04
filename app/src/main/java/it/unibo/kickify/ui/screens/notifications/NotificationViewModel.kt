package it.unibo.kickify.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import it.unibo.kickify.data.database.Notification
import it.unibo.kickify.data.repositories.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel (
    private val repository: AppRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _notificationState = MutableStateFlow<List<Notification>?>(emptyList())
    val notificationState: StateFlow<List<Notification>?> = _notificationState.asStateFlow()

    private val _unreadNotifications = MutableStateFlow(0)
    val unreadNotifications: StateFlow<Int> = _unreadNotifications.asStateFlow()

    fun getNotifications(email: String) {
        dismissError()
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.getNotifications(email)
                result.onSuccess { list ->
                    _notificationState.value = list
                    dismissError()

                }.onFailure { exception ->
                    _notificationState.value = null
                    _errorMessage.value = exception.message
                }

            } catch (e: Exception) {
                _notificationState.value = emptyList()
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markNotificationsAsRead(email: String, notificationIds: List<Int>) {
        dismissError()
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val result = repository.markNotificationsAsRead(email, notificationIds)
                result.onSuccess {
                    dismissError()

                }.onFailure { exception ->
                    _errorMessage.value = exception.message
                }

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUnreadNotificationsCount(email: String) {
        dismissError()
        _isLoading.value = true

        viewModelScope.launch {
            try {
                _unreadNotifications.value = repository.getUnreadNotificationsCount(email)

            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Unexpected error."

            } finally {
                _isLoading.value = false
            }
        }
    }

    fun dismissError(){
        _errorMessage.value = null
    }
}