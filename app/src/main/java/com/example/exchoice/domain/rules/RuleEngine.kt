package com.example.exchoice.domain.rules

import com.example.exchoice.domain.model.EnvironmentMode
import com.example.exchoice.domain.model.ParseStatus
import com.example.exchoice.domain.model.ParsedExResult
import com.example.exchoice.domain.model.RuleEvaluation
import com.example.exchoice.domain.model.RuleReason
import com.example.exchoice.domain.model.RuleTrace
import com.example.exchoice.domain.model.SelectionConditions
import com.example.exchoice.domain.model.Verdict

data class RuleSet(val version: String, val effectiveDate: String)

object DefaultReasonCodes {
    const val TCLASS_MISSING = "R_TCLASS_MISSING"
    const val GAS_SUBGROUP_MISMATCH = "R_GAS_SUBGROUP_MISMATCH"
    const val DUST_SUBGROUP_MISMATCH = "R_DUST_SUBGROUP_MISMATCH"
    const val TA_OUT_OF_RANGE = "R_TA_OUT_OF_RANGE"
}

class RuleEngine(private val ruleSet: RuleSet = RuleSet("1.0.0", "2026-01-01")) {

    fun evaluate(parsed: ParsedExResult, conditions: SelectionConditions): RuleEvaluation {
        if (parsed.status != ParseStatus.PARSED_OK) {
            val reasons = parsed.missingFields.map {
                RuleReason("R_${it.uppercase()}_MISSING", "Не заполнено поле: $it")
            }
            return RuleEvaluation(
                result = Verdict.NEED_MORE_DATA,
                reasons = reasons.ifEmpty { listOf(RuleReason("R_PARSE_FAILED", "Маркировку не удалось разобрать")) },
                ruleTrace = listOf(RuleTrace("SAFE_UNCERTAINTY", ruleSet.version))
            )
        }

        val reasons = mutableListOf<RuleReason>()
        val trace = mutableListOf<RuleTrace>()

        if (conditions.mode != EnvironmentMode.DUST && conditions.temperatureClass != null &&
            parsed.fields.temperatureClass == null
        ) {
            reasons += RuleReason(DefaultReasonCodes.TCLASS_MISSING, "Не указан температурный класс (T1–T6).")
            trace += RuleTrace("TCLASS_REQUIRED_GAS", ruleSet.version)
        }

        if (conditions.mode != EnvironmentMode.DUST && conditions.gasSubgroup != null) {
            val order = listOf("IIA", "IIB", "IIC")
            val actual = order.indexOf(parsed.fields.gasSubgroup)
            val need = order.indexOf(conditions.gasSubgroup)
            if (actual in 0 until need) {
                reasons += RuleReason(DefaultReasonCodes.GAS_SUBGROUP_MISMATCH, "Подгруппа газа ниже требуемой")
                trace += RuleTrace("GAS_SUBGROUP_COMPATIBILITY", ruleSet.version)
            }
        }

        if (conditions.mode != EnvironmentMode.GAS && conditions.dustSubgroup != null) {
            val order = listOf("IIIA", "IIIB", "IIIC")
            val actual = order.indexOf(parsed.fields.dustSubgroup)
            val need = order.indexOf(conditions.dustSubgroup)
            if (actual in 0 until need) {
                reasons += RuleReason(DefaultReasonCodes.DUST_SUBGROUP_MISMATCH, "Подгруппа пыли ниже требуемой")
                trace += RuleTrace("DUST_SUBGROUP_COMPATIBILITY", ruleSet.version)
            }
        }

        if (conditions.taMin != null && parsed.fields.ambientMin != null && conditions.taMin < parsed.fields.ambientMin) {
            reasons += RuleReason(DefaultReasonCodes.TA_OUT_OF_RANGE, "Минимальная Ta выходит за пределы маркировки")
            trace += RuleTrace("AMBIENT_TA_LIMIT", ruleSet.version)
        }
        if (conditions.taMax != null && parsed.fields.ambientMax != null && conditions.taMax > parsed.fields.ambientMax) {
            reasons += RuleReason(DefaultReasonCodes.TA_OUT_OF_RANGE, "Максимальная Ta выходит за пределы маркировки")
            trace += RuleTrace("AMBIENT_TA_LIMIT", ruleSet.version)
        }

        return if (reasons.isEmpty()) {
            RuleEvaluation(Verdict.OK, emptyList(), listOf(RuleTrace("BASE_PASS", ruleSet.version)))
        } else {
            RuleEvaluation(Verdict.NOT_OK, reasons, trace)
        }
    }
}
