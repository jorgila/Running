package com.estholon.running.domain.repository

import com.estholon.running.domain.model.UserModel

interface AuthenticationRepository {

    fun getCurrentUser(): UserModel?
    fun getCurrentUID(): String?
    fun getCurrentEmail(): String?
    fun getCurrentPhone(): String?
    fun getCurrentName(): String?
    fun isUserLogged(): Boolean
    suspend fun signUpWithEmail(email: String, password: String): Result<UserModel?>
    suspend fun signInWithEmail(email: String, password: String): Result<UserModel?>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()

}