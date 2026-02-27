package com.example.exchoice.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(items: List<EquipmentEntity>)

    @Query("SELECT * FROM equipment WHERE lifecycle = 'Approved' ORDER BY manufacturer, model")
    suspend fun getApprovedEquipment(): List<EquipmentEntity>

    @Query("SELECT * FROM equipment WHERE model LIKE '%' || :q || '%' OR manufacturer LIKE '%' || :q || '%' OR type LIKE '%' || :q || '%' LIMIT 200")
    suspend fun searchEquipment(q: String): List<EquipmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProject(project: ProjectEntity): Long

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    suspend fun allProjects(): List<ProjectEntity>

    @Insert
    suspend fun insertCheckLog(log: CheckLogEntity)

    @Query("SELECT * FROM check_logs ORDER BY createdAt DESC LIMIT 500")
    suspend fun getCheckLogs(): List<CheckLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRegistry(cache: RegistryCacheEntity)

    @Query("SELECT * FROM registry_cache WHERE key = :key")
    suspend fun getRegistry(key: String): RegistryCacheEntity?
}
