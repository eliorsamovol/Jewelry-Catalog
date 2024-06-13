package com.tests.jewelry.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import android.telephony.ims.ImsMmTelManager
import androidx.room.Room
import com.tests.jewelry.*
import androidx.room.TypeConverters
import com.tests.jewelry.data.db.entities.SupplierEntities

@Database(entities = [JewelryEntities::class, SupplierEntities::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun jewelryItemDao(): JewelryDao
    abstract fun supplierDao(): SupplierDao

    companion object {
        @Volatile
        private var instance: ItemsDatabase? = null

        fun getDatabase(context: Context) : ItemsDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemsDatabase::class.java,
                    "jewelry_database"
                ).fallbackToDestructiveMigration().build()
                instance = newInstance
                newInstance
            }
        }
    }
}
