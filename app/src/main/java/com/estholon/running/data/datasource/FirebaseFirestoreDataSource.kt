package com.estholon.running.data.datasource

import android.util.Log
import com.estholon.running.data.dto.LevelDto
import com.estholon.running.data.dto.LocationDto
import com.estholon.running.data.dto.RunDto
import com.estholon.running.data.dto.TotalDto
import com.estholon.running.data.mapper.LevelMapper
import com.estholon.running.data.mapper.LocationMapper
import com.estholon.running.data.mapper.RunMapper
import com.estholon.running.data.mapper.TotalMapper
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.data.network.response.RunResponse
import com.estholon.running.data.network.response.TotalResponse
import com.estholon.running.data.response.LocationResponse
import com.estholon.running.domain.exception.RunningException
import com.estholon.running.domain.repository.AuthenticationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseFirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authenticationRepository: AuthenticationRepository,
    private val runMapper : RunMapper,
    private val levelMapper : LevelMapper,
    private val totalMapper: TotalMapper,
    private val locationMapper: LocationMapper
): DatabaseDataSource {

    companion object {
        private const val COLLECTION_LEVELS_RUNNING = "levelsRunning"
        private const val COLLECTION_RUNS_RUNNING = "runsRunning"
        private const val COLLECTION_TOTALS_RUNNING = "totalsRunning"
    }

    override fun getTotals(): Flow<TotalDto> {
        return firestore
            .collection(COLLECTION_TOTALS_RUNNING)
            .document(authenticationRepository.getCurrentEmail().toString())
            .snapshots()
            .mapNotNull { querySnapshot ->
                querySnapshot.toObject(TotalResponse::class.java)?.let { response ->
                    totalMapper.totalResponseToDto(response)
                }
            }
    }

    override fun getLevels(): Flow<List<LevelDto>> {
        return firestore
            .collection(COLLECTION_LEVELS_RUNNING)
            .snapshots()
            .mapNotNull { querySnapshot ->
                val levels = mutableListOf<LevelDto>()
                for (document in querySnapshot.documents) {
                    document.toObject(LevelResponse::class.java)?.let { response ->
                        levelMapper.levelResponseToDto(response)?.let { dto ->
                            levels.add(dto)
                        }
                    }
                }
                levels.sortedBy { it.distanceTarget }
            }

    }

    override fun getRun(runId: String): Flow<RunDto> {

        val id = "${authenticationRepository.getCurrentEmail()}${runId}"

        return firestore
            .collection(COLLECTION_RUNS_RUNNING)
            .document(id)
            .snapshots()
            .mapNotNull { querySnapshot ->
                querySnapshot.toObject(RunResponse::class.java)?.let { response ->
                    runMapper.runResponseToDto(response)
                }
            }

    }

    override fun getAllRuns(): Flow<List<RunDto>> {
        return firestore
            .collection(COLLECTION_RUNS_RUNNING)
            .whereEqualTo("user",authenticationRepository.getCurrentEmail())
            .snapshots()
            .mapNotNull { querySnapshot ->
                val runsDTOs = mutableListOf<RunDto>()
                for (document in querySnapshot.documents){
                    try {
                        document.toObject(RunResponse::class.java)?.let { response ->
                            runMapper.runResponseToDto(response)?.let { dto ->
                                runsDTOs.add(dto)
                            }
                        }
                    } catch (e: Exception){
                        continue
                    }
                }
                runsDTOs.sortedBy { it.startDate }
            }
            .catch { exception ->
                emit(emptyList())
            }
    }

    override suspend fun setTotals(total: TotalDto) : Result<Unit> {

        val model = hashMapOf(
            "recordAvgSpeed" to total.recordAvgSpeed,
            "recordDistance" to total.recordDistance,
            "recordSpeed" to total.recordSpeed,
            "totalDistance" to total.totalDistance,
            "totalRuns" to total.totalRuns,
            "totalTime" to total.totalTime
        )

        return try {
            firestore
                .collection(COLLECTION_TOTALS_RUNNING)
                .document(authenticationRepository.getCurrentEmail().toString())
                .set(model)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RunningException.NetworkException("Error: ${e.message}"))
        }

    }

    override suspend fun setRun(run: RunDto) : Result<Unit> {

        val id = "${authenticationRepository.getCurrentEmail()}${run.runId}"

        val model = hashMapOf(
            "user" to run.user,
            "runId" to run.runId,
            "startDate" to run.startDate,
            "startTime" to run.startTime,
            "kpiDuration" to run.kpiDuration,
            "kpiDistance" to run.kpiDistance,
            "kpiAvgSpeed" to run.kpiAvgSpeed,
            "kpiMaxSpeed" to run.kpiMaxSpeed,
            "kpiMinAltitude" to run.kpiMinAltitude,
            "kpiMaxAltitude" to run.kpiMaxAltitude,
            "goalDurationSelected" to run.goalDurationSelected,
            "goalHoursDefault" to run.goalHoursDefault,
            "goalMinutesDefault" to run.goalMinutesDefault,
            "goalSecondsDefault" to run.goalSecondsDefault,
            "goalDistanceDefault" to run.goalDistanceDefault,
            "intervalDefault" to run.intervalDefault,
            "intervalRunDuration" to run.intervalRunDuration,
            "intervalWalkDuration" to run.intervalWalkDuration,
            "rounds" to run.rounds
        )

        return try {
            firestore
                .collection(COLLECTION_RUNS_RUNNING)
                .document(id)
                .set(model)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RunningException.NetworkException("Error: ${e.message}"))
        }
    }

    override suspend fun deleteRun(runId: String) : Result<Unit> {

        val user = authenticationRepository.getCurrentEmail()
        val id = "$user$runId"

        return try {
            firestore
                .collection(COLLECTION_RUNS_RUNNING)
                .document(id)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RunningException.NetworkException("Error: ${e.message}"))
        }

    }

    override suspend fun getDistanceRecord() : Result<Double> {

        return try {

            val documents = firestore
                .collection(COLLECTION_RUNS_RUNNING)
                .orderBy("kpiDistance", Query.Direction.DESCENDING)
                .whereEqualTo("user",authenticationRepository.getCurrentEmail())
                .limit(1)
                .get()
                .await()

            val record = if (documents.isEmpty){
                0.0
            } else {
                documents.documents.first()
                    .getDouble("kpiDistance") ?: 0.0
            }

            Result.success(record)
        } catch (e: Exception){
            Result.failure(e)
        }

    }

    override suspend fun getAvgSpeedRecord() : Result<Double> {
        return try {

            val documents = firestore
                .collection(COLLECTION_RUNS_RUNNING)
                .orderBy("kpiAvgSpeed", Query.Direction.DESCENDING)
                .whereEqualTo("user",authenticationRepository.getCurrentEmail())
                .limit(1)
                .get()
                .await()

            val record = if (documents.isEmpty){
                0.0
            } else {
                documents.documents.first()
                    .getDouble("kpiAvgSpeed") ?: 0.0
            }

            Result.success(record)
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun getSpeedRecord() : Result<Double> {

        return try {

            val documents = firestore
                .collection(COLLECTION_RUNS_RUNNING)
                .orderBy("kpiSpeed", Query.Direction.DESCENDING)
                .whereEqualTo("user",authenticationRepository.getCurrentEmail())
                .limit(1)
                .get()
                .await()

            val record = if (documents.isEmpty){
                0.0
            } else {
                documents.documents.first()
                    .getDouble("kpiSpeed") ?: 0.0
            }

            Result.success(record)
        } catch (e: Exception){
            Result.failure(e)
        }

    }

    override suspend fun setLocation(
        runId: String,
        docName: String,
        location: LocationDto
    ) : Result<Unit> {
        val user = authenticationRepository.getCurrentEmail()

        val model = hashMapOf(
            "time" to location.time,
            "latitude" to location.latitude,
            "longitude" to location.longitude,
            "altitude" to location.altitude,
            "hasAltitude" to location.hasAltitude,
            "speedFromGoogle" to location.speedFromGoogle,
            "speedFromApp" to location.speedFromApp,
            "isMaxSpeed" to location.isMaxSpeed,
            "isRunInterval" to location.isRunInterval
        )

        return try {
            firestore
                .collection("locations/$user/$runId")
                .document(docName)
                .set(model)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RunningException.NetworkException("Error: ${e.message}"))
        }
    }

    override fun getLocations(runId: String): Flow<List<LocationDto>> {

        val user = authenticationRepository.getCurrentEmail()
        return firestore
            .collection("locations/$user/$runId")
            .snapshots()
            .mapNotNull { querySnapshot ->
                val list = mutableListOf<LocationDto>()
                for( document in querySnapshot.documents) {
                    try {
                        document.toObject(LocationResponse::class.java)?.let { response ->
                            locationMapper.locationResponseToDto(response)?.let { dto ->
                                list.add(dto)
                            }
                            }
                    } catch (e: Exception) {
                        continue
                    }
                }
                list.sortedBy { it.time }
            }
            .catch { exception ->
                emit(emptyList())
            }
    }

    override suspend fun deleteLocations(runId: String) : Result<Unit> {

        return try {

            val querySnapshot = firestore
                .collection("locations/${authenticationRepository.getCurrentEmail()}/${runId}")
                .get()
                .await()

            if(querySnapshot.isEmpty){
                return Result.success(Unit)
            }

            val batch = firestore.batch()

            for (document in querySnapshot.documents){
                batch.delete(document.reference)
            }

            batch.commit().await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(
                RunningException.NetworkException(
                "Error fetching locations for deletion: ${e.message}"
            ))
        }

    }
}