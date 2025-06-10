package com.estholon.running.data.repository

import android.content.Context
import com.estholon.running.data.datasource.AuthenticationDataSource
import com.estholon.running.data.mapper.UserMapper
import com.estholon.running.domain.model.UserModel
import com.estholon.running.domain.repository.AuthenticationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val authenticationDataSource: AuthenticationDataSource,
    private val userMapper: UserMapper,
    @ApplicationContext private val context: Context
) : AuthenticationRepository {

    override fun getCurrentUser(): UserModel? {
        return authenticationDataSource.getCurrentUser()?.let { dto ->
            userMapper.userDtoToDomain(dto)
        }
    }

    override fun getCurrentUID(): String? =
        authenticationDataSource.getCurrentUID()

    override fun getCurrentEmail(): String? =
        authenticationDataSource.getCurrentEmail()

    override fun getCurrentPhone(): String? =
        authenticationDataSource.getCurrentPhone()

    override fun getCurrentName(): String? =
        authenticationDataSource.getCurrentName()

    override fun isUserLogged(): Boolean =
        authenticationDataSource.isUserLogged()

    override suspend fun signUpWithEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.signUpWithEmail(email, password)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<UserModel?> {
        return authenticationDataSource.signInWithEmail(email, password)
            .map { dto -> dto?.let { userMapper.userDtoToDomain(it) } }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return authenticationDataSource.resetPassword(email)
    }

    override suspend fun signOut() {
        authenticationDataSource.signOut()
    }

}