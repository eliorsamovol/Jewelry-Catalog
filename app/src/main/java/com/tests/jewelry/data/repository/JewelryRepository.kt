package com.tests.jewelry.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.tests.jewelry.JewelryDao
import com.tests.jewelry.JewelryEntities
import com.tests.jewelry.data.db.ItemsDatabase

class JewelryRepository(application: Application) {

    private var jewelryDao:JewelryDao?

    init {
        val db = ItemsDatabase.getDatabase(application)
        jewelryDao = db?.jewelryItemDao()
    }

    val allJewelries : LiveData<List<JewelryEntities>> = jewelryDao!!.getAllJewelry()
    fun getAllJewelry() = jewelryDao?.getAllJewelry()

    fun getJewelryByType(type: String){
        jewelryDao?.getJewelryByType(type)
    }

    fun addJewelry(jewelry: JewelryEntities){
        jewelryDao?.addJewelry(jewelry)
    }

    fun deleteJewelry(jewelry: JewelryEntities){
        jewelryDao?.deleteJewelry(jewelry)
    }

    fun deleteAll(){
        jewelryDao?.deleteAll()
    }
}