package com.estholon.running.data.response

data class LocationResponse (
    val time : String = "00:00:00",
    val latitude : Double = 0.0,
    val longitude : Double = 0.0,
    val altitude : Double = 0.0,
    val hasAltitude : Boolean = false,
    val speedFromGoogle : Float = 0f,
    val speedFromApp : Double = 0.0,
    val isMaxSpeed : Boolean = false,
    val isRunInterval : Boolean = false
)