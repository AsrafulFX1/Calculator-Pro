package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.CalculatorEngine
import com.example.model.ColorThemeScheme

@Composable
fun CalculatorDisplaySection(
    expression: String,
    displayExpression: String,
    previousFormula: String,
    result: String,
    isEvaluated: Boolean,
    decimalSeparator: String = ".",
    numberGrouping: String = "Comma",
    scheme: ColorThemeScheme,
    modifier: Modifier = Modifier
) {
    val resultScrollState = rememberScrollState()
    val exprScrollState = rememberScrollState()

    // Formatted real-time expression with thousand group separators (e.g. 1,000,000)
    val formattedDisplay = CalculatorEngine.formatExpression(
        expression = expression,
        decimalSeparator = decimalSeparator,
        numberGrouping = numberGrouping
    )
    val tightFormattedDisplay = tightenOperatorSpacing(formattedDisplay)

    // Formula to display at the top pill when evaluated:
    // e.g. "Ans + 56" or "1,000 + 56" as in reference screenshot WA0007
    val pillText = if (previousFormula.isNotBlank()) {
        tightenOperatorSpacing(previousFormula)
    } else {
        tightFormattedDisplay.ifEmpty { tightenOperatorSpacing(displayExpression) }
    }

    // ========================================================
    // MAIN LARGE DISPLAY AREA:
    // USER DIRECTIVE:
    // "Jab Main koi bhi number type karo agar hajar ke upar jata hai to number group mein ho (1,000)"
    // 1. Hisab lagate waqt (isEvaluated == false):
    //    Jo bhi dabaya (numbers, plus, minus), real-time comma grouping ke sath show hoga!
    //    Space around operators is reduced for tight, elegant spacing.
    // 2. Equal dabane par (isEvaluated == true):
    //    Final calculated result main display me right-aligned aayega!
    // ========================================================
    val mainText: String = if (isEvaluated) {
        result.ifEmpty { "0" }
    } else {
        tightFormattedDisplay.ifEmpty { "0" }
    }

    LaunchedEffect(mainText) {
        if (mainText.isNotEmpty()) {
            resultScrollState.animateScrollTo(resultScrollState.maxValue)
        }
    }

    LaunchedEffect(pillText) {
        if (pillText.isNotEmpty()) {
            exprScrollState.animateScrollTo(exprScrollState.maxValue)
        }
    }

    // Dynamic font size scaling:
    val mainFontSize = when {
        mainText.length <= 6 -> 82.sp
        mainText.length <= 9 -> 64.sp
        mainText.length <= 13 -> 50.sp
        mainText.length <= 17 -> 40.sp
        else -> 32.sp
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.Start
    ) {
        // ========================================================
        // 1. UPPER LINE (Pill Container on Left: e.g. "Ans + 56")
        // Exact match to reference screenshot IMG-20261003-WA0007.jpg
        // Visible after '=' has evaluated the calculation!
        // ========================================================
        val isPillVisible = isEvaluated && pillText.isNotBlank() && pillText != "0"

        AnimatedVisibility(
            visible = isPillVisible,
            enter = fadeIn(tween(250)) + slideInVertically(
                animationSpec = spring(dampingRatio = 0.75f, stiffness = 400f)
            ) { it },
            exit = fadeOut(tween(150)) + slideOutVertically { -it / 2 }
        ) {
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(RoundedCornerShape(50))
                    .background(scheme.expressionPillBackground)
                    .horizontalScroll(exprScrollState)
                    .padding(horizontal = 20.dp, vertical = 9.dp)
                    .testTag("expression_pill_animated"),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = pillText,
                    color = scheme.expressionTextColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    maxLines = 1
                )
            }
        }

        // ========================================================
        // 2. MAIN LARGE DISPLAY AREA (Right-Aligned: e.g. 156 or 0)
        // Everything pressed (+, -, numbers) appears DIRECTLY here!
        // ========================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 96.dp)
                .horizontalScroll(resultScrollState),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = mainText,
                color = scheme.resultTextColor,
                fontSize = mainFontSize,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_display_text")
            )
        }
    }
}

/**
 * Reduces the wide standard space around operators (+, -, ×, ÷, etc.) to a
 * clean narrow no-break space (U+202F) so numbers and operators remain close
 * and readable without excessive spacing at large font sizes.
 */
fun tightenOperatorSpacing(text: String): String {
    if (text.isEmpty()) return text
    return text
        .replace(Regex(" (?=[+−\\-×÷^])"), "\u202F")
        .replace(Regex("(?<=[+−\\-×÷^]) "), "\u202F")
}

