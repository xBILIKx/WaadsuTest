package com.example.waadsutest.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.waadsutest.R
import com.example.waadsutest.screens.viewmodels.ViewModelMain
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.data.geojson.GeoJsonLayer


class MapsFragment : Fragment() {

    //Создание экземпляра viewModel
    private lateinit var viewModel: ViewModelMain

    //Создаю лямбду которая даёт мне доступ к экземпляру карты когда она будет готова
    private val callback = OnMapReadyCallback { googleMap ->

        //Прошу у viewModel geoJson
        viewModel.getGeoJson()

        //Обработка ошибок во время получения geoJson
        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, {
            //Создание всплывающего сообщения о том что возникла ошибка во время прогрузки слоя на карту
            Toast.makeText(context, "Something is wrong, please, check your internet connection"
                , Toast.LENGTH_LONG).show()
            //Создаю новый запрос
            viewModel.getGeoJson()
        })

        //Обработка полученного geoJson
        viewModel.geoJsonLiveData.observe(viewLifecycleOwner, { jsonObj ->
            //Когда получил geoJson очищаю лайв дату с ошибкой
            viewModel.clearErrorMessage()
            //Создание слоя на карту из полученного geoJson
            val layer = GeoJsonLayer(googleMap, jsonObj)

            //Прошу viewModel посчитать длину отмеченного пути на слое
            viewModel.countLength(layer)

            //Переменная в которой будет хранится длина пути
            var pathLength = 0

            //Прошу у viewModel длину пути соответсвтенно
            viewModel.pathLengthLiveData.observe(viewLifecycleOwner, { _pathLength ->
                pathLength = _pathLength
            })

            //Координаты России
            val russia = LatLng(62.51951029803866, 93.01562831262851)

            //Создаю маркер на России с информацией о длине отмеченного пути
            val melbourne: Marker? = googleMap.addMarker(MarkerOptions()
                .position(russia)
                .title("Russia")
                .snippet("The length of the marked territory is: ${pathLength}km"))

            //Скрываю маркер т.к я хочу что бы он был виден только во время нажатия на слой
            melbourne?.isVisible = false

            //Создаю обработчик нажатий на слой
            layer.setOnFeatureClickListener {
                //Делаю маркер видимым после нажатия на слой
                melbourne?.isVisible = true
                //Делаю видимым поле с информацией
                melbourne?.showInfoWindow()

                //Создание анимации переноса к маркеру во время нажатия на слой
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(russia, 2.0f),
                    500, null)
            }

            //Обработчик нажатия на карту(вне слоя)
            googleMap.setOnMapClickListener {
                //Скрываю маркер и всю доп.информацию
                melbourne?.isVisible = false
                melbourne?.hideInfoWindow()
            }

            //Добавляю маркер на карту
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

        //Инициализация viewModel
        viewModel = ViewModelProvider(this)[ViewModelMain::class.java]


        //Создание самой карты
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}