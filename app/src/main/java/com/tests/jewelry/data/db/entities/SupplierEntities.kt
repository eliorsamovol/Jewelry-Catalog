package com.tests.jewelry.data.db.entities

import android.os.Parcel
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import android.os.Parcelable
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
    val purchasePrice: Double,

): Parcelable { // This constructor reads data from the Parcel to recreate a JewelryEntities object
    constructor(parcel: Parcel) : this(
        id = parcel.readInt(),
        name = parcel.readString()!!,
        type = parcel.readString()!!,
        phone = parcel.readString()!!,
        address = parcel.readString()!!,
        date = Date(parcel.readLong()),
        reception = parcel.readString()!!,
        purchasePrice = parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) { // Passing the data for multi-fragments view
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(phone)
        parcel.writeString(address)
        parcel.writeLong(date.time)
        parcel.writeString(reception)
        parcel.writeDouble(purchasePrice)
    }

    override fun describeContents(): Int { // Describes the contents of the Parcelable object
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SupplierEntities> { // Generating instances of the JewelryEntities class from parcel
        override fun createFromParcel(parcel: Parcel): SupplierEntities {
            return SupplierEntities(parcel)
        }

        override fun newArray(size: Int): Array<SupplierEntities?> {
            return arrayOfNulls(size)
        }
    }
}