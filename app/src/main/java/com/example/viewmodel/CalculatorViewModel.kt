package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HistoryItem
import com.example.data.HistoryManager
import com.example.data.RoomHistoryStorage
import com.example.data.SettingsManager
import com.example.engine.CalculatorEngine
import com.example.engine.EvaluationResult
import com.example.model.AngleMode
import com.example.model.CalcPalette
import com.example.model.CalculatorMode
import com.example.model.ColorThemeScheme
import com.example.model.ThemeMode
import com.example.model.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val displayExpression: String = "",
    val result: String = "",
    val lastAnswer: String = "",
    val previousFormula: String = "",
    val isEvaluated: Boolean = false,
    val calculatorMode: CalculatorMode = CalculatorMode.BASIC,
    val angleMode: AngleMode = AngleMode.DEG,
    val isSecondFunction: Boolean = false,
    val decimalSeparator: String = ".",
    val numberGrouping: String = "Comma",
    val isLargeButtonsEnabled: Boolean = false,
    val selectedPalette: CalcPalette = ThemeRepository.Orchid,
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val isHistoryVisible: Boolean = false,
    val isVibrationEnabled: Boolean = true,
    val isSoundEnabled: Boolean = false
) {
    fun currentScheme(isSystemDark: Boolean): ColorThemeScheme {
        return when (themeMode) {
            ThemeMode.LIGHT -> selectedPalette.lightScheme
            ThemeMode.DARK -> selectedPalette.darkScheme
            ThemeMode.AUTO -> if (isSystemDark) selectedPalette.darkScheme else selectedPalette.lightScheme
        }
    }
}

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val historyManager = HistoryManager(
        RoomHistoryStorage(AppDatabase.getDatabase(application).historyDao())
    )

    private val settingsManager = SettingsManager(application)

    val historyItems: StateFlow<List<HistoryItem>> = historyManager.historyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _uiState: MutableStateFlow<CalculatorUiState>

    init {
        val initialPalette = ThemeRepository.allPalettes.find {
            it.name.equals(settingsManager.selectedPaletteName, ignoreCase = true)
        } ?: ThemeRepository.Orchid

        val initialThemeMode = try {
            ThemeMode.valueOf(settingsManager.themeMode)
        } catch (_: Exception) {
            ThemeMode.AUTO
        }

        _uiState = MutableStateFlow(
            CalculatorUiState(
                calculatorMode = settingsManager.defaultCalculatorMode,
                angleMode = settingsManager.angleMode,
                decimalSeparator = settingsManager.decimalSeparator,
                numberGrouping = settingsManager.numberGrouping,
                isLargeButtonsEnabled = settingsManager.isLargeButtonsEnabled,
                isVibrationEnabled = settingsManager.isVibrationEnabled,
                isSoundEnabled = settingsManager.isSoundEnabled,
                selectedPalette = initialPalette,
                themeMode = initialThemeMode
            )
        )
    }

    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun setCalculatorMode(mode: CalculatorMode) {
        _uiState.update { it.copy(calculatorMode = mode) }
    }

    fun setDefaultCalculatorMode(mode: CalculatorMode) {
        settingsManager.defaultCalculatorMode = mode
        _uiState.update { it.copy(calculatorMode = mode) }
    }

    fun setAngleMode(mode: AngleMode) {
        settingsManager.angleMode = mode
        _uiState.update { current ->
            if (current.isEvaluated && current.expression.isNotBlank()) {
                when (val reEval = CalculatorEngine.evaluate(
                    expression = current.expression,
                    angleMode = mode,
                    decimalSeparator = current.decimalSeparator,
                    numberGrouping = current.numberGrouping
                )) {
                    is EvaluationResult.Success -> current.copy(angleMode = mode, result = reEval.formatted)
                    is EvaluationResult.Error -> current.copy(angleMode = mode, result = reEval.message)
                }
            } else {
                current.copy(angleMode = mode)
            }
        }
    }

    fun toggleAngleMode() {
        val next = if (_uiState.value.angleMode == AngleMode.DEG) AngleMode.RAD else AngleMode.DEG
        setAngleMode(next)
    }

    fun setDecimalSeparator(sep: String) {
        settingsManager.decimalSeparator = sep
        _uiState.update { current ->
            if (current.isEvaluated && current.expression.isNotBlank()) {
                when (val reEval = CalculatorEngine.evaluate(
                    expression = current.expression,
                    angleMode = current.angleMode,
                    decimalSeparator = sep,
                    numberGrouping = current.numberGrouping
                )) {
                    is EvaluationResult.Success -> current.copy(decimalSeparator = sep, result = reEval.formatted)
                    is EvaluationResult.Error -> current.copy(decimalSeparator = sep, result = reEval.message)
                }
            } else {
                current.copy(decimalSeparator = sep)
            }
        }
    }

    fun setNumberGrouping(grouping: String) {
        settingsManager.numberGrouping = grouping
        _uiState.update { current ->
            if (current.isEvaluated && current.expression.isNotBlank()) {
                when (val reEval = CalculatorEngine.evaluate(
                    expression = current.expression,
                    angleMode = current.angleMode,
                    decimalSeparator = current.decimalSeparator,
                    numberGrouping = grouping
                )) {
                    is EvaluationResult.Success -> current.copy(numberGrouping = grouping, result = reEval.formatted)
                    is EvaluationResult.Error -> current.copy(numberGrouping = grouping, result = reEval.message)
                }
            } else {
                current.copy(numberGrouping = grouping)
            }
        }
    }

    fun toggleLargeButtons(enabled: Boolean) {
        settingsManager.isLargeButtonsEnabled = enabled
        _uiState.update { it.copy(isLargeButtonsEnabled = enabled) }
    }

    fun toggleSecondFunction() {
        _uiState.update { it.copy(isSecondFunction = !it.isSecondFunction) }
    }

    fun toggleVibration(enabled: Boolean) {
        settingsManager.isVibrationEnabled = enabled
        _uiState.update { it.copy(isVibrationEnabled = enabled) }
    }

    fun toggleSound(enabled: Boolean) {
        settingsManager.isSoundEnabled = enabled
        _uiState.update { it.copy(isSoundEnabled = enabled) }
    }

    fun onDigit(digit: String) {
        _uiState.update { current ->
            if (current.isEvaluated) {
                current.copy(
                    expression = digit,
                    displayExpression = digit,
                    result = "",
                    isEvaluated = false
                )
            } else {
                val newExpr = if (current.expression == "0") digit else current.expression + digit
                current.copy(
                    expression = newExpr,
                    displayExpression = newExpr,
                    result = "",
                    isEvaluated = false
                )
            }
        }
    }

    fun onDecimal() {
        _uiState.update { current ->
            val sep = current.decimalSeparator
            if (current.isEvaluated) {
                current.copy(
                    expression = "0$sep",
                    displayExpression = "0$sep",
                    result = "",
                    isEvaluated = false
                )
            } else {
                val lastNumber = current.expression.split(Regex("[+\\-×÷%^() ]")).lastOrNull() ?: ""
                if (!lastNumber.contains(".") && !lastNumber.contains(",")) {
                    val append = if (lastNumber.isEmpty() || current.expression.endsWith(" ")) "0$sep" else sep
                    current.copy(
                        expression = current.expression + append,
                        displayExpression = current.displayExpression + append,
                        result = "",
                        isEvaluated = false
                    )
                } else {
                    current
                }
            }
        }
    }

    fun onOperator(op: String) {
        _uiState.update { current ->
            if (current.isEvaluated && current.result.isNotEmpty()) {
                val cleanAns = current.result.replace(",", "").replace(" ", "")
                val newExpr = "$cleanAns $op "
                current.copy(
                    expression = newExpr,
                    displayExpression = newExpr,
                    lastAnswer = cleanAns,
                    result = "",
                    isEvaluated = false
                )
            } else if (current.expression.isEmpty()) {
                val base = if (current.lastAnswer.isNotEmpty()) {
                    "${current.lastAnswer} $op "
                } else {
                    if (op == "-" || op == "−") "-" else "0 $op "
                }
                current.copy(
                    expression = base,
                    displayExpression = base,
                    result = "",
                    isEvaluated = false
                )
            } else {
                val trimmedExpr = current.expression.trimEnd()
                val ops = setOf("+", "-", "−", "×", "÷", "^")
                val lastToken = trimmedExpr.split(" ").lastOrNull() ?: ""

                val newExpr = if (lastToken in ops) {
                    trimmedExpr.dropLast(lastToken.length).trimEnd() + " $op "
                } else {
                    "$trimmedExpr $op "
                }
                current.copy(
                    expression = newExpr,
                    displayExpression = newExpr,
                    result = "",
                    isEvaluated = false
                )
            }
        }
    }

    fun onScientificFunction(fn: String) {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val newExpr = if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) {
                "$expr$fn("
            } else if (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e') {
                "$expr × $fn("
            } else {
                "$expr$fn("
            }
            current.copy(
                expression = newExpr,
                displayExpression = newExpr,
                result = "",
                isEvaluated = false
            )
        }
    }

    fun onPower(pow: String) {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) current.result.replace(",", "").replace(" ", "") else current.expression
            val newExpr = when (pow) {
                "²" -> if (expr.isNotEmpty()) "$expr²" else "0²"
                "³" -> if (expr.isNotEmpty()) "$expr³" else "0³"
                "^" -> if (expr.isNotEmpty()) "$expr ^ " else "0 ^ "
                else -> "$expr$pow"
            }
            current.copy(
                expression = newExpr,
                displayExpression = newExpr,
                result = "",
                isEvaluated = false
            )
        }
    }

    fun onReciprocal() {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) current.result.replace(",", "").replace(" ", "") else current.expression
            val newExpr = if (expr.isNotEmpty()) {
                "1 ÷ ($expr)"
            } else {
                "1 ÷ ("
            }
            current.copy(
                expression = newExpr,
                displayExpression = newExpr,
                result = "",
                isEvaluated = false
            )
        }
    }

    fun onConstant(constant: String) {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val newExpr = if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
                "$expr × $constant"
            } else {
                "$expr$constant"
            }
            current.copy(
                expression = newExpr,
                displayExpression = newExpr,
                result = "",
                isEvaluated = false
            )
        }
    }

    fun onParenthesis(paren: String) {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) "" else current.expression
            val newExpr = if (paren == "(") {
                if (expr.isNotEmpty() && (expr.last().isDigit() || expr.last() == ')' || expr.last() == 'π' || expr.last() == 'e')) {
                    "$expr × ("
                } else {
                    "$expr("
                }
            } else {
                "$expr)"
            }

            current.copy(
                expression = newExpr,
                displayExpression = newExpr,
                result = "",
                isEvaluated = false
            )
        }
    }

    fun onPercentage() {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) current.result.replace(",", "").replace(" ", "") else current.expression
            if (expr.isNotEmpty() && !expr.endsWith(" ") && !expr.endsWith("%") && !expr.endsWith("(")) {
                val newExpr = "$expr%"
                current.copy(
                    expression = newExpr,
                    displayExpression = newExpr,
                    result = "",
                    isEvaluated = false
                )
            } else {
                current
            }
        }
    }

    fun onEE() {
        _uiState.update { current ->
            val expr = if (current.isEvaluated) current.result.replace(",", "").replace(" ", "") else current.expression

            val newExpr = if (expr.isEmpty() || expr.endsWith(" ") || expr.endsWith("(")) {
                "${expr}1E"
            } else if (!expr.endsWith("E") && !expr.endsWith(".")) {
                "${expr}E"
            } else {
                expr
            }

            current.copy(
                expression = newExpr,
                displayExpression = newExpr,
                result = "",
                isEvaluated = false
            )
        }
    }

    fun onToggleSign() {
        _uiState.update { current ->
            val expr = current.expression.trim()
            if (expr.isEmpty()) return@update current
            if (current.isEvaluated && current.result.isNotEmpty()) {
                val clean = current.result.replace(",", "").replace(" ", "")
                val negated = if (clean.startsWith("-")) clean.removePrefix("-") else "-$clean"
                current.copy(
                    expression = negated,
                    displayExpression = negated,
                    result = negated,
                    lastAnswer = negated
                )
            } else {
                val tokens = expr.split(" ").toMutableList()
                if (tokens.isNotEmpty()) {
                    val last = tokens.last()
                    if (last.startsWith("-")) {
                        tokens[tokens.lastIndex] = last.removePrefix("-")
                    } else if (last.isNotEmpty() && last.first().isDigit()) {
                        tokens[tokens.lastIndex] = "-$last"
                    }
                    val updatedExpr = tokens.joinToString(" ")
                    current.copy(
                        expression = updatedExpr,
                        displayExpression = updatedExpr
                    )
                } else {
                    current
                }
            }
        }
    }

    fun onBackspace() {
        _uiState.update { current ->
            if (current.isEvaluated) {
                current.copy(expression = "", displayExpression = "", result = "", isEvaluated = false)
            } else {
                var expr = current.expression
                if (expr.isNotEmpty()) {
                    expr = when {
                        expr.endsWith("sin(") || expr.endsWith("cos(") || expr.endsWith("tan(") ||
                            expr.endsWith("log(") || expr.endsWith("cbrt(") -> expr.dropLast(4)
                        expr.endsWith("asin(") || expr.endsWith("acos(") || expr.endsWith("atan(") ||
                            expr.endsWith("sinh(") || expr.endsWith("cosh(") || expr.endsWith("tanh(") ||
                            expr.endsWith("sqrt(") -> expr.dropLast(5)
                        expr.endsWith("ln(") || expr.endsWith("√(") || expr.endsWith("∛(") -> expr.dropLast(expr.length - expr.lastIndexOfAny(listOf("ln(", "√(", "∛(")))
                        expr.endsWith(" ") -> expr.trimEnd().dropLast(1).trimEnd()
                        else -> expr.dropLast(1)
                    }

                    current.copy(
                        expression = expr,
                        displayExpression = expr,
                        result = ""
                    )
                } else {
                    current.copy(result = "")
                }
            }
        }
    }

    fun onClear() {
        _uiState.update {
            it.copy(
                expression = "",
                displayExpression = "",
                result = "",
                lastAnswer = "",
                previousFormula = "",
                isEvaluated = false
            )
        }
    }

    fun onEquals() {
        val state = _uiState.value
        val currentExpr = state.expression
        if (currentExpr.isBlank()) return

        when (val eval = CalculatorEngine.evaluate(
            expression = currentExpr,
            angleMode = state.angleMode,
            decimalSeparator = state.decimalSeparator,
            numberGrouping = state.numberGrouping
        )) {
            is EvaluationResult.Success -> {
                val cleanRaw = eval.formatted.replace(",", "").replace(" ", "")
                val formattedExpr = CalculatorEngine.formatExpression(
                    expression = currentExpr,
                    decimalSeparator = state.decimalSeparator,
                    numberGrouping = state.numberGrouping
                )
                _uiState.update {
                    it.copy(
                        result = eval.formatted,
                        lastAnswer = cleanRaw,
                        previousFormula = formattedExpr,
                        displayExpression = formattedExpr,
                        isEvaluated = true
                    )
                }

                val modeStr = when (state.calculatorMode) {
                    CalculatorMode.SCIENTIFIC -> "Scientific"
                    CalculatorMode.AGE -> "Age"
                    else -> "Basic"
                }
                val angleStr = if (state.calculatorMode == CalculatorMode.SCIENTIFIC) state.angleMode.name else null

                viewModelScope.launch {
                    historyManager.saveCalculation(
                        expression = currentExpr,
                        result = eval.formatted,
                        calculatorMode = modeStr,
                        angleMode = angleStr
                    )
                }
            }
            is EvaluationResult.Error -> {
                _uiState.update {
                    it.copy(
                        result = eval.message,
                        isEvaluated = true
                    )
                }
            }
        }
    }

    fun setPalette(palette: CalcPalette) {
        settingsManager.selectedPaletteName = palette.name
        _uiState.update { it.copy(selectedPalette = palette) }
    }

    fun setThemeMode(mode: ThemeMode) {
        settingsManager.themeMode = mode.name
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun toggleLightDarkMode(isSystemDark: Boolean = false) {
        val currentlyDark = when (_uiState.value.themeMode) {
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
            ThemeMode.AUTO -> isSystemDark
        }
        val newMode = if (currentlyDark) ThemeMode.LIGHT else ThemeMode.DARK
        setThemeMode(newMode)
    }

    fun toggleHistory(show: Boolean) {
        _uiState.update { it.copy(isHistoryVisible = show) }
    }

    fun deleteHistoryItem(id: Long) {
        viewModelScope.launch {
            historyManager.deleteItem(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            historyManager.clearAll()
        }
    }

    fun onSelectHistoryItem(item: HistoryItem) {
        _uiState.update {
            val cleanResult = item.result.replace(",", "").replace(" ", "")
            it.copy(
                expression = item.expression,
                displayExpression = item.expression,
                previousFormula = item.expression,
                result = item.result,
                lastAnswer = cleanResult,
                isEvaluated = true,
                isHistoryVisible = false
            )
        }
    }
}
