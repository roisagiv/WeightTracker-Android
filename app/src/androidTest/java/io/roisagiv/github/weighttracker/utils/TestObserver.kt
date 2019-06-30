package io.roisagiv.github.weighttracker.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 *
 */
class TestObserver<T>(expectedCount: Int) : Observer<T> {
    private val values = mutableListOf<T>()
    private val latch: CountDownLatch = CountDownLatch(expectedCount)

    override fun onChanged(value: T) {
        values.add(value)
        latch.countDown()
    }

    fun assertValues(function: (List<T>) -> Unit) {
        function.invoke(values)
    }

    fun await(timeout: Long = 5, unit: TimeUnit = TimeUnit.SECONDS) {
        if (!latch.await(timeout, unit)) {
            throw TimeoutException()
        }
    }
}

fun <T> LiveData<T>.test(expectedCount: Int = 1) =
    TestObserver<T>(expectedCount).also {
        observeForever(it)
    }
