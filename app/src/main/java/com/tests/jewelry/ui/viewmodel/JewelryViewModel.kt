package com.tests.jewelry.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.data.repository.JewelryRepository
import com.tests.jewelry.data.repository.SupplierRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.Calendar

class JewelryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JewelryRepository = JewelryRepository(application)
    private val supplierRepository: SupplierRepository = SupplierRepository(application)

    val items : LiveData<List<JewelryEntities>> = repository.getAllJewelry()
    val suppliers : LiveData<List<SupplierEntities>> = supplierRepository.getAllSuppliers()

    private val _chosenJewelry = MutableLiveData<JewelryEntities>()
    val chosenJewelry : LiveData<JewelryEntities> get() = _chosenJewelry

    private val _chosenSupplier = MutableLiveData<SupplierEntities>()
    val chosenSupplier : LiveData<SupplierEntities> get() = _chosenSupplier


    fun setJewelry(jewelry:JewelryEntities){
        _chosenJewelry.value = jewelry
    }

    fun setSupplier(supplier: SupplierEntities){
        _chosenSupplier.value = supplier
    }

    fun getJewelryByType(type: String): LiveData<List<JewelryEntities>> {
       return repository.getJewelryByType(type)
    }

    fun getSupplierByType(type: String): LiveData<List<SupplierEntities>> {
        return supplierRepository.getSupplierByType(type)
    }

    fun addJewelry(jewelry: JewelryEntities){
        viewModelScope.launch(Dispatchers.IO){
            repository.addJewelry(jewelry)
        }
    }

    fun addSupplier(supplier: SupplierEntities){
        viewModelScope.launch(Dispatchers.IO){
            supplierRepository.addSupplier(supplier)
        }
    }

    fun deleteJewelry(jewelry: JewelryEntities){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteJewelry(jewelry)
        }
    }

    fun deleteSupplier(supplier: SupplierEntities){
        viewModelScope.launch(Dispatchers.IO){
            supplierRepository.deleteSupplier(supplier)
        }
    }

    fun updateJewelry(jewelry: JewelryEntities) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateJewelry(jewelry)
        }
    }

    fun updateSupplier(supplier: SupplierEntities) {
        viewModelScope.launch(Dispatchers.IO) {
            supplierRepository.updateSupplier(supplier)
        }
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    fun getItemsSortedByPriceAsc(): LiveData<List<JewelryEntities>> {
        return repository.getItemsSortedByPriceAsc()
    }

    fun getItemsSortedByPriceDesc(): LiveData<List<JewelryEntities>> {
        return repository.getItemsSortedByPriceDesc()
    }

    fun getJewelriesByDate(startTime: Long, endTime: Long): LiveData<List<JewelryEntities>> {
        return repository.getJewelriesByDate(startTime, endTime)
    }

    fun getSuppliersByDate(startTime: Long, endTime: Long): LiveData<List<SupplierEntities>> {
        return supplierRepository.getSupplierByDate(startTime, endTime)
    }

    fun getBestSellerByQuantity(): LiveData<JewelryEntities?> {
        return repository.getBestSellerByQuantity()
    }

    fun getBestSellerByRevenue(): LiveData<JewelryEntities?> {
        return repository.getBestSellerByRevenue()
    }

    fun getItemsByCreationOrder(): LiveData<List<JewelryEntities>> {
        return repository.getItemByCreationOrder()
    }

    fun updateSoldItems(jewelry: JewelryEntities){
        viewModelScope.launch(Dispatchers.IO){
            repository.updateJewelry(jewelry)
        }
    }
    fun getItemsSortedByBestSellers(): LiveData<List<JewelryEntities>> {
        return repository.getItemsSortedByBestSellers()
    }

}

class JewelryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelsClass: Class<T>): T {
        if(modelsClass.isAssignableFrom(JewelryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JewelryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}