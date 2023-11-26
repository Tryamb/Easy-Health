package com.tryamb.healthcare.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SigltonRetroftObject private constructor() {
    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getAPI(): API? {
        return retrofit.create(API::class.java)
    }

    companion object {
        private var mInstance: SigltonRetroftObject? = null
        private const val BASE_URL = "https://script.google.com/macros/s/AKfycbypdVYYLkntCsjooGdQzC8csePUT7lTOwoPhBs4kSS6uHmLVMD-wl1GDVCG_Xz0JDyb/"
        private lateinit var retrofit:Retrofit

        @Synchronized
        fun getmInstance(): SigltonRetroftObject? {
            if (mInstance == null) {
                mInstance = SigltonRetroftObject()
            }
            return mInstance
        }
    }
}
