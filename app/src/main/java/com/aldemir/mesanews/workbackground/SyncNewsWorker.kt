package com.aldemir.mesanews.workbackground

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.aldemir.mesanews.ui.feed.FeedViewModel
import com.aldemir.mesanews.util.Constants
import kotlinx.coroutines.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SyncNewsWorker (appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams), CoroutineScope{

    private val job = Job()

    override val coroutineContext
        get() = Dispatchers.IO + job

    companion object {
        lateinit var feedViewModel: FeedViewModel
    }
    private val TAG = "workManager"


    override fun doWork(): Result {
        try {
            Log.i(TAG, "-------------- Iniciando Sincronismo Automático ----------------")

//            feedViewModel.getAllNews(1, 10)

            return Result.success()

        }catch (e: Throwable) {
            e.printStackTrace()
            Log.e(TAG, "Error fetching data", e)
            return Result.failure()
        }

    }

    override fun onStopped() {
        super.onStopped()
        Log.i(TAG, "OnStopped called for this worker")
    }
    private fun sleep() {
        try {
            Thread.sleep(Constants.DELAY_TIME_MILLIS, 0)
        } catch (e: InterruptedException) {
            Log.i(TAG, "Error $e")
        }
    }


}