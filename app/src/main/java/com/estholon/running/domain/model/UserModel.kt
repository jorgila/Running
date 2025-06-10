package com.estholon.running.domain.model

data class UserModel(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val phoneNumber: String?
)