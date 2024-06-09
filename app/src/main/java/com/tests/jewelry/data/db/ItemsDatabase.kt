package com.tests.jewelry.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import android.telephony.ims.ImsMmTelManager
import androidx.room.Room
import com.tests.jewelry.JewelryDao
import com.tests.jewelry.JewelryEntities

@Database(entities = [JewelryEntities::class], version = 1, exportSchema = false)
abstract class ItemsDatabase : RoomDatabase() {
    abstract fun jewelryItemDao(): JewelryDao

    companion object {
        @Volatile
        private var instance: ItemsDatabase? = null

        fun getDatabase(context: Context) : ItemsDatabase? {

            if(instance == null){
                synchronized(ItemsDatabase::class.java) {
                    instance = Room.databaseBuilder(context.applicationContext,
                        ItemsDatabase::class.java, "jewelry_database").allowMainThreadQueries().build()
                }
            }
            return instance
        }
    }
}
