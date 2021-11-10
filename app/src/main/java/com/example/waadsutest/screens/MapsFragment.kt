package com.example.waadsutest.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.waadsutest.R
import com.example.waadsutest.screens.viewmodels.ViewModelMain
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer
import kotlinx.coroutines.delay
import okhttp3.internal.wait


class MapsFragment : Fragment() {

    private lateinit var viewModel: ViewModelMain

    private val callback = OnMapReadyCallback { googleMap ->

        viewModel.getGeoJson(googleMap)

        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, {
            Toast.makeText(context, "Something is wrong, please, check your internet connection"
                , Toast.LENGTH_LONG).show()
            viewModel.getGeoJson(googleMap)
        })

        viewModel.geoJsonLiveData.observe(viewLifecycleOwner, { jsonObj ->
            val layer = GeoJsonLayer(googleMap, jsonObj)
            viewModel.countLength(layer)

            var pathLength = 0
            viewModel.pathLengthLiveData.observe(viewLifecycleOwner, { _pathLength ->
                pathLength = _pathLength
            })

            val russia = LatLng(62.51951029803866, 93.01562831262851)

            val melbourne: Marker? = googleMap.addMarker(MarkerOptions()
                .position(russia)
                .title("Russia")
                .snippet("The length of the marked territory is: ${pathLength}km"))
            melbourne?.isVisible = false

            layer.setOnFeatureClickListener {
                melbourne?.isVisible = true
                melbourne?.showInfoWindow()

                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(russia, 2.0f),
                    500, null)
            }

            googleMap.setOnMapClickListener {
                melbourne?.isVisible = false
                melbourne?.hideInfoWindow()
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