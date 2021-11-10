package com.example.waadsutest.screens.viewmodels

import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.maps.GoogleMap
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.maps.android.SphericalUtil
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode

class ViewModelMain : ViewModel() {
    private val _geoJsonLiveData = MutableLiveData<JSONObject>()
    private val _errorMessageLiveData = MutableLiveData<String>()
    private val _pathLengthLiveData = MutableLiveData<Int>()
    val geoJsonLiveData: LiveData<JSONObject>
        get() = _geoJsonLiveData
    val errorMessageLiveData: LiveData<String>
        get() = _errorMessageLiveData
    val pathLengthLiveData: LiveData<Int>
        get() = _pathLengthLiveData

    fun getGeoJson(googleMap: GoogleMap){
        val client = OkHttpClient();
        val request = Request.Builder()
            .url("https://waadsu.com/api/russia.geo.json")
            .build()

        viewModelScope.launch{
            delay(5000)
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    _errorMessageLiveData.postValue(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val responseString = response.body!!.string()

                        val geoJsonData = JSONObject(responseString)
                        _geoJsonLiveData.postValue(geoJsonData)
                    }
                }
            })
        }
    }

    fun countLength(layer: GeoJsonLayer){
        var sumMeters = 0.0
        layer.features.forEach {

            val multiPolygon: GeoJsonMultiPolygon = it.geometry as GeoJsonMultiPolygon
            val coordinates = multiPolygon.polygons
            coordinates.forEach { polygon ->
                polygon.coordinates.forEach { latIng ->
                    sumMeters += SphericalUtil.computeLength(latIng)
                }
            }
        }

        _pathLengthLiveData.value = (sumMeters.toBigDecimal())
            .setScale(0, RoundingMode.DOWN).toInt() / 1000
    }
}