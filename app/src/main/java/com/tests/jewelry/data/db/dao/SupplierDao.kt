package com.tests.jewelry

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.tests.jewelry.data.db.entities.SupplierEntities

@Dao
interface SupplierDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSupplier(supplier: SupplierEntities)

    @Delete
    fun deleteSupplier(supplier: SupplierEntities)

    @Update
    fun updateSupplier(supplier: SupplierEntities)

    @Query("SELECT * From supplier_table ORDER BY date DESC")
    fun getAllSuppliers(): LiveData<List<SupplierEntities>>

    @Query("SELECT *  FROM supplier_table WHERE type LIKE :type")
    fun getSupplierByType(type: String): LiveData<List<SupplierEntities>>

    @Query("DELETE FROM supplier_table")
    fun deleteAll()
}