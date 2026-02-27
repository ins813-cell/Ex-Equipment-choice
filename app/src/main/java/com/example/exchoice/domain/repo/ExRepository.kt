package com.example.exchoice.domain.repo

import com.example.exchoice.data.local.AppDatabase
import com.example.exchoice.data.local.CheckLogEntity
import com.example.exchoice.data.local.EquipmentEntity
import com.example.exchoice.data.local.RegistryCacheEntity
import com.example.exchoice.data.remote.BackendApi
import com.example.exchoice.domain.model.ExEquipment
import com.example.exchoice.domain.model.LifecycleStatus
import com.example.exchoice.domain.model.ParsedExFields
import com.example.exchoice.domain.model.RuleEvaluation
import com.example.exchoice.domain.model.SelectionConditions
import com.example.exchoice.domain.parser.ExMarkingParser
import com.example.exchoice.domain.rules.RuleEngine
import java.time.Instant

class ExRepository(
    private val db: AppDatabase,
    private val api: BackendApi,
    private val engine: RuleEngine = RuleEngine()
) {
    suspend fun seedCatalogIfEmpty() {
        if (db.dao().getApprovedEquipment().isNotEmpty()) return
        val demo = listOf(
            EquipmentEntity(
                manufacturer = "ExTech",
                model = "ETX-100",
                type = "Светильник",
                mode = "GAS",
                exMarkingRaw = "1 Ex db IIB T4 Gb Ta -40..+50",
                parsedJson = "{\"equipmentGroup\":\"II\",\"gasSubgroup\":\"IIB\",\"temperatureClass\":\"T4\"}",
                taMin = -40,
                taMax = 50,
                ip = "IP66",
                certNumber = "EAEUCN.RU.12345",
                certType = "cert",
                lifecycle = LifecycleStatus.Approved.name,
                catalogVersion = "1.0.0",
                syncedAt = System.currentTimeMillis()
            )
        )
        db.dao().insertEquipment(demo)
    }

    suspend fun filterCatalog(conditions: SelectionConditions): List<ExEquipment> {
        return db.dao().getApprovedEquipment().mapNotNull { e ->
            val parsed = ExMarkingParser.parse(e.exMarkingRaw, conditions.mode)
            val eval = engine.evaluate(parsed, conditions)
            if (eval.result.name == "OK") e.toDomain(parsed.fields) else null
        }
    }

    suspend fun evaluateManual(raw: String, conditions: SelectionConditions, manualEdits: String? = null): RuleEvaluation {
        val parsed = ExMarkingParser.parse(raw, conditions.mode)
        val result = engine.evaluate(parsed, conditions)
        db.dao().insertCheckLog(
            CheckLogEntity(
                projectId = null,
                rawMarking = raw,
                parsedStatus = parsed.status.name,
                verdict = result.result.name,
                reasonsJson = result.reasons.joinToString(";") { "${it.code}:${it.message}" },
                manualEdits = manualEdits,
                createdAt = System.currentTimeMillis()
            )
        )
        return result
    }

    suspend fun checkRegistry(number: String, type: String): RegistryCacheEntity {
        val key = "$type:$number"
        return try {
            val online = api.checkRegistry(number, type)
            val cache = RegistryCacheEntity(
                key = key,
                status = online.status,
                validFrom = online.valid_from,
                validTo = online.valid_to,
                regulation = online.regulation,
                holder = online.holder,
                checkedAt = online.checked_at,
                sourceLink = online.source_link,
                stale = false
            )
            db.dao().upsertRegistry(cache)
            cache
        } catch (e: Exception) {
            db.dao().getRegistry(key)?.copy(stale = true) ?: RegistryCacheEntity(
                key = key,
                status = "UNKNOWN",
                validFrom = null,
                validTo = null,
                regulation = null,
                holder = null,
                checkedAt = Instant.now().toString(),
                sourceLink = null,
                stale = true
            )
        }
    }

    private fun EquipmentEntity.toDomain(parsed: ParsedExFields) = ExEquipment(
        id = id,
        manufacturer = manufacturer,
        model = model,
        type = type,
        exMarkingRaw = exMarkingRaw,
        mode = com.example.exchoice.domain.model.EnvironmentMode.valueOf(mode),
        parsed = parsed,
        taMin = taMin,
        taMax = taMax,
        ip = ip,
        certNumber = certNumber,
        certType = certType,
        lifecycle = LifecycleStatus.valueOf(lifecycle),
        catalogVersion = catalogVersion,
        syncedAt = Instant.ofEpochMilli(syncedAt)
    )
}
