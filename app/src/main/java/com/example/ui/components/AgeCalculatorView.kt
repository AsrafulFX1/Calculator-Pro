package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorThemeScheme
import java.text.DecimalFormat
import java.util.Calendar

data class AgeCalculation(
    val years: Int,
    val months: Int,
    val days: Int,
    val totalMonths: Long,
    val totalWeeks: Long,
    val totalDays: Long,
    val totalHours: Long,
    val totalMinutes: Long,
    val nextBirthdayMonths: Int,
    val nextBirthdayDays: Int,
    val nextBirthdayDayOfWeek: String,
    val isBirthdayToday: Boolean,
    val isValid: Boolean,
    val errorMessage: String? = null
)

/**
 * VisualTransformation that dynamically displays slashes ('/') after DD and MM
 * while keeping the underlying TextField value as pure digits.
 * This guarantees the cursor never jumps or glitches when typing after the slash.
 */
class DateVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 8) text.text.substring(0, 8) else text.text
        val out = buildString {
            for (i in trimmed.indices) {
                append(trimmed[i])
                if (i == 1 || i == 3) {
                    append("/")
                }
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, trimmed.length)
                return when {
                    clamped <= 1 -> clamped
                    clamped <= 3 -> (clamped + 1).coerceAtMost(out.length)
                    else -> (clamped + 2).coerceAtMost(out.length)
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, out.length)
                return when {
                    clamped <= 2 -> clamped
                    clamped <= 5 -> (clamped - 1).coerceAtMost(trimmed.length)
                    else -> (clamped - 2).coerceAtMost(trimmed.length)
                }
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}

fun formatWithAutoSlash(newText: String, oldText: String): String {
    val isDeleting = newText.length < oldText.length
    val cleanDigits = if (isDeleting && oldText.endsWith("/") && !newText.endsWith("/")) {
        newText.filter { it.isDigit() }.dropLast(1)
    } else {
        newText.filter { it.isDigit() }.take(8)
    }

    return buildString {
        for (i in cleanDigits.indices) {
            append(cleanDigits[i])
            if (i == 1 || i == 3) {
                append("/")
            }
        }
    }
}

fun parseDateInput(input: String): Triple<Int, Int, Int>? {
    val clean = input.trim()
    val parts = clean.split("/")
    if (parts.size == 3 && parts[2].length == 4) {
        val d = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        val y = parts[2].toIntOrNull() ?: return null
        if (d in 1..31 && m in 1..12 && y in 1900..2100) {
            return Triple(y, m, d)
        }
    }
    return null
}

fun parseDigitsDate(digits: String): Triple<Int, Int, Int>? {
    if (digits.length == 8) {
        val d = digits.substring(0, 2).toIntOrNull() ?: return null
        val m = digits.substring(2, 4).toIntOrNull() ?: return null
        val y = digits.substring(4, 8).toIntOrNull() ?: return null
        if (d in 1..31 && m in 1..12 && y in 1900..2100) {
            return Triple(y, m, d)
        }
    }
    return null
}

fun calculateDetailedAge(
    bYear: Int, bMonth: Int, bDay: Int, // 1-based month (1..12)
    tYear: Int, tMonth: Int, tDay: Int
): AgeCalculation {
    val birthCal = Calendar.getInstance().apply {
        set(bYear, bMonth - 1, bDay, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val targetCal = Calendar.getInstance().apply {
        set(tYear, tMonth - 1, tDay, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }

    if (birthCal.after(targetCal)) {
        return AgeCalculation(
            years = 0, months = 0, days = 0,
            totalMonths = 0, totalWeeks = 0, totalDays = 0,
            totalHours = 0, totalMinutes = 0,
            nextBirthdayMonths = 0, nextBirthdayDays = 0,
            nextBirthdayDayOfWeek = "",
            isBirthdayToday = false,
            isValid = false,
            errorMessage = "Date of birth cannot be after the target date."
        )
    }

    var years = tYear - bYear
    var months = tMonth - bMonth
    var days = tDay - bDay

    if (days < 0) {
        months -= 1
        val prevMonthCal = Calendar.getInstance().apply {
            set(tYear, tMonth - 2, 1)
        }
        val daysInPrevMonth = prevMonthCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        days += daysInPrevMonth
    }

    if (months < 0) {
        years -= 1
        months += 12
    }

    val diffMillis = targetCal.timeInMillis - birthCal.timeInMillis
    val totalDays = if (diffMillis > 0) diffMillis / (1000 * 60 * 60 * 24) else 0L
    val totalWeeks = totalDays / 7
    val totalMonths = (years * 12L) + months
    val totalHours = totalDays * 24
    val totalMinutes = totalHours * 60

    // Next Birthday calculation
    var nextBdayYear = tYear
    val bdayThisYear = Calendar.getInstance().apply {
        set(tYear, bMonth - 1, bDay, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val isBirthdayToday = (bMonth == tMonth && bDay == tDay)

    if (bdayThisYear.before(targetCal)) {
        nextBdayYear += 1
    }
    val nextBdayCal = Calendar.getInstance().apply {
        set(nextBdayYear, bMonth - 1, bDay, 0, 0, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val dayNames = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val nextBdayDayOfWeek = dayNames[nextBdayCal.get(Calendar.DAY_OF_WEEK) - 1]

    var nbMonths = nextBdayCal.get(Calendar.MONTH) - targetCal.get(Calendar.MONTH)
    var nbDays = nextBdayCal.get(Calendar.DAY_OF_MONTH) - targetCal.get(Calendar.DAY_OF_MONTH)

    if (nbDays < 0) {
        nbMonths -= 1
        val prevMonthDays = targetCal.getActualMaximum(Calendar.DAY_OF_MONTH)
        nbDays += prevMonthDays
    }
    if (nbMonths < 0) {
        nbMonths += 12
    }

    return AgeCalculation(
        years = maxOf(0, years),
        months = maxOf(0, months),
        days = maxOf(0, days),
        totalMonths = totalMonths,
        totalWeeks = totalWeeks,
        totalDays = totalDays,
        totalHours = totalHours,
        totalMinutes = totalMinutes,
        nextBirthdayMonths = maxOf(0, nbMonths),
        nextBirthdayDays = maxOf(0, nbDays),
        nextBirthdayDayOfWeek = nextBdayDayOfWeek,
        isBirthdayToday = isBirthdayToday,
        isValid = true
    )
}

@Composable
fun AgeCalculatorView(
    scheme: ColorThemeScheme,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentToday = remember { Calendar.getInstance() }
    val initialTargetDigits = remember {
        val d = currentToday.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
        val m = (currentToday.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
        val y = currentToday.get(Calendar.YEAR).toString()
        "$d$m$y"
    }

    // Pure 8-digit representations (DDMMYYYY) - no cursor desynchronization!
    var birthDigits by remember { mutableStateOf("01012000") }
    var targetDigits by remember { mutableStateOf(initialTargetDigits) }

    val ageResult by remember(birthDigits, targetDigits) {
        derivedStateOf {
            val b = parseDigitsDate(birthDigits)
            val t = parseDigitsDate(targetDigits)
            if (b != null && t != null) {
                calculateDetailedAge(
                    bYear = b.first, bMonth = b.second, bDay = b.third,
                    tYear = t.first, tMonth = t.second, tDay = t.third
                )
            } else {
                AgeCalculation(
                    years = 0, months = 0, days = 0,
                    totalMonths = 0, totalWeeks = 0, totalDays = 0,
                    totalHours = 0, totalMinutes = 0,
                    nextBirthdayMonths = 0, nextBirthdayDays = 0,
                    nextBirthdayDayOfWeek = "",
                    isBirthdayToday = false,
                    isValid = false,
                    errorMessage = "Please enter complete date (DD/MM/YYYY)"
                )
            }
        }
    }

    val numberFormatter = remember { DecimalFormat("#,###") }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Date Selection / Input Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Date of Birth Input Field (Masked with DateVisualTransformation)
            DateInputField(
                title = "Date of Birth",
                digitsValue = birthDigits,
                onDigitsChange = { newDigits ->
                    birthDigits = newDigits
                },
                icon = Icons.Default.CalendarMonth,
                scheme = scheme,
                modifier = Modifier.weight(1f),
                onCalendarClick = {
                    val parsed = parseDigitsDate(birthDigits) ?: Triple(2000, 1, 1)
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val dd = d.toString().padStart(2, '0')
                            val mm = (m + 1).toString().padStart(2, '0')
                            birthDigits = "$dd$mm$y"
                        },
                        parsed.first,
                        parsed.second - 1,
                        parsed.third
                    ).show()
                }
            )

            // Today / Target Date Input Field
            DateInputField(
                title = "Today / As of",
                digitsValue = targetDigits,
                onDigitsChange = { newDigits ->
                    targetDigits = newDigits
                },
                icon = Icons.Default.CalendarMonth,
                scheme = scheme,
                modifier = Modifier.weight(1f),
                onCalendarClick = {
                    val parsed = parseDigitsDate(targetDigits) ?: Triple(
                        currentToday.get(Calendar.YEAR),
                        currentToday.get(Calendar.MONTH) + 1,
                        currentToday.get(Calendar.DAY_OF_MONTH)
                    )
                    DatePickerDialog(
                        context,
                        { _, y, m, d ->
                            val dd = d.toString().padStart(2, '0')
                            val mm = (m + 1).toString().padStart(2, '0')
                            targetDigits = "$dd$mm$y"
                        },
                        parsed.first,
                        parsed.second - 1,
                        parsed.third
                    ).show()
                }
            )
        }

        if (!ageResult.isValid) {
            // Warning or Helper message
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(scheme.accentButtonBackground.copy(alpha = 0.12f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = ageResult.errorMessage ?: "Please enter complete date (DD/MM/YYYY)",
                    color = scheme.accentButtonBackground,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            // HERO AGE CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(scheme.accentButtonBackground)
                    .padding(20.dp)
                    .testTag("card_primary_age")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (ageResult.isBirthdayToday) "🎉 Happy Birthday! 🎉" else "YOUR CURRENT AGE",
                        color = scheme.accentButtonText.copy(alpha = 0.85f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${ageResult.years} Years",
                        color = scheme.accentButtonText,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${ageResult.months} Months | ${ageResult.days} Days",
                        color = scheme.accentButtonText.copy(alpha = 0.92f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // NEXT BIRTHDAY CARD
            val cardBg = if (scheme.isDark) Color(0xFF202020) else Color(0xFFF7F7F7)
            val cardStroke = if (scheme.isDark) Color(0xFF333333) else Color(0xFFE2E2E2)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(cardBg)
                    .border(1.dp, cardStroke, RoundedCornerShape(18.dp))
                    .padding(16.dp)
                    .testTag("card_next_birthday")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(scheme.accentButtonBackground.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = scheme.accentButtonBackground,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Next Birthday",
                                color = scheme.expressionTextColor.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (ageResult.isBirthdayToday) "Today!" else "${ageResult.nextBirthdayMonths}m ${ageResult.nextBirthdayDays}d left",
                                color = scheme.expressionTextColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (ageResult.nextBirthdayDayOfWeek.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(scheme.splitParenBackground)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = ageResult.nextBirthdayDayOfWeek,
                                color = scheme.splitParenText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // DETAILED SUMMARY STATISTICS
            Text(
                text = "Summary Statistics",
                color = scheme.expressionTextColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )

            // Statistics Grid (2 columns)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Total Months",
                        value = numberFormatter.format(ageResult.totalMonths),
                        scheme = scheme,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Weeks",
                        value = numberFormatter.format(ageResult.totalWeeks),
                        scheme = scheme,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Total Days",
                        value = numberFormatter.format(ageResult.totalDays),
                        scheme = scheme,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Total Hours",
                        value = numberFormatter.format(ageResult.totalHours),
                        scheme = scheme,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "Total Minutes",
                        value = numberFormatter.format(ageResult.totalMinutes),
                        scheme = scheme,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Years Lived",
                        value = "${ageResult.years} yrs",
                        scheme = scheme,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun DateInputField(
    title: String,
    digitsValue: String,
    onDigitsChange: (String) -> Unit,
    icon: ImageVector,
    scheme: ColorThemeScheme,
    modifier: Modifier = Modifier,
    onCalendarClick: () -> Unit
) {
    val cardBg = if (scheme.isDark) Color(0xFF202020) else Color(0xFFF7F7F7)
    val cardStroke = if (scheme.isDark) Color(0xFF333333) else Color(0xFFE2E2E2)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, cardStroke, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = scheme.expressionTextColor.copy(alpha = 0.65f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCalendarClick() }
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Open Calendar",
                        tint = scheme.accentButtonBackground,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            BasicTextField(
                value = digitsValue,
                onValueChange = { input ->
                    val clean = input.filter { it.isDigit() }.take(8)
                    onDigitsChange(clean)
                },
                singleLine = true,
                visualTransformation = DateVisualTransformation(),
                textStyle = TextStyle(
                    color = scheme.expressionTextColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                ),
                cursorBrush = SolidColor(scheme.accentButtonBackground),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (digitsValue.isEmpty()) {
                            Text(
                                text = "DD/MM/YYYY",
                                color = scheme.expressionTextColor.copy(alpha = 0.35f),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    scheme: ColorThemeScheme,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(scheme.numberButtonBackground)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                color = scheme.numberButtonText.copy(alpha = 0.65f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = scheme.numberButtonText,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
