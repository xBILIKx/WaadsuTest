package com.example.waadsutest.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.waadsutest.R
import com.example.waadsutest.screens.viewmodels.ViewModelMain
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.SphericalUtil
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon


class MapsFragment : Fragment() {

    private lateinit var viewModel: ViewModelMain

    private val callback = OnMapReadyCallback { googleMap ->


        //Todo() Перенести всю логику во view model, и доделать окно с протяженностью
        viewModel.getGeoJson()
        viewModel.geoJsonLiveData.observe(viewLifecycleOwner, { jsonObj ->
            val layer = GeoJsonLayer(googleMap, jsonObj)
            var sumMeters = 0.0
            layer.features.forEach {

                val a: GeoJsonMultiPolygon = it.geometry as GeoJsonMultiPolygon
                val coordinates = a.polygons
                coordinates.forEach { polygon ->
                    polygon.coordinates.forEach { latIng ->
                        sumMeters += SphericalUtil.computeLength(latIng)
                    }
                }
            }

            val russia = LatLng(62.51951029803866, 93.01562831262851)

            val melbourne = MarkerOptions()
                    .position(russia)
                    .title("Melbourne")

            layer.setOnFeatureClickListener {
//                println("Meters : ${sumMeters.toBigDecimal()}")

            }

            googleMap.setOnCircleClickListener {
                googleMap
//                melbourne?.hideInfoWindow()
            }
            layer.addLayerToMap()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ViewModelMain::class.java]

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}