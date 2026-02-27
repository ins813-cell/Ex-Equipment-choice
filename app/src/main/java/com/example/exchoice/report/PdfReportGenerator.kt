package com.example.exchoice.report

import android.content.Context
import android.graphics.pdf.PdfDocument
import com.example.exchoice.domain.model.ExEquipment
import com.example.exchoice.domain.model.RuleEvaluation
import com.example.exchoice.domain.model.SelectionConditions
import java.io.File

class PdfReportGenerator {
    fun generate(
        context: Context,
        fileName: String,
        conditions: SelectionConditions,
        positions: List<Pair<ExEquipment, RuleEvaluation>>,
        ruleSetVersion: String,
        catalogVersion: String,
        appVersion: String
    ): File {
        val doc = PdfDocument()
        val page = doc.startPage(PdfDocument.PageInfo.Builder(595, 842, 1).create())
        val c = page.canvas
        val p = android.graphics.Paint().apply { textSize = 12f }
        var y = 30f
        c.drawText("Отчёт Ex-Подбор", 20f, y, p); y += 18
        c.drawText("Условия: ${conditions.mode} зона ${conditions.zone}", 20f, y, p); y += 18
        c.drawText("RuleSet: $ruleSetVersion; Catalog: $catalogVersion; App: $appVersion", 20f, y, p); y += 18
        positions.forEach {
            c.drawText("${it.first.manufacturer} ${it.first.model} -> ${it.second.result}", 20f, y, p)
            y += 16
            it.second.reasons.forEach { r ->
                c.drawText("${r.code} ${r.message}", 40f, y, p)
                y += 14
            }
            y += 8
        }
        c.drawText("Результат — предварительная проверка по RuleSet v$ruleSetVersion", 20f, y, p)
        doc.finishPage(page)

        val file = File(context.cacheDir, fileName)
        file.outputStream().use { doc.writeTo(it) }
        doc.close()
        return file
    }
}
