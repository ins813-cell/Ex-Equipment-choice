package com.example.exchoice.domain.parser

import com.example.exchoice.domain.model.EnvironmentMode
import com.example.exchoice.domain.model.ParseStatus
import com.example.exchoice.domain.model.ParsedExFields
import com.example.exchoice.domain.model.ParsedExResult

object ExMarkingParser {
    private val tempRegex = Regex("T[1-6]")
    private val gasRegex = Regex("II[ABC]")
    private val dustRegex = Regex("III[ABC]")
    private val equipGroupRegex = Regex("\\bIII?\\b")
    private val taRegex = Regex("Ta\\s*(-?\\d+)\\.\\.(\\+?\\d+)")

    fun parse(raw: String, mode: EnvironmentMode): ParsedExResult {
        val normalized = raw.uppercase().replace(',', ' ')
        val protection = listOf("D", "E", "I", "P", "M", "Q", "N")
            .filter { Regex("EX\\s*.*\\b$it\\b").containsMatchIn(normalized) }
            .map { it.lowercase() }

        val fields = ParsedExFields(
            equipmentGroup = equipGroupRegex.find(normalized)?.value,
            gasSubgroup = gasRegex.find(normalized)?.value,
            dustSubgroup = dustRegex.find(normalized)?.value,
            temperatureClass = tempRegex.find(normalized)?.value,
            protectionTypes = protection,
            ambientMin = taRegex.find(normalized)?.groupValues?.getOrNull(1)?.toIntOrNull(),
            ambientMax = taRegex.find(normalized)?.groupValues?.getOrNull(2)?.replace("+", "")?.toIntOrNull(),
            epl = Regex("\\b[GD][ABCD]\\b").find(normalized)?.value
        )

        val required = when (mode) {
            EnvironmentMode.GAS -> listOf("equipment_group", "gas_subgroup", "temperature_class")
            EnvironmentMode.DUST -> listOf("equipment_group", "dust_subgroup")
            EnvironmentMode.BOTH -> listOf("equipment_group", "gas_subgroup", "dust_subgroup", "temperature_class")
        }

        val missing = required.filterNot {
            when (it) {
                "equipment_group" -> fields.equipmentGroup != null
                "gas_subgroup" -> fields.gasSubgroup != null
                "dust_subgroup" -> fields.dustSubgroup != null
                "temperature_class" -> fields.temperatureClass != null
                else -> false
            }
        }

        val status = when {
            missing.isEmpty() -> ParseStatus.PARSED_OK
            listOf(fields.equipmentGroup, fields.gasSubgroup, fields.dustSubgroup, fields.temperatureClass)
                .all { it == null } -> ParseStatus.NOT_PARSED
            else -> ParseStatus.PARSED_PARTIAL
        }

        val warnings = if (normalized.contains("EX") && normalized.split("EX").size > 2) {
            listOf("ambiguous_token_order")
        } else emptyList()

        return ParsedExResult(raw, status, fields, missing, warnings)
    }
}
