package com.example.waadsutest.screens.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.maps.android.SphericalUtil
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.math.RoundingMode

class ViewModelMain : ViewModel() {
    /* Ниже изменяемые лайв даты которые доступны только внутри ViewModel (Это необходимо что бы никто из вне
    не мог изменять данные хранящиеся внутри ViewModel) и которые будут хранить в себе данные
    в которых нуждается view */
    private val _geoJsonLiveData = MutableLiveData<JSONObject>()
    private val _errorMessageLiveData = MutableLiveData<String>()
    private val _pathLengthLiveData = MutableLiveData<Int>()
    //Лайв даты которые только ВОЗВРАЩАЮТ изменяемые лайв даты, и доступны извне
    val geoJsonLiveData: LiveData<JSONObject>
        get() = _geoJsonLiveData
    val errorMessageLiveData: LiveData<String>
        get() = _errorMessageLiveData
    val pathLengthLiveData: LiveData<Int>
        get() = _pathLengthLiveData

    //Функция которая создают экземпляр geoJson и передаёт его view
    fun getGeoJson() {
        /* Инициализация клиента и запроса для получения нужного файла с сервера.
        * Я решил изпользовать OkHttp, а не retrofit, т.к в данном случае мне необходимо
        * получить лишь один файл с сервера, и мне даже не нужны никакие данные с него,
        * так что я решил что это будет экономнее и проще в плане сил и времени, */
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://waadsu.com/api/russia.geo.json")
            .build()

        //Курутина в которой происходит запрос на сервер
        viewModelScope.launch{
            /* Это необходимо только в случае когда сервер вернул ошибку, что бы можно было
            * создавать запрос на сервер с переодичностью в 5 секунд, а не дудосить его*/
            delay(5000)
            //Сам запрос
            client.newCall(request).enqueue(object : Callback {
                //Метод который срабатывает когда возникла ошибка во время выполнения запроса
                override fun onFailure(call: Call, e: IOException) {
                    //В лайв дату для сообщений об ошибках передаю сообщение об ошибке
                    _errorMessageLiveData.postValue(e.message)
                }
                //Метод который срабатывает когда запрос прошел успешно
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        //Если сервер возвращает ошибку, то вызывается исключение
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        //Строка которая содержит тело ответа
                        val responseString: String = response.body!!.string()

                        //Преобразование строки в json файл и загрузка его в соответствующую лайв дату
                        val geoJsonData = JSONObject(responseString)
                        _geoJsonLiveData.postValue(geoJsonData)
                    }
                }
            })


        }
    }

    /* Метод считающий длину отмеченного пути, и принимающий как аргумент слой, что бы получать из
    * него нужные данные */
    fun countLength(layer: GeoJsonLayer){
        //Переменная хранящая общую длину всего пути, типа double
        var sumMeters = 0.0

        /* Получаю данные слоя, и прохожусь по ним, т.к в этом случае один элемент features,
        *  цикл не нуждается в дополнительной обработке. */
        layer.features.forEach {

            //Тип геометрии "Мулти полигон", и я создаю его экземпляр с слоя
            val multiPolygon: GeoJsonMultiPolygon = it.geometry as GeoJsonMultiPolygon
            //Получаю массив полигонов из мулти полигона
            val polygons = multiPolygon.polygons
            /* Прохожусь по всем полигонов, получаю его координаты, далее гугловским методом
            * получаю его длину, и прибавляю её к общей сумме */
            polygons.forEach { polygon ->
                polygon.coordinates.forEach { latIng ->
                    sumMeters += SphericalUtil.computeLength(latIng)
                }
            }
        }

        /* После всего цикла сохраняю сумму в соответствующую лайв дату, предварительно преобразовав
        * её в тип данных BigDecimal, т.к число является довольно большим, и округлил его, после
        * этого преобразовываю его обратно в Int и делю на 1000 что бы получить данные в киллометр,
        * т.к метод от гугла возвращает данные в метрах */
        _pathLengthLiveData.value = (sumMeters.toBigDecimal())
            .setScale(0, RoundingMode.DOWN).toInt() / 1000
    }
}