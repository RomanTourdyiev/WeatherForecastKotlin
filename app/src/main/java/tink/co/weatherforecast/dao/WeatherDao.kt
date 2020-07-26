package tink.co.weatherforecast.dao

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Created by Tourdyiev Roman on 24.07.2020.
 */
@Dao
open interface WeatherDao{

    @Query("SELECT * FROM weather WHERE city=:city AND type=\"hourly\" ORDER BY date DESC")
    fun getAllWeatherHourly(city:String): List<WeatherRoomEntity>

    @Query("SELECT * FROM weather WHERE city=:city AND type=\"daily\" ORDER BY date DESC")
    fun getAllWeatherDaily(city:String): List<WeatherRoomEntity>

    @Transaction
    @Insert
    fun insertWeather(weatherList: List<WeatherRoomEntity>)

}