package com.estholon.running.data.datasource

import com.estholon.running.data.dto.UserDto

interface AuthenticationDataSource {

    fun getCurrentUser(): UserDto?
    fun getCurrentUID(): String?
    fun getCurrentEmail(): String?
    fun getCurrentPhone(): String?
    fun getCurrentName(): String?
    fun isUserLogged(): Boolean

    suspend fun signUpWithEmail(email: String, password: String): Result<UserDto?>
    suspend fun signInWithEmail(email: String, password: String): Result<UserDto?>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()

}