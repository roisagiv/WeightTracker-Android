package io.roisagiv.github.weighttracker.utils

import android.content.Context
import java.io.BufferedReader

object Assets {
    fun read(path: String, context: Context): String {
        val inputStream = context.assets.open(path)
        return inputStream.bufferedReader().use(BufferedReader::readText)
    }
}
