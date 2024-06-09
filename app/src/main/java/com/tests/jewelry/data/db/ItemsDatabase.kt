package com.tests.jewelry.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import android.telephony.ims.ImsMmTelManager
import androidx.room.Room
import com.tests.jewelry.JewelryDao
import com.tests.jewelry.JewelryEntities
import androidx.room.TypeConverters

@Database(entities = [JewelryEntities::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun jewelryItemDao(): JewelryDao

    companion object {
        @Volatile
        private var instance: ItemsDatabase? = null

        fun getDatabase(context: Context) : ItemsDatabase? {

            if(instance == null){
                synchronized(ItemsDatabase::class.java) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        ItemsDatabase::class.java, "jewelry_database").fallbackToDestructiveMigration().build()
                }
            }
            return instance
        }
    }
}
