package com.tests.jewelry.data.repository

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.tests.jewelry.JewelryDao
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.data.db.ItemsDatabase

class JewelryRepository(context: Context) {

    private val jewelryDao:JewelryDao = ItemsDatabase.getDatabase(context)!!.jewelryItemDao()

    fun getAllJewelry() : LiveData<List<JewelryEntities>> {
        return jewelryDao.getAllJewelry()
    }

    fun getJewelryByType(type: String): LiveData<List<JewelryEntities>> {
        return jewelryDao.getJewelryByType(type)
    }

    fun addJewelry(jewelry: JewelryEntities){
        jewelryDao.addJewelry(jewelry)
    }

    fun deleteJewelry(jewelry: JewelryEntities){
        jewelryDao.deleteJewelry(jewelry)
    }

    fun updateJewelry(jewelry: JewelryEntities) {
        jewelryDao.updateJewelry(jewelry)
    }

    fun deleteAll(){
        jewelryDao.deleteAll()
    }
}