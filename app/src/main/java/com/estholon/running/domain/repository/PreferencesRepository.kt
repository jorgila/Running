package com.estholon.running.domain.repository

interface PreferencesRepository {

    // RESET PREFERENCES
    fun resetPreferences()
    // STRING
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = "") : String
    // INTEGER
    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0) : Int
    // FLOAT
    fun putFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float = 0F) : Float
    // BOOLEAN
    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false) : Boolean
    // LONG
    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0) : Long

}