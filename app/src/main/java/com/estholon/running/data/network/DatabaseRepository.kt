package com.estholon.running.data.network

import android.util.Log
import com.estholon.running.data.manager.AuthManager
import com.estholon.running.data.network.response.LevelResponse
import com.estholon.running.data.network.response.TotalResponse
import com.estholon.running.domain.model.LevelModel
import com.estholon.running.domain.model.TotalModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import java.util.logging.Level
import javax.inject.Inject

class DatabaseRepository @Inject constructor(
    val db: FirebaseFirestore,
    val authManager: AuthManager
) {

    companion object {
        const val COLLECTION_LEVELS_RUNNING = "levelsRunning"
        const val COLLECTION_RUNS_RUNNING = "runsRunning"
        const val COLLECTION_TOTALS_RUNNING = "totalsRunning"
    }

    fun getTotals() : Flow<TotalModel> {

        return db
            .collection(COLLECTION_TOTALS_RUNNING)
            .document(authManager.getCurrentEmail().toString())
            .snapshots()
            .mapNotNull { qr ->
                qr.toObject(TotalResponse::class.java)?.let { tr ->
                    totalToDomain(tr)
                }
            }

    }

    fun getLevels() : Flow<List<LevelModel>> {

        return db
            .collection(COLLECTION_LEVELS_RUNNING)
            .snapshots()
            .mapNotNull { qr ->
                val levelsList = mutableListOf<LevelModel>()
                for (document in qr.documents) {
                    document.toObject(LevelResponse::class.java)?.let { lr ->
                        levelToDomain(lr)?.let { levelModel ->
                            levelsList.add(levelModel)
                        }
                    }
                }
                levelsList.sortedBy { it.distanceTarget }
            }


    }

    private fun totalToDomain(tr: TotalResponse): TotalModel? {
        return if (
            tr.recordAvgSpeed != null &&
            tr.recordDistance != null &&
            tr.recordSpeed != null &&
            tr.totalDistance != null &&
            tr.totalRuns != null &&
            tr.totalTime != null
        ) {

            TotalModel(
                recordAvgSpeed = tr.recordAvgSpeed,
                recordDistance = tr.recordDistance,
                recordSpeed = tr.recordSpeed,
                totalDistance = tr.totalDistance,
                totalRuns = tr.totalRuns,
                totalTime = tr.totalTime
            )

        } else {
            null
        }
    }

    private fun levelToDomain(lr: LevelResponse): LevelModel? {
        return if (
            lr.distanceTarget != null &&
            lr.level !=null &&
            lr.runsTarget !=null
        ) {

            LevelModel(
                distanceTarget = lr.distanceTarget,
                level = lr.level,
                runsTarget = lr.runsTarget
            )

        } else {
            null
        }
    }

}