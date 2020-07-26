package tink.co.weatherforecast.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Created by Tourdyiev Roman on 24.07.2020.
 */
@Database(entities = [(WeatherRoomEntity::class)], version = 1, exportSchema = false)
abstract class WeatherDatabase:RoomDatabase(){

    abstract fun weatherRoomDao():WeatherDao

    companion object {
        private var db: WeatherDatabase? = null
        fun getDB(context: Context): WeatherDatabase? {
            if (db == null) {
                synchronized(WeatherDatabase::class) {
                    db = Room.databaseBuilder(
                        context.applicationContext,
                        WeatherDatabase::class.java,
                        "weather.db"
                    )
                        .fallbackToDestructiveMigration()
                        .setJournalMode(JournalMode.TRUNCATE)
                        .build()
                }
            }
            return db
        }
    }

}