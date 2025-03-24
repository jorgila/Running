package com.estholon.running.data.manager

import android.content.Context
import android.content.SharedPreferences

object PreferencesManager {

    private const val PREFERENCES_NAME = "shared_preferences"
    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesEditor : SharedPreferences.Editor? = null

    fun init(context: Context){
        if (sharedPreferences==null){
            sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME,Context.MODE_PRIVATE)
        }
        if (sharedPreferencesEditor==null){
            sharedPreferencesEditor = sharedPreferences?.edit()
        }
    }

    // STRINGS

    fun putString(key: String, value: String){
        sharedPreferencesEditor?.putString(key,value)?.apply()
    }

    fun getString(key: String, defaultValue: String = "") : String {
        return sharedPreferences?.getString(key,defaultValue) ?: defaultValue
    }

    // INTEGERS

    fun putInt(key: String, value: Int){
        sharedPreferencesEditor?.putInt(key,value)?.apply()
    }

    fun getString(key: String, defaultValue: Int = 0) : Int {
        return sharedPreferences?.getInt(key,defaultValue) ?: defaultValue
    }

    // FLOATS

    fun putFloat(key: String, value: Float){
        sharedPreferencesEditor?.putFloat(key,value)?.apply()
    }

    fun getFloat(key: String, defaultValue: Float = 0F) : Float {
        return sharedPreferences?.getFloat(key,defaultValue) ?: defaultValue
    }

    // BOOLEANS

    fun putBoolean(key: String, value: Boolean){
        sharedPreferencesEditor?.putBoolean(key,value)?.apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false) : Boolean {
        return sharedPreferences?.getBoolean(key,defaultValue) ?: defaultValue
    }

    // BOOLEANS

    fun putLong(key: String, value: Long){
        sharedPreferencesEditor?.putLong(key,value)?.apply()
    }

    fun getLong(key: String, defaultValue: Long = 0) : Long {
        return sharedPreferences?.getLong(key,defaultValue) ?: defaultValue
    }

}