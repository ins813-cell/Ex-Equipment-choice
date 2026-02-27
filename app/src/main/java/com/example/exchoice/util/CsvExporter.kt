package com.example.exchoice.util

import com.example.exchoice.data.local.CheckLogEntity

object CsvExporter {
    fun export(logs: List<CheckLogEntity>): String {
        val header = "id,projectId,rawMarking,parsedStatus,verdict,reasons,manualEdits,createdAt"
        val rows = logs.map {
            listOf(
                it.id,
                it.projectId ?: "",
                it.rawMarking.escape(),
                it.parsedStatus,
                it.verdict,
                it.reasonsJson.escape(),
                (it.manualEdits ?: "").escape(),
                it.createdAt
            ).joinToString(",")
        }
        return (listOf(header) + rows).joinToString("\n")
    }

    private fun String.escape() = "\"${replace("\"", "\"\"")}\""
}
