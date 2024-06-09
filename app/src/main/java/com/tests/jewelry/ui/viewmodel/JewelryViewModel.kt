package com.tests.jewelry.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.data.db.ItemsDatabase
import com.tests.jewelry.data.repository.JewelryRepository
import kotlinx.coroutines.launch

class JewelryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JewelryRepository(application)

    val items : LiveData<List<JewelryEntities>>? = repository.getAllJewelry()

    private val _chosenJewelry = MutableLiveData<JewelryEntities>()
    val chosenJewelry : LiveData<JewelryEntities> get() = _chosenJewelry

    fun setJewelry(jewelry:JewelryEntities){
        _chosenJewelry.value = jewelry
    }

    fun getAllJewelry() {
        repository.getAllJewelry()
    }

    fun getJewelryByType(type: String){
        repository.getJewelryByType(type)
    }

    fun addJewelry(jewelry: JewelryEntities) = viewModelScope.launch {
        repository.addJewelry(jewelry)
    }

    fun deleteJewelry(jewelry: JewelryEntities) = viewModelScope.launch {
        repository.deleteJewelry()
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
}