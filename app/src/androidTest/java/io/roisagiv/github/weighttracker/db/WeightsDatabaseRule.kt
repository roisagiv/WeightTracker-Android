package io.roisagiv.github.weighttracker.db

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class WeightsDatabaseRule : TestRule {

    internal lateinit var weightItemsDao: WeightItemsDao
    private lateinit var db: WeightsDatabase

    override fun apply(base: Statement?, description: Description?): Statement {
        return object : Statement() {
            override fun evaluate() {
                db = Room.inMemoryDatabaseBuilder(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    WeightsDatabase::class.java
                ).build()

                weightItemsDao = db.weightItemsDao()

                try {
                    base?.evaluate()
                } finally {
                    db.close()
                }
            }
        }
    }
}
