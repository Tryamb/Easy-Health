package com.tryamb.healthcare

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.tryamb.healthcare.adapter.MyAdapter
import com.tryamb.healthcare.api.SigltonRetroftObject
import com.tryamb.healthcare.models.Clinic
import com.tryamb.healthcare.models.NearbyPlaces
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NearbyDoctors : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby_doctors)
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        val granted = PackageManager.PERMISSION_GRANTED

        if (ActivityCompat.checkSelfPermission(this, permission) != granted) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 123)
        } else {
            // Permission already granted, we proceed to get location
            getLocation()
        }
    }
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    val lat = it.latitude
                    val lon = it.longitude
                    Toast.makeText(
                        this,
                        "lat:$lat and lon:$lon",
                        Toast.LENGTH_SHORT
                    ).show()
                    doApiCall()
                } ?: run {
                    showDialog()
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    this,
                    "$it",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }


    private fun doApiCall() {
        val videoModelCall: Call<NearbyPlaces> =
            SigltonRetroftObject.getmInstance()!!.getAPI()!!.getPlaceDetails(
                28.7532, 77.4971,10.0
            )
        videoModelCall.enqueue(object : Callback<NearbyPlaces> {
            override fun onResponse(call: Call<NearbyPlaces>, response: Response<NearbyPlaces>) {
                setRecyclerView(response.body()!!.clinics)
                response.body()?.clinics?.forEach { it ->
                    val name = it.name
                    Log.d("places", name)
                }
            }

            override fun onFailure(call: Call<NearbyPlaces>, t: Throwable) {}
        })
    }

    private fun showDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Location Alert")
        builder.setMessage("Location is not available at this moment, try later!!")

        builder.setNegativeButton("Exit") { dialog, which ->
            finish()
        }

        builder.show()
    }
    private fun setRecyclerView(items: List<Clinic>) {
        val recyclerView=findViewById<RecyclerView>(R.id.recyclerView)
        val progressBar=findViewById<ProgressBar>(R.id.progressBar)
        val myAdapter = MyAdapter(this, items)
        recyclerView!!.layoutManager=LinearLayoutManager(this)
        recyclerView!!.adapter = myAdapter
        recyclerView!!.visibility = View.VISIBLE
        progressBar!!.visibility = View.GONE
    }

}