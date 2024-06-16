package com.tests.jewelry.data.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.tests.jewelry.JewelryDao
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.data.db.ItemsDatabase
import com.tests.jewelry.data.db.entities.SupplierEntities
import com.tests.jewelry.*

class SupplierRepository(context: Context) {

    private val supplierDao: SupplierDao = ItemsDatabase.getDatabase(context)!!.supplierDao()

    fun getAllSuppliers(): LiveData<List<SupplierEntities>> {
        return supplierDao.getAllSuppliers()
    }

    fun getSupplierByType(type: String): LiveData<List<SupplierEntities>> {
        return supplierDao.getSupplierByType(type)
    }

    fun addSupplier(supplier: SupplierEntities){
        supplierDao.addSupplier(supplier)
    }

    fun deleteSupplier(supplier: SupplierEntities){
        supplierDao.deleteSupplier(supplier)
    }

    fun updateSupplier(supplier: SupplierEntities) {
        supplierDao.updateSupplier(supplier)
    }

    fun getSupplierByDate(startTime: Long, endTime: Long): LiveData<List<SupplierEntities>> {
        return supplierDao.getSupplierByDate(startTime, endTime)
    }

    fun deleteAll(){
        supplierDao.deleteAll()
    }

}