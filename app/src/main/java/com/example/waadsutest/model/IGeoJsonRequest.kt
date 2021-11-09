package com.example.waadsutest.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface IGeoJsonRequest {

    //url: https://waadsu.com/api/
    @GET("russia.geo.json")
    fun getGeoJson() : Call<String>
}