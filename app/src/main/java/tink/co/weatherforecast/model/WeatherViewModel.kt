package tink.co.weatherforecast.model

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import tink.co.weatherforecast.R
import tink.co.weatherforecast.dao.WeatherDatabase
import tink.co.weatherforecast.dao.WeatherRoomEntity
import java.io.IOException
import java.util.*

/**
 * Created by Tourdyiev Roman on 25.07.2020.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    var weatherLiveData: MutableLiveData<List<WeatherRoomEntity>> = MutableLiveData()
    var okHttpClient = OkHttpClient()

    fun getWeather(city: String, mode: Int) {
        val context: Context = getApplication()
        GlobalScope.launch(Dispatchers.IO) {
            weatherLiveData.postValue(
                when (mode) {
                    0 -> {
                        WeatherDatabase.getDB(context)?.weatherRoomDao()?.getAllWeatherHourly(city)
                    }
                    1 -> {
                        WeatherDatabase.getDB(context)?.weatherRoomDao()?.getAllWeatherDaily(city)
                    }
                    else -> WeatherDatabase.getDB(context)?.weatherRoomDao()
                        ?.getAllWeatherHourly(city)
                }

            )
            getWeatherFromWeb(city, mode)
        }
    }

    fun getWeatherFromWeb(city: String, mode: Int) {
        val context: Context = getApplication()
        val currentLocale = context.resources.configuration.locale.language
        var baseURL = ""
        when (mode) {
            0 -> {
                // hourly
                baseURL = context.resources.getString(R.string.hourly)
            }
            1 -> {
                // daily
                baseURL = context.resources.getString(R.string.daily)
            }
        }
        var lang = if (currentLocale == "ru") "ru" else "en"
        val appID = context.resources.getString(R.string.appid)
        var url = "$baseURL?q=$city&units=metric&lang=$lang&appid=$appID"
        val request: Request = Request.Builder().url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

                val json = response.body?.string()

                Log.d("jsonLog", json.toString())

                json?.let {

                    val jsonObject = JSONObject(json)
                    val code: Int? = jsonObject.getInt("cod")

                    code?.let {
                        if (code == 200) {
                            val jsonArray = jsonObject.getJSONArray("list")
                            val weatherList: MutableList<WeatherRoomEntity> = mutableListOf()
                            when (mode) {
                                0 -> {
                                    // hourly
                                    for (i in 0 until jsonArray.length()) {
                                        val item = jsonArray.getJSONObject(i)
                                        val weather = WeatherRoomEntity()
                                        weather.apply {
                                            this.type = "hourly"
                                            this.city = jsonObject.getJSONObject("city").getString("name")
                                            this.date = item.getLong("dt")
                                            this.temp = item.getJSONObject("main").getDouble("temp")
                                            this.temp_min = item.getJSONObject("main").getDouble("temp_min")
                                            this.temp_max = item.getJSONObject("main").getDouble("temp_max")
                                            this.icon = item.getJSONArray("weather").getJSONObject(0).getString("icon")
                                            this.forecast = item.getJSONArray("weather").getJSONObject(0).getString("main")
                                            this.humidity = item.getJSONObject("main").getInt("humidity")
                                            this.pressure = item.getJSONObject("main").getDouble("pressure")
                                            this.wind = item.getJSONObject("wind").getDouble("speed")
                                        }
                                        weatherList.add(weather)
                                    }
                                }
                                1 -> {
                                    // daily
                                    for (i in 0 until jsonArray.length()) {
                                        val item = jsonArray.getJSONObject(i)
                                        val weather = WeatherRoomEntity()
                                        weather.apply {
                                            this.type = "daily"
                                            this.city = jsonObject.getJSONObject("city").getString("name")
                                            this.date = item.getLong("dt")
                                            this.temp = item.getJSONObject("temp").getDouble("day")
                                            this.temp_min = item.getJSONObject("temp").getDouble("min")
                                            this.temp_max = item.getJSONObject("temp").getDouble("max")
                                            this.temp_morn = item.getJSONObject("temp").getDouble("morn")
                                            this.temp_day = item.getJSONObject("temp").getDouble("day")
                                            this.temp_eve = item.getJSONObject("temp").getDouble("eve")
                                            this.temp_night = item.getJSONObject("temp").getDouble("night")
                                            this.icon = item.getJSONArray("weather").getJSONObject(0).getString("icon")
                                            this.forecast = item.getJSONArray("weather").getJSONObject(0).getString("main")
                                            this.humidity = item.getInt("humidity")
                                            this.pressure = item.getDouble("pressure")
                                            this.wind = item.getDouble("speed")
                                        }
                                        weatherList.add(weather)
                                    }
                                }
                            }
                            WeatherDatabase.getDB(context)?.weatherRoomDao()?.insertWeather(weatherList)
                        }
                    }
                }
            }

        })
    }


}