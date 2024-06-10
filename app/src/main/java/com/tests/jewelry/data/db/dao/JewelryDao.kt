package com.tests.jewelry

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface JewelryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addJewelry(jewelry: JewelryEntities)

    @Delete
    fun deleteJewelry(jewelry: JewelryEntities)

    @Update
    fun updateJewelry(jewelry: JewelryEntities)

    @Query("SELECT * FROM jewelry_table ORDER BY name ASC")
    fun getAllJewelry(): LiveData<List<JewelryEntities>>

    @Query("SELECT *  FROM jewelry_table WHERE type LIKE :type")
    fun getJewelryByType(type: String): LiveData<List<JewelryEntities>>

    @Query("DELETE FROM jewelry_table")
    fun deleteAll()

}