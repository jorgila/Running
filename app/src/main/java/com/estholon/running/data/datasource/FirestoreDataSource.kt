package com.estholon.running.data.datasource

import com.estholon.running.data.dto.LevelDTO
import com.estholon.running.data.dto.LocationDTO
import com.estholon.running.data.dto.RunDTO
import com.estholon.running.data.dto.TotalDTO
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.mapper.LevelMapper
import com.estholon.running.data.mapper.RunMapper
import com.estholon.running.data.mapper.TotalMapper
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.data.network.response.RunResponse
import com.estholon.running.data.network.response.TotalResponse
import com.estholon.running.domain.exception.RunningException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authManager: AuthManager,
    private val runMapper : RunMapper,
    private val levelMapper : LevelMapper,
    private val totalMapper: TotalMapper
): DatabaseDataSource {

    companion object {
        private const val COLLECTION_LEVELS_RUNNING = "levelsRunning"
        private const val COLLECTION_RUNS_RUNNING = "runsRunning"
        private const val COLLECTION_TOTALS_RUNNING = "totalsRunning"
    }

    override fun getTotals(): Flow<TotalDTO> {
        return firestore
            .collection(COLLECTION_TOTALS_RUNNING)
            .document(authManager.getCurrentEmail().toString())
            .snapshots()
            .mapNotNull { querySnapshot ->
                querySnapshot.toObject(TotalResponse::class.java)?.let { response ->
                    totalMapper.totalResponseToDto(response)
                }
            }
    }

    override fun getLevels(): Flow<List<LevelDTO>> {
        return firestore
            .collection(COLLECTION_LEVELS_RUNNING)
            .snapshots()
            .mapNotNull { querySnapshot ->
                val levels = mutableListOf<LevelDTO>()
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

    override fun getRun(runId: String): Flow<RunDTO> {

        val id = "${authManager.getCurrentEmail()}${runId}"

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

    override fun getAllRuns(): Flow<List<RunDTO>> {
        return firestore
            .collection(COLLECTION_RUNS_RUNNING)
            .whereEqualTo("user",authManager.getCurrentEmail())
            .snapshots()
            .mapNotNull { querySnapshot ->
                val runsDTOs = mutableListOf<RunDTO>()
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

    override suspend fun setTotals(total: TotalDTO) : Result<Unit> {

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
                .document(authManager.getCurrentEmail().toString())
                .set(model)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(RunningException.NetworkException("Error: ${e.message}"))
        }

    }

    override suspend fun setRun(run: RunDTO) : Result<Unit> {

        val id = "${authManager.getCurrentEmail()}${run.runId}"

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

        val user = authManager.getCurrentEmail()
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
                .whereEqualTo("user",authManager.getCurrentEmail())
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
                .whereEqualTo("user",authManager.getCurrentEmail())
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
                .whereEqualTo("user",authManager.getCurrentEmail())
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
        location: LocationDTO
    ) : Result<Unit> {
        val user = authManager.getCurrentEmail()

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

    override suspend fun deleteLocations(runId: String) : Result<Unit> {

        return try {

            val querySnapshot = firestore
                .collection("locations/${authManager.getCurrentEmail()}/${runId}")
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