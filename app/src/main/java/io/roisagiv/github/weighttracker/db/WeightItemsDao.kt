package io.roisagiv.github.weighttracker.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import io.roisagiv.github.weighttracker.entity.WeightItem

@Dao
interface WeightItemsDao {
    @Insert(onConflict = REPLACE)
    suspend fun save(item: WeightItem)

    @Query("SELECT * from WeightItems")
    suspend fun all(): List<WeightItem>
}
