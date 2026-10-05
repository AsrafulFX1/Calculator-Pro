package com.example.engine

import com.example.model.AngleMode
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale
import java.util.Stack
import kotlin.math.abs

object CalculatorEngine {

    private val mathContext = MathContext(16, RoundingMode.HALF_UP)

    private val KNOWN_FUNCTIONS = setOf(
        "sin", "cos", "tan",
        "asin", "acos", "atan",
        "sinh", "cosh", "tanh",
        "log", "ln", "sqrt", "cbrt"
    )

    fun evaluate(
        expression: String,
        angleMode: AngleMode = AngleMode.DEG,
        decimalSeparator: String = ".",
        numberGrouping: String = "Comma"
    ): EvaluationResult {
        if (expression.isBlank()) {
            return EvaluationResult.Success("0", BigDecimal.ZERO)
        }

        try {
            // Trim any trailing open operators e.g. "5 + " -> "5"
            val cleanedExpr = cleanTrailingOperators(expression)
            if (cleanedExpr.isBlank()) {
                return EvaluationResult.Success("0", BigDecimal.ZERO)
            }

            val normalized = normalizeExpression(cleanedExpr)
            val balanced = autoBalanceParentheses(normalized)
            val tokens = tokenize(balanced)
            if (tokens.isEmpty()) {
                return EvaluationResult.Success("0", BigDecimal.ZERO)
            }
            val postfix = infixToPostfix(tokens)
            val result = evaluatePostfix(postfix, angleMode)
            return EvaluationResult.Success(formatNumber(result, decimalSeparator, numberGrouping), result)
        } catch (e: ArithmeticException) {
            val msg = e.message ?: "Invalid format"
            return EvaluationResult.Error(msg)
        } catch (e: IllegalArgumentException) {
            val msg = e.message ?: "Invalid input"
            return EvaluationResult.Error(msg)
        } catch (e: Exception) {
            return EvaluationResult.Error("Invalid format")
        }
    }

    private fun cleanTrailingOperators(expr: String): String {
        var s = expr.trim()
        val ops = setOf('+', '-', '−', '×', '÷', '*', '/', '^')
        while (s.isNotEmpty() && s.last() in ops) {
            s = s.dropLast(1).trim()
        }
        return s
    }

    private fun autoBalanceParentheses(expr: String): String {
        var openCount = 0
        var closeCount = 0
        for (ch in expr) {
            if (ch == '(') openCount++
            else if (ch == ')') closeCount++
        }
        val diff = openCount - closeCount
        return if (diff > 0) {
            expr + ")".repeat(diff)
        } else {
            expr
        }
    }

    private fun normalizeExpression(expr: String): String {
        return expr
            .replace(Regex("[ \u202F\u2009\u200A\u00A0]"), "")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("−", "-")
            .replace("sin⁻¹", "asin")
            .replace("cos⁻¹", "acos")
            .replace("tan⁻¹", "atan")
            .replace("√", "sqrt")
            .replace("∛", "cbrt")
            .replace("²", "^2")
            .replace("³", "^3")
            .replace("π", "(3.141592653589793)")
            // Replace 'e' when standalone or not part of numbers like 1E5 or word like 'sqrt'
            .replace(Regex("(?<![0-9a-zA-Z])e(?![0-9a-zA-Z])"), "(2.718281828459045)")
            // Normalize thousands separators:
            // "1,000,000" -> "1000000"
            .replace(Regex("(?<=\\d),(?=\\d{3}(?:[+\\-*/^()]|$))"), "")
            .replace(Regex("(?<=\\d)\\.(?=\\d{3}(?:[+\\-*/^()]|$))"), "")
            // Any remaining comma is used as decimal separator (e.g. 2,5 -> 2.5)
            .replace(',', '.')
            .trim()
    }

    private fun tokenize(expr: String): List<String> {
        val rawTokens = mutableListOf<String>()
        var i = 0
        val n = expr.length

        while (i < n) {
            val c = expr[i]

            if (c.isWhitespace()) {
                i++
                continue
            }

            // Check for words (functions like sin, cos, sqrt, etc.)
            if (c.isLetter()) {
                val sb = StringBuilder()
                while (i < n && expr[i].isLetter()) {
                    sb.append(expr[i])
                    i++
                }
                val word = sb.toString().lowercase()
                if (word in KNOWN_FUNCTIONS) {
                    rawTokens.add(word)
                }
                continue
            }

            // Check for numbers (including decimals and scientific notation like 3.2E3)
            if (c.isDigit() || c == '.' || (c == '-' && isUnaryMinus(rawTokens))) {
                val sb = StringBuilder()
                if (c == '-') {
                    sb.append(c)
                    i++
                }
                while (i < n && (expr[i].isDigit() || expr[i] == '.')) {
                    sb.append(expr[i])
                    i++
                }
                // Scientific notation exponent (e.g. 1.5E3)
                if (i < n && (expr[i] == 'E' || expr[i] == 'e')) {
                    val peekNext = if (i + 1 < n) expr[i + 1] else ' '
                    if (peekNext.isDigit() || peekNext == '+' || peekNext == '-') {
                        sb.append('E')
                        i++
                        if (i < n && (expr[i] == '+' || expr[i] == '-')) {
                            sb.append(expr[i])
                            i++
                        }
                        while (i < n && expr[i].isDigit()) {
                            sb.append(expr[i])
                            i++
                        }
                    }
                }
                rawTokens.add(sb.toString())
                continue
            }

            // Operators & Parentheses
            if (c in "+-*/^()%") {
                rawTokens.add(c.toString())
                i++
                continue
            }

            i++
        }

        // Post-process tokens for implicit multiplication:
        val tokensWithImplicitMul = mutableListOf<String>()
        for (idx in rawTokens.indices) {
            val curr = rawTokens[idx]
            tokensWithImplicitMul.add(curr)

            if (idx + 1 < rawTokens.size) {
                val next = rawTokens[idx + 1]
                val currIsOperand = isNumber(curr) || curr == ")" || curr == "%"
                val nextIsOpStarter = next == "(" || next in KNOWN_FUNCTIONS || isNumber(next)

                if (currIsOperand && nextIsOpStarter && !(curr == "%" && next == ")")) {
                    tokensWithImplicitMul.add("*")
                }
            }
        }

        return tokensWithImplicitMul
    }

    private fun isUnaryMinus(tokens: List<String>): Boolean {
        if (tokens.isEmpty()) return true
        val last = tokens.last()
        return last in "+-*/^(" || last in KNOWN_FUNCTIONS
    }

    private fun isNumber(token: String): Boolean {
        return token.toDoubleOrNull() != null
    }

    private fun precedence(op: String): Int {
        return when (op) {
            in KNOWN_FUNCTIONS -> 5
            "%" -> 4
            "^" -> 3
            "*", "/" -> 2
            "+", "-" -> 1
            else -> 0
        }
    }

    private fun infixToPostfix(tokens: List<String>): List<String> {
        val output = mutableListOf<String>()
        val opStack = Stack<String>()

        for (token in tokens) {
            if (isNumber(token)) {
                output.add(token)
            } else if (token in KNOWN_FUNCTIONS) {
                opStack.push(token)
            } else if (token == "(") {
                opStack.push(token)
            } else if (token == ")") {
                while (opStack.isNotEmpty() && opStack.peek() != "(") {
                    output.add(opStack.pop())
                }
                if (opStack.isNotEmpty() && opStack.peek() == "(") {
                    opStack.pop()
                }
                // If there was a function associated with this parenthesis, pop it!
                if (opStack.isNotEmpty() && opStack.peek() in KNOWN_FUNCTIONS) {
                    output.add(opStack.pop())
                }
            } else if (token == "%") {
                output.add(token)
            } else {
                while (opStack.isNotEmpty() && precedence(opStack.peek()) >= precedence(token) && opStack.peek() != "(") {
                    output.add(opStack.pop())
                }
                opStack.push(token)
            }
        }

        while (opStack.isNotEmpty()) {
            val op = opStack.pop()
            if (op != "(" && op != ")") {
                output.add(op)
            }
        }

        return output
    }

    private fun evaluatePostfix(postfix: List<String>, angleMode: AngleMode): BigDecimal {
        val stack = Stack<BigDecimal>()

        for (idx in postfix.indices) {
            val token = postfix[idx]
            if (isNumber(token)) {
                stack.push(BigDecimal(token, mathContext))
            } else if (token == "%") {
                if (stack.isEmpty()) continue
                val top = stack.pop()
                val nextToken = if (idx + 1 < postfix.size) postfix[idx + 1] else null
                val percentVal = if ((nextToken == "+" || nextToken == "-") && stack.isNotEmpty()) {
                    val base = stack.peek()
                    base.multiply(top, mathContext).divide(BigDecimal("100"), mathContext)
                } else {
                    top.divide(BigDecimal("100"), mathContext)
                }
                stack.push(percentVal)
            } else if (token in KNOWN_FUNCTIONS) {
                if (stack.isEmpty()) throw ArithmeticException("Invalid format")
                val arg = stack.pop().toDouble()
                val res = evaluateFunction(token, arg, angleMode)
                stack.push(BigDecimal.valueOf(res))
            } else {
                if (stack.size < 2) continue
                val b = stack.pop()
                val a = stack.pop()

                val res = when (token) {
                    "+" -> a.add(b, mathContext)
                    "-" -> a.subtract(b, mathContext)
                    "*" -> a.multiply(b, mathContext)
                    "/" -> {
                        if (b.compareTo(BigDecimal.ZERO) == 0) {
                            throw ArithmeticException("Cannot divide by zero")
                        }
                        a.divide(b, mathContext)
                    }
                    "^" -> {
                        val base = a.toDouble()
                        val exp = b.toDouble()
                        val powRes = Math.pow(base, exp)
                        if (powRes.isNaN()) {
                            throw ArithmeticException("Invalid input")
                        }
                        if (powRes.isInfinite()) {
                            throw ArithmeticException("Overflow")
                        }
                        BigDecimal.valueOf(powRes)
                    }
                    else -> BigDecimal.ZERO
                }
                stack.push(res)
            }
        }

        return if (stack.isNotEmpty()) stack.pop() else BigDecimal.ZERO
    }

    private fun evaluateFunction(fn: String, x: Double, angleMode: AngleMode): Double {
        return when (fn) {
            "sin" -> {
                if (angleMode == AngleMode.DEG) {
                    val deg = x % 360.0
                    val normalizedDeg = if (deg < 0) deg + 360.0 else deg
                    when {
                        normalizedDeg == 0.0 || normalizedDeg == 180.0 || normalizedDeg == 360.0 -> 0.0
                        normalizedDeg == 90.0 -> 1.0
                        normalizedDeg == 270.0 -> -1.0
                        else -> cleanZero(Math.sin(Math.toRadians(x)))
                    }
                } else {
                    cleanZero(Math.sin(x))
                }
            }
            "cos" -> {
                if (angleMode == AngleMode.DEG) {
                    val deg = x % 360.0
                    val normalizedDeg = if (deg < 0) deg + 360.0 else deg
                    when {
                        normalizedDeg == 90.0 || normalizedDeg == 270.0 -> 0.0
                        normalizedDeg == 0.0 || normalizedDeg == 360.0 -> 1.0
                        normalizedDeg == 180.0 -> -1.0
                        else -> cleanZero(Math.cos(Math.toRadians(x)))
                    }
                } else {
                    cleanZero(Math.cos(x))
                }
            }
            "tan" -> {
                if (angleMode == AngleMode.DEG) {
                    val deg = x % 180.0
                    val normalizedDeg = if (deg < 0) deg + 180.0 else deg
                    if (normalizedDeg == 90.0) {
                        throw ArithmeticException("Invalid input")
                    }
                    when (normalizedDeg) {
                        0.0 -> 0.0
                        45.0 -> 1.0
                        135.0 -> -1.0
                        else -> cleanZero(Math.tan(Math.toRadians(x)))
                    }
                } else {
                    cleanZero(Math.tan(x))
                }
            }
            "asin" -> {
                if (x < -1.0 || x > 1.0) throw ArithmeticException("Invalid input")
                val rad = Math.asin(x)
                if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
            }
            "acos" -> {
                if (x < -1.0 || x > 1.0) throw ArithmeticException("Invalid input")
                val rad = Math.acos(x)
                if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
            }
            "atan" -> {
                val rad = Math.atan(x)
                if (angleMode == AngleMode.DEG) Math.toDegrees(rad) else rad
            }
            "sinh" -> {
                val res = Math.sinh(x)
                if (res.isInfinite() || res.isNaN()) throw ArithmeticException("Overflow")
                cleanZero(res)
            }
            "cosh" -> {
                val res = Math.cosh(x)
                if (res.isInfinite() || res.isNaN()) throw ArithmeticException("Overflow")
                cleanZero(res)
            }
            "tanh" -> {
                cleanZero(Math.tanh(x))
            }
            "log" -> {
                if (x <= 0.0) throw ArithmeticException("Invalid input")
                cleanZero(Math.log10(x))
            }
            "ln" -> {
                if (x <= 0.0) throw ArithmeticException("Invalid input")
                cleanZero(Math.log(x))
            }
            "sqrt" -> {
                if (x < 0.0) throw ArithmeticException("Invalid input")
                Math.sqrt(x)
            }
            "cbrt" -> {
                Math.cbrt(x)
            }
            else -> throw ArithmeticException("Invalid format")
        }
    }

    private fun cleanZero(v: Double): Double {
        return if (abs(v) < 1e-15) 0.0 else v
    }

    fun formatNumber(
        num: BigDecimal,
        decimalSeparator: String = ".",
        numberGrouping: String = "Comma"
    ): String {
        val actualGrouping = when {
            numberGrouping == "Off" -> "Off"
            numberGrouping == "Comma" && decimalSeparator == "," -> "Dot"
            numberGrouping == "Dot" && decimalSeparator == "." -> "Comma"
            else -> numberGrouping
        }

        val symbols = DecimalFormatSymbols(Locale.US).apply {
            this.decimalSeparator = if (decimalSeparator == ",") ',' else '.'
            when (actualGrouping) {
                "Comma" -> this.groupingSeparator = ','
                "Dot" -> this.groupingSeparator = '.'
                "Space" -> this.groupingSeparator = ' '
                else -> this.groupingSeparator = ','
            }
        }

        val abs = num.abs()
        // If extremely large or tiny non-zero, use scientific notation
        if ((abs.compareTo(BigDecimal("1e12")) >= 0 || (abs.compareTo(BigDecimal.ZERO) > 0 && abs.compareTo(BigDecimal("1e-6")) < 0))) {
            val df = DecimalFormat("0.######E0", symbols)
            return df.format(num)
        }

        val pattern = if (actualGrouping == "Off") "0.##########" else "#,##0.##########"
        val df = DecimalFormat(pattern, symbols)
        df.isGroupingUsed = (actualGrouping != "Off")
        return df.format(num)
    }

    fun formatExpression(
        expression: String,
        decimalSeparator: String = ".",
        numberGrouping: String = "Comma"
    ): String {
        if (expression.isEmpty()) return ""
        val actualGrouping = when {
            numberGrouping == "Off" -> "Off"
            numberGrouping == "Comma" && decimalSeparator == "," -> "Dot"
            numberGrouping == "Dot" && decimalSeparator == "." -> "Comma"
            else -> numberGrouping
        }
        if (actualGrouping == "Off") return expression

        val groupChar = when (actualGrouping) {
            "Comma" -> ','
            "Dot" -> '.'
            "Space" -> ' '
            else -> ','
        }
        val decChar = if (decimalSeparator == ",") ',' else '.'

        val sb = StringBuilder()
        var i = 0
        val n = expression.length

        while (i < n) {
            val c = expression[i]
            if (c.isDigit()) {
                val intDigits = StringBuilder()
                while (i < n && expression[i].isDigit()) {
                    intDigits.append(expression[i])
                    i++
                }
                sb.append(groupDigits(intDigits.toString(), groupChar))

                if (i < n && (expression[i] == decChar || expression[i] == '.' || expression[i] == ',')) {
                    sb.append(decChar)
                    i++
                    while (i < n && expression[i].isDigit()) {
                        sb.append(expression[i])
                        i++
                    }
                }
            } else {
                sb.append(c)
                i++
            }
        }

        return sb.toString()
    }

    private fun groupDigits(digits: String, separator: Char): String {
        if (digits.length <= 3) return digits
        val sb = StringBuilder()
        val rem = digits.length % 3
        if (rem > 0) {
            sb.append(digits.substring(0, rem))
            if (rem < digits.length) sb.append(separator)
        }
        var i = rem
        while (i < digits.length) {
            sb.append(digits.substring(i, i + 3))
            i += 3
            if (i < digits.length) {
                sb.append(separator)
            }
        }
        return sb.toString()
    }
}

sealed class EvaluationResult {
    data class Success(val formatted: String, val raw: BigDecimal) : EvaluationResult()
    data class Error(val message: String) : EvaluationResult()
}
