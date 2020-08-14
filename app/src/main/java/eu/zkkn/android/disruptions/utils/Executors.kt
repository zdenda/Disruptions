package eu.zkkn.android.disruptions.utils

import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


private val NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors()

private val BACKGROUND_WORK_EXECUTOR = ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES,
    1L, TimeUnit.SECONDS, LinkedBlockingQueue<Runnable>())

private val IO_EXECUTOR = Executors.newSingleThreadExecutor()


/**
 * Utility method to run blocks on a dedicated background thread, used for io/database work.
 */
fun ioThread(f : () -> Unit) {
    IO_EXECUTOR.execute(f)
}

/**
 * Utility method to run blocks on a dedicated threads for background work.
 */
@Suppress("unused")
fun backgroundThread(f: () -> Unit) {
    BACKGROUND_WORK_EXECUTOR.execute(f)
}
