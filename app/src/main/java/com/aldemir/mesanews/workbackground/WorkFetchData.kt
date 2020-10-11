package com.aldemir.mesanews.workbackground

import android.content.Context
import android.util.Log
import androidx.work.*
import com.aldemir.mesanews.ui.feed.FeedViewModel
import com.aldemir.mesanews.util.Constants
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

class WorkFetchData(appContext: Context,  private val feedViewModel: FeedViewModel) {

    private val TAG = "workManager"
    private var mContext: Context = appContext

    private var mWorkManager: WorkManager? = null

    fun fetchData() {
        mWorkManager = WorkManager.getInstance(mContext)
        // Create Network constraint
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val periodicSyncDataWork = PeriodicWorkRequest.Builder(
            SyncNewsWorker::class.java,
            1,
            TimeUnit.MINUTES
        )
            .setInitialDelay(2, TimeUnit.MINUTES)
            .addTag(Constants.TAG_SYNC_NEW)
            .setConstraints(constraints) // setting a backoff on case the work needs to retry
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        SyncNewsWorker.feedViewModel = feedViewModel
//        mWorkManager!!.enqueue(periodicSyncDataWork)
        mWorkManager!!.enqueueUniquePeriodicWork(Constants.SYNC_DATA_NEW,
            ExistingPeriodicWorkPolicy.REPLACE, periodicSyncDataWork)
    }

    fun stopFetch() {

        if (WorkManager.getInstance(mContext).isAnyWorkScheduled(Constants.TAG_SYNC_NEW)) {
            WorkManager.getInstance(mContext).cancelAllWorkByTag(Constants.TAG_SYNC_NEW)
            Log.i(TAG, "----------- stopFetch called for this worker 2 ----------- ${WorkManager.getInstance(mContext).isAnyWorkScheduled(Constants.TAG_SYNC_NEW)}")
        }
    }

    private fun WorkManager.isAnyWorkScheduled(tag: String): Boolean {
        return try {
            getWorkInfosByTag(tag).get().firstOrNull { !it.state.isFinished } != null
        } catch (e: Exception) {
            when (e) {
                is ExecutionException, is InterruptedException -> {
                    e.printStackTrace()
                }
                else -> throw e
            }
            false
        }
    }
}