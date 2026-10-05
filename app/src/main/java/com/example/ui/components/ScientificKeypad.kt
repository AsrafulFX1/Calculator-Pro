package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AngleMode
import com.example.model.ColorThemeScheme

@Composable
fun ScientificKeypad(
    scheme: ColorThemeScheme,
    angleMode: AngleMode,
    isSecondFunction: Boolean,
    isVibrationEnabled: Boolean,
    isSoundEnabled: Boolean,
    onDigit: (String) -> Unit,
    onDecimal: () -> Unit,
    onOperator: (String) -> Unit,
    onEquals: () -> Unit,
    onClear: () -> Unit,
    onBackspace: () -> Unit,
    onParenthesis: (String) -> Unit,
    onPercentage: () -> Unit,
    onScientificFunction: (String) -> Unit,
    onPower: (String) -> Unit,
    onReciprocal: () -> Unit,
    onConstant: (String) -> Unit,
    onToggleAngleMode: () -> Unit,
    onToggleSecondFunction: () -> Unit,
    isLargeButtonsEnabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val buttonAspectRatio = if (isLargeButtonsEnabled) 1.20f else 1.30f
    val buttonCornerRadius = 18.dp

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        // ========================================================
        // ROW 1: [ 2nd ] [ DEG/RAD ] [ sin/sin⁻¹ ] [ cos/cos⁻¹ ] [ tan/tan⁻¹ ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = "2nd",
                backgroundColor = if (isSecondFunction) scheme.accentButtonBackground else scheme.operatorButtonBackground,
                contentColor = if (isSecondFunction) scheme.accentButtonText else scheme.operatorButtonText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_2nd",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onToggleSecondFunction
            )
            CalcButton(
                text = if (angleMode == AngleMode.DEG) "DEG" else "RAD",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_angle_mode",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onToggleAngleMode
            )
            CalcButton(
                text = if (isSecondFunction) "sin⁻¹" else "sin",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = if (isSecondFunction) 14.sp else 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_sin",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onScientificFunction(if (isSecondFunction) "sin⁻¹" else "sin") }
            )
            CalcButton(
                text = if (isSecondFunction) "cos⁻¹" else "cos",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = if (isSecondFunction) 14.sp else 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_cos",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onScientificFunction(if (isSecondFunction) "cos⁻¹" else "cos") }
            )
            CalcButton(
                text = if (isSecondFunction) "tan⁻¹" else "tan",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = if (isSecondFunction) 14.sp else 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_tan",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onScientificFunction(if (isSecondFunction) "tan⁻¹" else "tan") }
            )
        }

        // ========================================================
        // ROW 2: [ ln ] [ log ] [ √ / ∛ ] [ xʸ ] [ 1/x ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = "ln",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_ln",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onScientificFunction("ln") }
            )
            CalcButton(
                text = "log",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_log",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onScientificFunction("log") }
            )
            CalcButton(
                text = if (isSecondFunction) "∛" else "√",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 18.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_root",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onScientificFunction(if (isSecondFunction) "∛" else "√") }
            )
            CalcButton(
                text = "xʸ",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_power_y",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onPower("^") }
            )
            CalcButton(
                text = "1/x",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 15.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_reciprocal",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onReciprocal
            )
        }

        // ========================================================
        // ROW 3: [ π / sinh ] [ e / cosh ] [ x² / tanh ] [ x³ / ∛ ] [ ÷ ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = if (isSecondFunction) "sinh" else "π",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = if (isSecondFunction) 14.sp else 18.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_pi_sinh",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isSecondFunction) onScientificFunction("sinh") else onConstant("π")
                }
            )
            CalcButton(
                text = if (isSecondFunction) "cosh" else "e",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = if (isSecondFunction) 14.sp else 18.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_e_cosh",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isSecondFunction) onScientificFunction("cosh") else onConstant("e")
                }
            )
            CalcButton(
                text = if (isSecondFunction) "tanh" else "x²",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = if (isSecondFunction) 14.sp else 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_sq_tanh",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = {
                    if (isSecondFunction) onScientificFunction("tanh") else onPower("²")
                }
            )
            CalcButton(
                text = "x³",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 16.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_cube",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onPower("³") }
            )
            CalcButton(
                text = "÷",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 24.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_divide",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onOperator("÷") }
            )
        }

        // ========================================================
        // ROW 4: [ C ] [ ⌫ ] [ ( ] [ ) ] [ × ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = "C",
                backgroundColor = scheme.accentButtonBackground,
                contentColor = scheme.accentButtonText,
                fontSize = 20.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_clear",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onClear
            )
            CalcButton(
                text = "",
                backgroundColor = scheme.accentButtonBackground,
                contentColor = scheme.accentButtonText,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_backspace",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Backspace",
                        tint = scheme.accentButtonText,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = onBackspace
            )
            CalcButton(
                text = "(",
                backgroundColor = scheme.splitParenBackground,
                contentColor = scheme.splitParenText,
                fontSize = 20.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_open_paren",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onParenthesis("(") }
            )
            CalcButton(
                text = ")",
                backgroundColor = scheme.splitParenBackground,
                contentColor = scheme.splitParenText,
                fontSize = 20.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_close_paren",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onParenthesis(")") }
            )
            CalcButton(
                text = "×",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 24.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_multiply",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onOperator("×") }
            )
        }

        // ========================================================
        // ROW 5: [ 7 ] [ 8 ] [ 9 ] [ % ] [ − ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = "7",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_7",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("7") }
            )
            CalcButton(
                text = "8",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_8",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("8") }
            )
            CalcButton(
                text = "9",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_9",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("9") }
            )
            CalcButton(
                text = "%",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 20.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_percent",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onPercentage
            )
            CalcButton(
                text = "−",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 24.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_subtract",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onOperator("−") }
            )
        }

        // ========================================================
        // ROW 6: [ 4 ] [ 5 ] [ 6 ] [ . ] [ + ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = "4",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_4",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("4") }
            )
            CalcButton(
                text = "5",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_5",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("5") }
            )
            CalcButton(
                text = "6",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_6",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("6") }
            )
            CalcButton(
                text = "0",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_0",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("0") }
            )
            CalcButton(
                text = "+",
                backgroundColor = scheme.operatorButtonBackground,
                contentColor = scheme.operatorButtonText,
                fontSize = 24.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_add",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onOperator("+") }
            )
        }

        // ========================================================
        // ROW 7: [ 1 ] [ 2 ] [ 3 ] [ . ] [ = ]
        // ========================================================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            CalcButton(
                text = "1",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_1",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("1") }
            )
            CalcButton(
                text = "2",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_2",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("2") }
            )
            CalcButton(
                text = "3",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 22.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_3",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = { onDigit("3") }
            )
            CalcButton(
                text = ".",
                backgroundColor = scheme.numberButtonBackground,
                contentColor = scheme.numberButtonText,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_dot",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onDecimal
            )
            CalcButton(
                text = "=",
                backgroundColor = scheme.accentButtonBackground,
                contentColor = scheme.accentButtonText,
                fontSize = 26.sp,
                aspectRatio = buttonAspectRatio,
                cornerRadius = buttonCornerRadius,
                tag = "btn_sci_equals",
                isVibrationEnabled = isVibrationEnabled,
                isSoundEnabled = isSoundEnabled,
                modifier = Modifier.weight(1f),
                onClick = onEquals
            )
        }
    }
}
