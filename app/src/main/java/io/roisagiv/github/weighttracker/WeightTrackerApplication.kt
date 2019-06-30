package io.roisagiv.github.weighttracker

import android.app.Application
import androidx.room.Room
import com.github.ajalt.timberkt.Timber
import com.grivos.spanomatic.Spanomatic
import com.jakewharton.threetenabp.AndroidThreeTen
import io.roisagiv.github.weighttracker.add.AddWeightViewModel
import io.roisagiv.github.weighttracker.add.LiveAddWeightViewModel
import io.roisagiv.github.weighttracker.api.AirtableAPI
import io.roisagiv.github.weighttracker.db.WeightsDatabase
import io.roisagiv.github.weighttracker.history.HistoryViewModel
import io.roisagiv.github.weighttracker.history.LiveHistoryViewModel
import io.roisagiv.github.weighttracker.repository.LiveWeightsRepository
import io.roisagiv.github.weighttracker.repository.WeightsRepository
import io.roisagiv.github.weighttracker.ui.TopAlignSuperscriptSpan
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 *
 */
@Suppress("unused")
open class WeightTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Spanomatic.setAnnotationSpanHandler("topAlignSuperscript") { value, _ ->
            TopAlignSuperscriptSpan(value.toFloat())
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        configureKoin()
    }

    protected open fun configureKoin() {
        val rootModule = module {
            viewModel<HistoryViewModel> { LiveHistoryViewModel(get()) }
            viewModel<AddWeightViewModel> { LiveAddWeightViewModel(get()) }
            factory {
                AirtableAPI.build(
                    baseUrl = get(named(AIRTABLE_URL)),
                    apiKey = get(named(AIRTABLE_KEY))
                )
            }
            factory {
                Room.databaseBuilder(get(), WeightsDatabase::class.java, "Weights.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .weightItemsDao()
            }
            factory<WeightsRepository> {
                LiveWeightsRepository(get(), get())
            }
            single(named(AIRTABLE_URL)) { BuildConfig.AIRTABLE_URL }
            single(named(AIRTABLE_KEY)) { BuildConfig.AIRTABLE_KEY }
        }

        startKoin {
            if (BuildConfig.DEBUG) {
                androidLogger(Level.DEBUG)
            } else {
                androidLogger(Level.INFO)
            }
            androidContext(this@WeightTrackerApplication)
            modules(rootModule)
        }
    }

    companion object {
        internal const val AIRTABLE_URL = "AIRTABLE_URL"
        internal const val AIRTABLE_KEY = "AIRTABLE_KEY"
    }
}
