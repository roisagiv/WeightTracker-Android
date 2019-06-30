package io.roisagiv.github.weighttracker.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.OffsetDateTime

/**
 *
 */
@Entity(tableName = "WeightItems")
data class WeightItem(
    @PrimaryKey val id: String,
    val date: OffsetDateTime,
    val weight: Double,
    val notes: String?
)

/**
 *
 */
data class NewWeightItem(
    val date: OffsetDateTime,
    val weight: Double,
    val notes: String?
)
