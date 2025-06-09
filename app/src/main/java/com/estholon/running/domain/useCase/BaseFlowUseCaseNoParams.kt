package com.estholon.running.domain.useCase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

abstract class BaseFlowUseCaseNoParams<R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(): Flow<Result<R>> {
        return execute()
            .map { data -> Result.success(data) }
            .catch { exception -> emit(Result.failure(exception)) }
            .flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(): Flow<R>
}