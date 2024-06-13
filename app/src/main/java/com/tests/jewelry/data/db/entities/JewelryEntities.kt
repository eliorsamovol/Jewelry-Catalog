package com.tests.jewelry

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

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

    @ColumnInfo(name = "weight")
    val weight: Double,

    @ColumnInfo(name = "image_res_id")
    val imageResId: String,

    @ColumnInfo(name = "sold_items")
    var soldItems: Int = 0,

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(description)
        parcel.writeDouble(price)
        parcel.writeDouble(weight)
        parcel.writeString(imageResId)
        parcel.writeInt(soldItems)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<JewelryEntities> {
        override fun createFromParcel(parcel: Parcel): JewelryEntities {
            return JewelryEntities(parcel)
        }

        override fun newArray(size: Int): Array<JewelryEntities?> {
            return arrayOfNulls(size)
        }
    }
}
