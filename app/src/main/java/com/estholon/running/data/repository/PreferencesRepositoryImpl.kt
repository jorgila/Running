package com.estholon.running.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.estholon.running.domain.repository.PreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPreferences: SharedPreferences
) : PreferencesRepository {

    private val editor: SharedPreferences.Editor
        get() = sharedPreferences.edit()

    override fun resetPreferences() {
        editor.clear()?.apply()
    }

    override fun putString(key: String, value: String) {
        editor.putString(key,value)?.apply()
    }

    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key,defaultValue) ?: defaultValue
    }

    override fun putInt(key: String, value: Int) {
        editor.putInt(key,value)?.apply()
    }

    override fun getInt(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key,defaultValue) ?: defaultValue
    }

    override fun putFloat(key: String, value: Float) {
        editor.putFloat(key,value)?.apply()
    }

    override fun getFloat(key: String, defaultValue: Float): Float {
        return sharedPreferences.getFloat(key,defaultValue) ?: defaultValue
    }

    override fun putBoolean(key: String, value: Boolean) {
        editor.putBoolean(key,value)?.apply()
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key,defaultValue) ?: defaultValue
    }

    override fun putLong(key: String, value: Long) {
        editor.putLong(key,value)?.apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key,defaultValue) ?: defaultValue
    }

}