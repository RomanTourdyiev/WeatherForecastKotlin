package tink.co.weatherforecast.dao

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

/**
 * Created by Tourdyiev Roman on 24.07.2020.
 */

@Entity(tableName = "weather")
class WeatherRoomEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var date: Long = 0
    var city: String = ""
    var temp: Double = 0.0
    var temp_min: Double = 0.0
    var temp_max: Double = 0.0
    var temp_morn: Double = 0.0
    var temp_day: Double = 0.0
    var temp_eve: Double = 0.0
    var temp_night: Double = 0.0
    var icon: String = ""
    var forecast: String = ""
    var humidity: Int = 0
    var pressure: Double = 0.0
    var wind: Double = 0.0
    var type: String = "hourly"

    override fun toString(): String {
        return "WeatherRoomEntity(date=$date, city='$city', temp=$temp, temp_min=$temp_min, temp_max=$temp_max, temp_morn=$temp_morn, temp_day=$temp_day, temp_eve=$temp_eve, temp_night=$temp_night, icon='$icon', forecast='$forecast', humidity=$humidity, pressure=$pressure, wind=$wind, type='$type')"
    }

}