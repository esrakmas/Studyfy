package com.example.studyfy.modules.bottomBar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BottomBarVM : ViewModel() {

    // Varsayılan sekme (Home)
    private val _currentTab = MutableLiveData(0)
    val currentTab: LiveData<Int> get() = _currentTab

    fun setCurrentTab(tab: Int) {
        _currentTab.value = tab
    }

    fun getCurrentTab(): Int {
        return _currentTab.value ?: 0
    }
}
