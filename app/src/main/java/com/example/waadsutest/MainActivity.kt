package com.example.waadsutest

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController

//AIzaSyBj0pKcIndcJW40kbLqAD3vE0rRmoOBXlA

class MainActivity : AppCompatActivity() {

//    private val TAG = "MainActivity"
//    private val ERROR_DIALOG_REQUEST = 9001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)
    }

//    private fun init(){
//        findViewById<Button>(R.id.button).setOnClickListener {
//            val intent = Intent(this, MapsActivity::class.java)
//            startActivity(intent)
//        }
//    }

//    public fun isServicesOk(): Boolean{
//        Log.d(TAG, "isServicesOk: checking google services version")
//
//        val avalabile = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
//
//        if(avalabile == ConnectionResult.SUCCESS){
//            Log.d(TAG, "isServicesOk: everything is ok")
//            return true
//        }
//        else if(GoogleApiAvailability.getInstance().isUserResolvableError(avalabile)){
//            Log.d(TAG, "isServicesOk: error, but we can fix it")
//            val dialog = GoogleApiAvailability.getInstance().getErrorDialog(this, avalabile, ERROR_DIALOG_REQUEST)
//            dialog.show()
//        }
//        else{
//            Toast.makeText(this, "Something is wrong", Toast.LENGTH_LONG).show()
//        }
//
//        return false
//    }
}