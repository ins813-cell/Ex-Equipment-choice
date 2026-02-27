package com.example.exchoice

import com.example.exchoice.domain.model.EnvironmentMode
import com.example.exchoice.domain.model.SelectionConditions
import com.example.exchoice.domain.model.Verdict
import com.example.exchoice.domain.parser.ExMarkingParser
import com.example.exchoice.domain.rules.RuleEngine
import org.junit.Assert.assertEquals
import org.junit.Test

class ParserAndRuleEngineTest {
    private val engine = RuleEngine()

    @Test
    fun partialParsingReturnsNeedMoreData() {
        val parsed = ExMarkingParser.parse("Ex db IIB", EnvironmentMode.GAS)
        val result = engine.evaluate(
            parsed,
            SelectionConditions(
                mode = EnvironmentMode.GAS,
                zone = 1,
                gasSubgroup = "IIB",
                temperatureClass = "T4"
            )
        )
        assertEquals(Verdict.NEED_MORE_DATA, result.result)
    }

    @Test
    fun subgroupMismatchReturnsNotOk() {
        val parsed = ExMarkingParser.parse("Ex db IIA T4", EnvironmentMode.GAS)
        val result = engine.evaluate(
            parsed,
            SelectionConditions(
                mode = EnvironmentMode.GAS,
                zone = 1,
                gasSubgroup = "IIC",
                temperatureClass = "T4"
            )
        )
        assertEquals(Verdict.NOT_OK, result.result)
    }

    @Test
    fun validMarkingReturnsOk() {
        val parsed = ExMarkingParser.parse("1 Ex db IIC T4 Gb Ta -40..+50", EnvironmentMode.GAS)
        val result = engine.evaluate(
            parsed,
            SelectionConditions(
                mode = EnvironmentMode.GAS,
                zone = 1,
                gasSubgroup = "IIB",
                temperatureClass = "T4",
                taMin = -20,
                taMax = 40
            )
        )
        assertEquals(Verdict.OK, result.result)
    }
}
