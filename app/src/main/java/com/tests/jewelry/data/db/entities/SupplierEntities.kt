package com.tests.jewelry.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
@Entity(tableName = "supplier_table")
data class SupplierEntities(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name= "type")
    val type: String,

    @ColumnInfo(name = "phone_number")
    val phone: String,

    @ColumnInfo(name = "address")
    val address: String,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "reception")
    val reception: String,

    @ColumnInfo(name = "purchase_price")
    val purchasePrice: Double
)