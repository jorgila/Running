package com.estholon.running.domain.model

data class LocationModel (
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