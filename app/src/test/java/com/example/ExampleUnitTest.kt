package com.example

import com.example.engine.CalculatorEngine
import com.example.engine.EvaluationResult
import com.example.model.AngleMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testBasicCalculations() {
        val r1 = CalculatorEngine.evaluate("10 + 5")
        assertTrue(r1 is EvaluationResult.Success)
        assertEquals("15", (r1 as EvaluationResult.Success).formatted)

        val r2 = CalculatorEngine.evaluate("10 + 5 × 2")
        assertTrue(r2 is EvaluationResult.Success)
        assertEquals("20", (r2 as EvaluationResult.Success).formatted)

        val r3 = CalculatorEngine.evaluate("(10 + 5) × 2")
        assertTrue(r3 is EvaluationResult.Success)
        assertEquals("30", (r3 as EvaluationResult.Success).formatted)

        val r4 = CalculatorEngine.evaluate("200 × 25%")
        assertTrue(r4 is EvaluationResult.Success)
        assertEquals("50", (r4 as EvaluationResult.Success).formatted)

        val rPercentMinus = CalculatorEngine.evaluate("100 - 30%")
        assertTrue(rPercentMinus is EvaluationResult.Success)
        assertEquals("70", (rPercentMinus as EvaluationResult.Success).formatted)

        val rPercentPlus = CalculatorEngine.evaluate("100 + 30%")
        assertTrue(rPercentPlus is EvaluationResult.Success)
        assertEquals("130", (rPercentPlus as EvaluationResult.Success).formatted)

        val rPercentMinus2 = CalculatorEngine.evaluate("500 - 20%")
        assertTrue(rPercentMinus2 is EvaluationResult.Success)
        assertEquals("400", (rPercentMinus2 as EvaluationResult.Success).formatted)

        val r5 = CalculatorEngine.evaluate("30 × 40 × 2")
        assertTrue(r5 is EvaluationResult.Success)
        assertEquals("2,400", (r5 as EvaluationResult.Success).formatted)

        val r6 = CalculatorEngine.evaluate("( 3.2E3 × 30 ) 25%")
        assertTrue(r6 is EvaluationResult.Success)
        assertEquals("24,000", (r6 as EvaluationResult.Success).formatted)

        val r7 = CalculatorEngine.evaluate("(25 + 75) × 2")
        assertTrue(r7 is EvaluationResult.Success)
        assertEquals("200", (r7 as EvaluationResult.Success).formatted)

        val r8 = CalculatorEngine.evaluate("1 ÷ 4")
        assertTrue(r8 is EvaluationResult.Success)
        assertEquals("0.25", (r8 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testScientificTrigonometry() {
        // sin(90) in DEG = 1
        val sin90 = CalculatorEngine.evaluate("sin(90)", AngleMode.DEG)
        assertTrue(sin90 is EvaluationResult.Success)
        assertEquals("1", (sin90 as EvaluationResult.Success).formatted)

        // cos(0) in DEG = 1
        val cos0 = CalculatorEngine.evaluate("cos(0)", AngleMode.DEG)
        assertTrue(cos0 is EvaluationResult.Success)
        assertEquals("1", (cos0 as EvaluationResult.Success).formatted)

        // tan(45) in DEG = 1
        val tan45 = CalculatorEngine.evaluate("tan(45)", AngleMode.DEG)
        assertTrue(tan45 is EvaluationResult.Success)
        assertEquals("1", (tan45 as EvaluationResult.Success).formatted)

        // sin(π/2) in RAD = 1
        val sinPiOver2 = CalculatorEngine.evaluate("sin(π / 2)", AngleMode.RAD)
        assertTrue(sinPiOver2 is EvaluationResult.Success)
        assertEquals("1", (sinPiOver2 as EvaluationResult.Success).formatted)

        // tan(90) in DEG -> Error (undefined)
        val tan90 = CalculatorEngine.evaluate("tan(90)", AngleMode.DEG)
        assertTrue(tan90 is EvaluationResult.Error)
    }

    @Test
    fun testScientificRootsAndPowers() {
        // √144 = 12
        val sqrt144 = CalculatorEngine.evaluate("√(144)")
        assertTrue(sqrt144 is EvaluationResult.Success)
        assertEquals("12", (sqrt144 as EvaluationResult.Success).formatted)

        // ∛27 = 3
        val cbrt27 = CalculatorEngine.evaluate("∛(27)")
        assertTrue(cbrt27 is EvaluationResult.Success)
        assertEquals("3", (cbrt27 as EvaluationResult.Success).formatted)

        // 2² = 4
        val twoSq = CalculatorEngine.evaluate("2²")
        assertTrue(twoSq is EvaluationResult.Success)
        assertEquals("4", (twoSq as EvaluationResult.Success).formatted)

        // 2³ = 8
        val twoCube = CalculatorEngine.evaluate("2³")
        assertTrue(twoCube is EvaluationResult.Success)
        assertEquals("8", (twoCube as EvaluationResult.Success).formatted)

        // 2^10 = 1024 (formatted as 1,024)
        val twoPow10 = CalculatorEngine.evaluate("2 ^ 10")
        assertTrue(twoPow10 is EvaluationResult.Success)
        assertEquals("1,024", (twoPow10 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testScientificLogsAndConstants() {
        // log(100) = 2
        val log100 = CalculatorEngine.evaluate("log(100)")
        assertTrue(log100 is EvaluationResult.Success)
        assertEquals("2", (log100 as EvaluationResult.Success).formatted)

        // ln(e) = 1
        val lnE = CalculatorEngine.evaluate("ln(e)")
        assertTrue(lnE is EvaluationResult.Success)
        assertEquals("1", (lnE as EvaluationResult.Success).formatted)
    }

    @Test
    fun testScientificErrorHandling() {
        // sqrt(-1) -> Error
        val negSqrt = CalculatorEngine.evaluate("√(-1)")
        assertTrue(negSqrt is EvaluationResult.Error)

        // log(0) -> Error
        val zeroLog = CalculatorEngine.evaluate("log(0)")
        assertTrue(zeroLog is EvaluationResult.Error)

        // ln(-5) -> Error
        val negLn = CalculatorEngine.evaluate("ln(-5)")
        assertTrue(negLn is EvaluationResult.Error)

        // Division by zero -> Error
        val divZero = CalculatorEngine.evaluate("10 ÷ 0")
        assertTrue(divZero is EvaluationResult.Error)
    }

    @Test
    fun testSettingsDisplaySeparatorsAndGrouping() {
        // 2.5 + 2.5 = 5
        val r1 = CalculatorEngine.evaluate("2.5 + 2.5", decimalSeparator = ".", numberGrouping = "Comma")
        assertTrue(r1 is EvaluationResult.Success)
        assertEquals("5", (r1 as EvaluationResult.Success).formatted)

        // 2,5 + 2,5 = 5 with comma separator
        val r2 = CalculatorEngine.evaluate("2,5 + 2,5", decimalSeparator = ",", numberGrouping = "Dot")
        assertTrue(r2 is EvaluationResult.Success)
        assertEquals("5", (r2 as EvaluationResult.Success).formatted)

        // Number grouping formatting:
        val num = java.math.BigDecimal("1000000")
        // Off
        assertEquals("1000000", CalculatorEngine.formatNumber(num, ".", "Off"))
        // Comma
        assertEquals("1,000,000", CalculatorEngine.formatNumber(num, ".", "Comma"))
        // Dot
        assertEquals("1.000.000", CalculatorEngine.formatNumber(num, ",", "Dot"))
        // Space
        assertEquals("1 000 000", CalculatorEngine.formatNumber(num, ".", "Space"))
    }

    @Test
    fun testTrailingOperatorsAndUnclosedParen() {
        val r1 = CalculatorEngine.evaluate("15 + ")
        assertTrue(r1 is EvaluationResult.Success)
        assertEquals("15", (r1 as EvaluationResult.Success).formatted)

        val r2 = CalculatorEngine.evaluate("(10 + 5")
        assertTrue(r2 is EvaluationResult.Success)
        assertEquals("15", (r2 as EvaluationResult.Success).formatted)
    }

    @Test
    fun testDecimalsAndPrecision() {
        val r = CalculatorEngine.evaluate("0.1 + 0.2")
        assertTrue(r is EvaluationResult.Success)
        assertEquals("0.3", (r as EvaluationResult.Success).formatted)
    }

    @Test
    fun testFormatExpressionGrouping() {
        // Less than 1000
        assertEquals("999", CalculatorEngine.formatExpression("999", ".", "Comma"))
        // 1000 and above
        assertEquals("1,000", CalculatorEngine.formatExpression("1000", ".", "Comma"))
        assertEquals("10,000", CalculatorEngine.formatExpression("10000", ".", "Comma"))
        assertEquals("1,000,000", CalculatorEngine.formatExpression("1000000", ".", "Comma"))
        // Expression with operators
        assertEquals("1,000 + 50,000", CalculatorEngine.formatExpression("1000 + 50000", ".", "Comma"))
        // Expression with decimals
        assertEquals("1,000.55", CalculatorEngine.formatExpression("1000.55", ".", "Comma"))
        // Grouping Off
        assertEquals("10000", CalculatorEngine.formatExpression("10000", ".", "Off"))
        // Space grouping
        assertEquals("1 000 000", CalculatorEngine.formatExpression("1000000", ".", "Space"))
        // Dot grouping with comma decimal
        assertEquals("1.000,50", CalculatorEngine.formatExpression("1000,50", ",", "Dot"))
    }

    @Test
    fun testAgeCalculation() {
        val age = com.example.ui.components.calculateDetailedAge(
            bYear = 2000, bMonth = 1, bDay = 1,
            tYear = 2026, tMonth = 10, tDay = 4
        )
        assertTrue(age.isValid)
        assertEquals(26, age.years)
        assertEquals(9, age.months)
        assertEquals(3, age.days)
        assertTrue(age.totalDays > 9000L)
    }

    @Test
    fun testDateAutoSlashFormatting() {
        // Typing 15 -> auto slash
        val step1 = com.example.ui.components.formatWithAutoSlash("1", "")
        assertEquals("1", step1)

        val step2 = com.example.ui.components.formatWithAutoSlash("15", "1")
        assertEquals("15/", step2)

        val step3 = com.example.ui.components.formatWithAutoSlash("15/0", "15/")
        assertEquals("15/0", step3)

        val step4 = com.example.ui.components.formatWithAutoSlash("15/08", "15/0")
        assertEquals("15/08/", step4)

        val step5 = com.example.ui.components.formatWithAutoSlash("15/08/1995", "15/08/")
        assertEquals("15/08/1995", step5)

        // Parse date
        val parsed = com.example.ui.components.parseDateInput("15/08/1995")
        assertNotNull(parsed)
        assertEquals(1995, parsed?.first)
        assertEquals(8, parsed?.second)
        assertEquals(15, parsed?.third)
    }

    @Test
    fun testTightenOperatorSpacing() {
        val tightenedPlus = com.example.ui.components.tightenOperatorSpacing("5 + 2")
        assertEquals("5\u202F+\u202F2", tightenedPlus)

        val tightenedPartial = com.example.ui.components.tightenOperatorSpacing("5 + ")
        assertEquals("5\u202F+\u202F", tightenedPartial)

        val tightenedGrouped = com.example.ui.components.tightenOperatorSpacing("1,000 + 500")
        assertEquals("1,000\u202F+\u202F500", tightenedGrouped)

        val tightenedMinus = com.example.ui.components.tightenOperatorSpacing("100 - 30%")
        assertEquals("100\u202F-\u202F30%", tightenedMinus)

        val tightenedTimesDiv = com.example.ui.components.tightenOperatorSpacing("10 × 20 ÷ 5")
        assertEquals("10\u202F×\u202F20\u202F÷\u202F5", tightenedTimesDiv)

        // Verify that expressions with tight spaces evaluate properly
        val eval = CalculatorEngine.evaluate(tightenedGrouped)
        assertTrue(eval is EvaluationResult.Success)
        assertEquals("1,500", (eval as EvaluationResult.Success).formatted)
    }
}
