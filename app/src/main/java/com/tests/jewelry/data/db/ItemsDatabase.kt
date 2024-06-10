package com.tests.jewelry.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import android.telephony.ims.ImsMmTelManager
import androidx.room.Room
import com.tests.jewelry.JewelryDao
import com.tests.jewelry.JewelryEntities
import androidx.room.TypeConverters

@Database(entities = [JewelryEntities::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun jewelryItemDao(): JewelryDao

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
