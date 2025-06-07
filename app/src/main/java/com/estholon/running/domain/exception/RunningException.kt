package com.estholon.running.domain.exception

sealed class RunningException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class NetworkException(message: String = "Network error occurred") : RunningException(message)
    class AuthenticationException(message: String = "Authentication failed") : RunningException(message)
    class DataNotFoundException(message: String = "Data not found") : RunningException(message)
    class ValidationException(message: String = "Validation failed") : RunningException(message)
    class LocationException(message: String = "Location service error") : RunningException(message)
    class UnknownException(message: String = "Unknown error occurred") : RunningException(message)

}