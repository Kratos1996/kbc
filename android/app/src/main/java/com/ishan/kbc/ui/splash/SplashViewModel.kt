package com.ishan.kbc.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _isComplete = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete

    init {
        startLoading()
    }

    private fun startLoading() {
        viewModelScope.launch {
            var currentProgress = 0f
            while (currentProgress < 1f && !_isComplete.value) {
                // Organic progress increments
                val increment = (Math.random() * 0.02).toFloat()
                currentProgress += increment
                if (currentProgress > 1f) currentProgress = 1f
                _progress.value = currentProgress
                delay(30)
            }
            _isComplete.value = true
        }
    }
}
