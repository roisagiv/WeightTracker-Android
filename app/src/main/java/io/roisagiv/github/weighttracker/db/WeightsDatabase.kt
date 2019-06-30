package io.roisagiv.github.weighttracker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.roisagiv.github.weighttracker.entity.WeightItem

@Database(entities = [WeightItem::class], version = 1)
@TypeConverters(DateConverters::class)
abstract class WeightsDatabase : RoomDatabase() {
    abstract fun weightItemsDao(): WeightItemsDao
}
