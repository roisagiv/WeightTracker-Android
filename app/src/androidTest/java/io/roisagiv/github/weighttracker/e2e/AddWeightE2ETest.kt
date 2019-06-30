package io.roisagiv.github.weighttracker.e2e

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.JsonObject
import io.roisagiv.github.weighttracker.BuildConfig
import io.roisagiv.github.weighttracker.e2e.airtable.AirtableBatchAPI
import io.roisagiv.github.weighttracker.e2e.robot.AddWeightRobot
import io.roisagiv.github.weighttracker.e2e.robot.ApplicationRobot
import io.roisagiv.github.weighttracker.e2e.robot.HistoryRobot
import io.roisagiv.github.weighttracker.utils.Assets
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class AddWeightE2ETest {

    @Before
    fun before() = runBlocking {
        val airtable =
            AirtableBatchAPI.build(BuildConfig.AIRTABLE_E2E_URL, BuildConfig.AIRTABLE_E2E_KEY)

        val records = airtable.records()
        assertThat(records.isSuccessful).isTrue()
        records.body()?.records?.map { it.id }?.let {
            if (it.isNotEmpty()) {
                assertThat(airtable.delete(it).isSuccessful).isTrue()
            }
        }
        val body = Assets.read(
            "create_records_e2e_body.json",
            InstrumentationRegistry.getInstrumentation().context
        )
        val response = airtable.create(
            Gson().fromJson(body, JsonObject::class.java)
        )
        assertThat(response.isSuccessful).isTrue()
    }

    @Test
    fun addNewWeight() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        ApplicationRobot.launchApp()
        val historyRobot = HistoryRobot(uiDevice)
        historyRobot.assertPageDisplayed()
        historyRobot.assertNumberOfItemsInList(5)

        historyRobot.navigateToAddWeight()

        val addWeightRobot = AddWeightRobot(uiDevice)
        addWeightRobot.assertPageDisplayed()
        addWeightRobot.typeWeight("12.34")
        addWeightRobot.save()

        historyRobot.assertPageDisplayed()
        historyRobot.assertNumberOfItemsInList(6)
    }
}
