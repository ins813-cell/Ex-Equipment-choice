package com.example.exchoice.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class EquipmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val manufacturer: String,
    val model: String,
    val type: String,
    val mode: String,
    val exMarkingRaw: String,
    val parsedJson: String,
    val taMin: Int?,
    val taMax: Int?,
    val ip: String?,
    val certNumber: String?,
    val certType: String?,
    val lifecycle: String,
    val catalogVersion: String,
    val syncedAt: Long
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val conditionsJson: String,
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "check_logs")
data class CheckLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val projectId: Long?,
    val rawMarking: String,
    val parsedStatus: String,
    val verdict: String,
    val reasonsJson: String,
    val manualEdits: String?,
    val createdAt: Long
)

@Entity(tableName = "registry_cache")
data class RegistryCacheEntity(
    @PrimaryKey val key: String,
    val status: String,
    val validFrom: String?,
    val validTo: String?,
    val regulation: String?,
    val holder: String?,
    val checkedAt: String,
    val sourceLink: String?,
    val stale: Boolean
)
