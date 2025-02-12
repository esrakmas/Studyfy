package com.example.studyfy.modules.bottomBar.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*
* _currentTab: Alt menüdeki mevcut sekmeyi tutar. Bu, kullanıcı hangi
*  sekmeye tıklarsa onun değerini günceller.
setCurrentTab(): Tab geçişlerini yönetmek için kullanılır.
*
* */

class BottomBarVM : ViewModel() {

    private val _bottomBarModel = MutableLiveData(BottomBarModel())
    val bottomBarModel: LiveData<BottomBarModel> get() = _bottomBarModel

    fun setCurrentTab(tab: Int) {
        _bottomBarModel.value = _bottomBarModel.value?.copy(currentTab = tab)
    }
}