package com.tests.jewelry.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewItemViewModel : ViewModel() {
    private val _jewelryName = MutableLiveData<String>()
    val jewelryName: LiveData<String> get() = _jewelryName

    fun setJewelryName(name: String){
        _jewelryName.value = name
    }
}