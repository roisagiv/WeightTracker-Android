package io.roisagiv.github.weighttracker.utils

object Resources {
    fun read(path: String): String {
        val stream = javaClass.classLoader?.getResourceAsStream("./$path")
        return stream?.bufferedReader()?.readText() ?: ""
    }
}
