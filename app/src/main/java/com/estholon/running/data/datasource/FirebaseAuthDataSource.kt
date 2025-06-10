package com.estholon.running.data.datasource

import android.content.Context
import com.estholon.running.data.dto.UserDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val context: Context
) : AuthenticationDataSource {

    private fun FirebaseUser.toUserDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        phoneNumber = phoneNumber
    )

    override fun getCurrentUser(): UserDto? = firebaseAuth.currentUser?.toUserDto()
    override fun getCurrentUID(): String? = firebaseAuth.currentUser?.uid
    override fun getCurrentEmail(): String? = firebaseAuth.currentUser?.email
    override fun getCurrentPhone(): String? = firebaseAuth.currentUser?.phoneNumber
    override fun getCurrentName(): String?  = firebaseAuth.currentUser?.displayName

    override fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    override suspend fun signUpWithEmail(email: String, password: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user?.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<UserDto?> {
        return suspendCancellableCoroutine { cancellableContinuation ->
            firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val result = if (it.user != null) {
                        Result.success(it.user?.toUserDto())
                    } else {
                        Result.failure(Exception("Error al iniciar sesión"))
                    }
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener {
                    val result = Result.failure<UserDto?>(Exception(it.message.toString()))
                    cancellableContinuation.resume(result)
                }
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return suspendCancellableCoroutine {cancellableContinuation ->
            firebaseAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    val result = Result.success(Unit)
                    cancellableContinuation.resume(result)
                }
                .addOnFailureListener{
                    val result = Result.failure<Unit>(Exception(it.message ?: "Error al restablecer la contraseña"))
                    cancellableContinuation.resume(result)
                }
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

}