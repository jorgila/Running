package com.estholon.running.domain.useCase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

abstract class BaseFlowResultUseCase<in P, R>(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    operator fun invoke(parameters: P): Flow<Result<R>> {
        return execute(parameters)
            .map { data -> Result.success(data) }
            .catch { exception -> emit(Result.failure(exception)) }
            .flowOn(coroutineDispatcher)
    }

    protected abstract fun execute(parameters: P): Flow<R>
}