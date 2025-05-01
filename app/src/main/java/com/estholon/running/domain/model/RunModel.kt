package com.estholon.running.domain.model

data class RunModel(
    var startDate : String?=null,
    var startTime : String?=null,
    var user: String?=null,
    var duration: String?=null,
    var intervalMode: Boolean?=null,
    var intervalDuration: Int?=null,
    var intervalRunTime: String?=null,
    var intervalWalkTime: String?=null,
    var goalDuration: String ?= null,
    var goalDistance: String ?= null,
    var kpiDistance: String ?= null,
    var kpiMaxSpeed: String ?= null,
    var kpiAvgSpeed: String ?= null,
    var kpiMinAltitude: Double?=null,
    var kpiMaxAltitude: Double?=null,
    var kpiMinLatitude: Double?=null,
    var kpiMaxLatitude: Double?=null,
    var kpiMinLongitude: Double?=null,
    var kpiMaxLongitude: Double?=null,
    var activatedGPS: Boolean?=null
)
