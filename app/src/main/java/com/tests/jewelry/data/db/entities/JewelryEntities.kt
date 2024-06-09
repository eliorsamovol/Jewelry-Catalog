package com.tests.jewelry

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jewelry_table")
data class JewelryEntities(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name= "type")
    val type: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name= "price")
    val price: Double,

    @ColumnInfo(name = "image_res_id")
    val imageResId: String,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)