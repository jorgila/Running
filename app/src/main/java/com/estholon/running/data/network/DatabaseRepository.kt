package com.estholon.running.data.network

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(val db: FirebaseFirestore) {

    companion object {
        const val LEVELS_RUNNING = "levelsRunning"
        const val RUNS_RUNNING = "runsRunning"
        const val TOTALS_RUNNING = "totalsRunning"
    }


}