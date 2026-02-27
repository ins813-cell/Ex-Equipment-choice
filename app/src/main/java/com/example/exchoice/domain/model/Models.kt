package com.example.exchoice.domain.model

import java.time.Instant

enum class EnvironmentMode { GAS, DUST, BOTH }
enum class Verdict { OK, NOT_OK, NEED_MORE_DATA }
enum class ParseStatus { PARSED_OK, PARSED_PARTIAL, NOT_PARSED }
enum class LifecycleStatus { Draft, Approved, Deprecated }

data class SelectionConditions(
    val mode: EnvironmentMode,
    val zone: Int,
    val gasSubgroup: String? = null,
    val dustSubgroup: String? = null,
    val temperatureClass: String? = null,
    val taMin: Int? = null,
    val taMax: Int? = null,
    val minIp: String? = null,
    val equipmentType: String? = null
)

data class ParsedExFields(
    val equipmentGroup: String? = null,
    val gasSubgroup: String? = null,
    val dustSubgroup: String? = null,
    val temperatureClass: String? = null,
    val protectionTypes: List<String> = emptyList(),
    val ambientMin: Int? = null,
    val ambientMax: Int? = null,
    val epl: String? = null
)

data class ParsedExResult(
    val raw: String,
    val status: ParseStatus,
    val fields: ParsedExFields,
    val missingFields: List<String>,
    val warnings: List<String>
)

data class RuleReason(val code: String, val message: String)
data class RuleTrace(val ruleId: String, val version: String)

data class RuleEvaluation(
    val result: Verdict,
    val reasons: List<RuleReason>,
    val ruleTrace: List<RuleTrace>
)

data class ExEquipment(
    val id: Long = 0,
    val manufacturer: String,
    val model: String,
    val type: String,
    val exMarkingRaw: String,
    val mode: EnvironmentMode,
    val parsed: ParsedExFields,
    val taMin: Int? = null,
    val taMax: Int? = null,
    val ip: String? = null,
    val certNumber: String? = null,
    val certType: String? = null,
    val lifecycle: LifecycleStatus = LifecycleStatus.Approved,
    val catalogVersion: String = "1.0.0",
    val syncedAt: Instant = Instant.now()
)
