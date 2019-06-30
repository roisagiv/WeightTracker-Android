package io.roisagiv.github.weighttracker.utils

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Helper method for testing LiveData objects, from
 * https://github.com/googlesamples/android-architecture-components.
 *
 * Get the value from a LiveData object. We're waiting for LiveData to emit, for 2 seconds.
 * Once we got a notification via onChanged, we stop observing.
 */
@Throws(InterruptedException::class)
fun <T> getValue(liveData: LiveData<T>): T {
    val data = arrayOfNulls<Any>(1)
    val latch = CountDownLatch(1)
    val observer: (t: T) -> Unit = { o ->
        data[0] = o
        latch.countDown()
    }
    liveData.observeForever(observer)
    latch.await(2, TimeUnit.SECONDS)
    liveData.removeObserver(observer)

    @Suppress("UNCHECKED_CAST")
    return data[0] as T
}
