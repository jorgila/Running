package com.estholon.running.data.manager

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthManager @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
){

    // USER FUNCTIONS

    fun getCurrentUser() = firebaseAuth.currentUser

    fun getCurrentUID() = firebaseAuth.currentUser?.uid

    fun getCurrentEmail() = firebaseAuth.currentUser?.email

    fun getCurrentPhone() = firebaseAuth.currentUser?.phoneNumber

    fun getCurrentName() = firebaseAuth.currentUser?.displayName

    fun isUserLogged() : Boolean {
        return getCurrentUser() != null
    }


}