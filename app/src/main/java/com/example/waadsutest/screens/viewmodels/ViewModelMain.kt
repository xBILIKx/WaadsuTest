package com.example.waadsutest.screens.viewmodels

import android.widget.Toast
import androidx.lifecycle.*
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class ViewModelMain : ViewModel() {
    private val _geoJsonLiveData = MutableLiveData<JSONObject>()
    private val _errorMessageLiveData = MutableLiveData<String>()
    val geoJsonLiveData: LiveData<JSONObject>
        get() = _geoJsonLiveData
    val errorMessageLiveData: LiveData<String>
        get() = _errorMessageLiveData

    fun getGeoJson(){
        val client = OkHttpClient();
        val request = Request.Builder()
            .url("https://waadsu.com/api/russia.geo.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                _errorMessageLiveData.value = e.message
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