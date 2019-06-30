package io.roisagiv.github.weighttracker.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.roisagiv.github.weighttracker.entity.WeightItem
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.OffsetDateTime
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class WeightsDatabaseTest {

    @get:Rule
    var weightsDatabaseRule = WeightsDatabaseRule()

    @Test
    fun newSavedItemShouldBeReturnInAllQuery() = runBlocking {
        // Arrange
        val dao = weightsDatabaseRule.weightItemsDao
        val newEntry = WeightItem(
            id = Random.nextInt().toString(),
            date = OffsetDateTime.now(),
            weight = Random.nextDouble(),
            notes = ""
        )

        // Act
        dao.save(newEntry)

        // Assert
        val items = dao.all()
        assertThat(items).hasSize(1)
        assertThat(items[0].weight).isEqualTo(newEntry.weight)
    }
}
