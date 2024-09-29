package com.estholon.running.data.manager

import android.content.Context
import com.estholon.running.data.model.AuthRes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

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

    // Email Sign Up

    suspend fun signUpWithEmail(email: String, password: String): AuthRes<FirebaseUser?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = AuthRes.Error(it.message.toString())
                    cancellableContinuation.resume(result)
                }
        }
    }

    // Email Recover

    suspend fun resetPassword(email: String): AuthRes<Unit> {
        return suspendCancellableCoroutine {cancellableContinuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    val result = AuthRes.Success(Unit)
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener{
                    val result = AuthRes.Error(it.message ?: "Error al restablecer la contraseña")
                    cancellableContinuation.resume(result)
                }
        }
    }

    // Email Sign In
    suspend fun signInWithEmail(user: String, password: String): AuthRes<FirebaseUser?> {

        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithEmailAndPassword(user,password)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        AuthRes.Success(it.user)
                    } else {
                        AuthRes.Error("Error al iniciar sesión")
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = AuthRes.Error(it.message.toString())
                    cancellableContinuation.resume(result)
                }
        }
    }

}