package tink.co.weatherforecast.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.layout_weather_item.view.*
import tink.co.weatherforecast.R
import tink.co.weatherforecast.dao.WeatherRoomEntity
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Tourdyiev Roman on 25.07.2020.
 */
class WeatherAdapter : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

    private var weatherList: List<WeatherRoomEntity> = listOf()
    private var expandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_weather_item, parent, false)
        return ViewHolder(view)
    }

    open fun setData(weatherList: List<WeatherRoomEntity>) {
        this.weatherList = weatherList
        expandedPosition = -1
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = weatherList[holder.adapterPosition]
        // show / hide details on click
        holder.itemView.details.visibility =
            if (holder.adapterPosition == expandedPosition) VISIBLE else GONE
        // show / hide hourly temperature
        holder.itemView.day_temp_ll.visibility = if (item.type == "daily") VISIBLE else GONE
        // date
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.UK)
        holder.itemView.date.text = sdf.format(item.date * 1000).toString()
        // weather forecast
        holder.itemView.forecast
        // load weather image
        val options = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_timer_sand_48dp)
            .error(R.drawable.ic_timer_sand_48dp)
        val imgURL = holder.itemView.context.resources.getString(R.string.icon_url) + item.icon + "@2x.png"
        Glide
            .with(holder.itemView.context)
            .load(imgURL)
            .apply(options)
            .into(holder.itemView.icon)
        // temperature
        holder.itemView.day_temp.text = "${item.temp.toInt()}°C"
        holder.itemView.temp_max.text = "${item.temp_max.toInt()}°C"
        holder.itemView.temp_min.text = "${item.temp_min.toInt()}°C"
        holder.itemView.temp_morn.text = "${item.temp_morn.toInt()}°C"
        holder.itemView.temp_day.text = "${item.temp_day.toInt()}°C"
        holder.itemView.temp_eve.text = "${item.temp_eve.toInt()}°C"
        holder.itemView.temp_night.text = "${item.temp_night.toInt()}°C"
        // humidity
        holder.itemView.humidity.text = "${item.humidity}%"
        // pressure
        holder.itemView.pressure.text = "${item.pressure}hPa"
        // wind
        holder.itemView.wind.text = "${item.wind}m/s"


        // click listener
        holder.itemView.setOnClickListener {
            if (expandedPosition == holder.adapterPosition) {
                expandedPosition = -1
            } else {
                expandedPosition = holder.adapterPosition
            }
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = weatherList.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

}