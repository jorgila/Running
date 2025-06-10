package com.estholon.running.data.dto

data class LocationDto (
    val time : String,
    val latitude : Double,
    val longitude : Double,
    val altitude : Double,
    val hasAltitude : Boolean,
    val speedFromGoogle : Float,
    val speedFromApp : Double,
    val isMaxSpeed : Boolean,
    val isRunInterval : Boolean
)