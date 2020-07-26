package tink.co.weatherforecast.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.fragment_weather_now.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tink.co.weatherforecast.R
import tink.co.weatherforecast.model.WeatherViewModel
import tink.co.weatherforecast.ui.activity.MainActivity
import tink.co.weatherforecast.ui.adapter.WeatherAdapter


/**
 * Created by Tourdyiev Roman on 24.07.2020.
 */
class WeatherFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var weatherViewModel: WeatherViewModel
    private var weatherAdapter = WeatherAdapter()
    private var city: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_weather_now, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        initViewModel()
        initViews()
    }

    private fun initViewModel() {
        weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel::class.java)
        weatherViewModel.weatherLiveData.observe(viewLifecycleOwner, Observer { weatherList ->
            GlobalScope.launch(Dispatchers.Main) {
                recycler_now.visibility = if (weatherList.isEmpty()) GONE else VISIBLE
                weatherAdapter.setData(weatherList)
                progress.visibility = GONE
            }
        })
    }

    private fun initViews() {

        city = sharedPreferences.getString("city", "")!!

        toolbar.title = city
        val navHostFragment = NavHostFragment.findNavController(this)
        NavigationUI.setupWithNavController(toolbar, navHostFragment)

        recycler_now.apply {
            adapter = weatherAdapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                weatherViewModel.getWeather(city, spinner.selectedItemPosition)
            }
        }
    }
}