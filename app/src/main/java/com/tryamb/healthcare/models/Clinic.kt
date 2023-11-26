package com.tryamb.healthcare.models

data class Clinic(
    val address: String,
    val contact: Long,
    val image: String,
    val lat: Double,
    val lon: Double,
    val name: String,
    val rating: Double,
    val speciality: String
)