package com.tryamb.healthcare.api

import com.tryamb.healthcare.models.NearbyPlaces
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface API {
    @GET("exec")
    fun getPlaceDetails(
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("circle") circle: Double?
    ): Call<NearbyPlaces>
}