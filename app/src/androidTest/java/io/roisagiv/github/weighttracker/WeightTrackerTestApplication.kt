package io.roisagiv.github.weighttracker

import androidx.test.platform.app.InstrumentationRegistry
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 *
 */
class WeightTrackerTestApplication : WeightTrackerApplication() {
    override fun configureKoin() {
        val endToEndTests = InstrumentationRegistry.getArguments().get("E2E")
        if (endToEndTests == "true") {
            super.configureKoin()
            loadKoinModules(module(override = true) {
                single(named(AIRTABLE_URL)) { BuildConfig.AIRTABLE_E2E_URL }
                single(named(AIRTABLE_KEY)) { BuildConfig.AIRTABLE_E2E_KEY }
            })
        } else {
            startKoin {
                androidLogger(Level.DEBUG)
            }
        }
    }
}
