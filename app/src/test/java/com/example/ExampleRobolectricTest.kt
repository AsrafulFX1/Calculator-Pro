package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.HistoryItem
import com.example.data.HistoryManager
import com.example.data.RoomHistoryStorage
import com.example.data.SettingsManager
import com.example.model.AngleMode
import com.example.model.CalculatorMode
import com.example.model.ThemeMode
import com.example.model.ThemeRepository
import com.example.viewmodel.CalculatorViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Calculator", appName)
    }

    @Test
    fun `test scientific mode switching preserves calculation`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = CalculatorViewModel(app)

        viewModel.onDigit("1")
        viewModel.onDigit("2")
        Thread.sleep(160)
        viewModel.onOperator("+")
        Thread.sleep(160)
        viewModel.onDigit("5")
        viewModel.onDigit("6")

        assertEquals("12 + 56", viewModel.uiState.value.expression)

        viewModel.setCalculatorMode(CalculatorMode.SCIENTIFIC)
        assertEquals(CalculatorMode.SCIENTIFIC, viewModel.uiState.value.calculatorMode)
        assertEquals("12 + 56", viewModel.uiState.value.expression)

        viewModel.setCalculatorMode(CalculatorMode.AGE)
        assertEquals(CalculatorMode.AGE, viewModel.uiState.value.calculatorMode)

        viewModel.setCalculatorMode(CalculatorMode.BASIC)
        assertEquals(CalculatorMode.BASIC, viewModel.uiState.value.calculatorMode)
        assertEquals("12 + 56", viewModel.uiState.value.expression)
    }

    @Test
    fun `test angle mode toggle in viewmodel`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = CalculatorViewModel(app)

        assertEquals(AngleMode.DEG, viewModel.uiState.value.angleMode)
        viewModel.toggleAngleMode()
        assertEquals(AngleMode.RAD, viewModel.uiState.value.angleMode)
        viewModel.toggleAngleMode()
        assertEquals(AngleMode.DEG, viewModel.uiState.value.angleMode)
    }

    @Test
    fun `test history manager saves, loads, deletes and clears`() = runBlocking {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val database = AppDatabase.getDatabase(app)
        val storage = RoomHistoryStorage(database.historyDao())
        val manager = HistoryManager(storage)

        manager.clearAll()

        manager.saveCalculation("10 + 20", "30", "Basic", null)
        manager.saveCalculation("25 × 40", "1,000", "Basic", null)
        manager.saveCalculation("100 ÷ 4", "25", "Basic", null)

        manager.saveCalculation("sin(90)", "1", "Scientific", "DEG")
        manager.saveCalculation("√(144)", "12", "Scientific", "DEG")
        manager.saveCalculation("2 ^ 10", "1,024", "Scientific", "DEG")

        val list = manager.historyFlow.first()
        assertEquals(6, list.size)

        assertEquals("2 ^ 10", list[0].expression)
        assertEquals("1,024", list[0].result)
        assertEquals("Scientific", list[0].calculatorMode)
        assertEquals("DEG", list[0].angleMode)

        val itemToDelete = list[0]
        manager.deleteItem(itemToDelete.id)

        val afterDelete = manager.historyFlow.first()
        assertEquals(5, afterDelete.size)
        assertTrue(afterDelete.none { it.id == itemToDelete.id })

        manager.clearAll()
        val afterClear = manager.historyFlow.first()
        assertEquals(0, afterClear.size)
    }

    @Test
    fun `test selecting history item loads back into calculator`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = CalculatorViewModel(app)

        val historyItem = HistoryItem(
            id = 42,
            expression = "25 × 40",
            result = "1,000",
            calculatorMode = "Basic"
        )

        viewModel.onSelectHistoryItem(historyItem)

        assertEquals("25 × 40", viewModel.uiState.value.expression)
        assertEquals("1,000", viewModel.uiState.value.result)
        assertEquals("1000", viewModel.uiState.value.lastAnswer)
        assertTrue(viewModel.uiState.value.isEvaluated)

        Thread.sleep(160)
        viewModel.onOperator("+")
        assertEquals("1000 + ", viewModel.uiState.value.expression)
    }

    @Test
    fun `test settings persistence and behavior`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val settingsManager = SettingsManager(app)

        // 1. Change Default Calculator to Scientific
        settingsManager.defaultCalculatorMode = CalculatorMode.SCIENTIFIC
        assertEquals(CalculatorMode.SCIENTIFIC, settingsManager.defaultCalculatorMode)

        // 2. Change Angle Mode to RAD
        settingsManager.angleMode = AngleMode.RAD
        assertEquals(AngleMode.RAD, settingsManager.angleMode)

        // 3. Change Decimal Separator to Comma
        settingsManager.decimalSeparator = ","
        assertEquals(",", settingsManager.decimalSeparator)

        // 4. Change Number Grouping to Dot
        settingsManager.numberGrouping = "Dot"
        assertEquals("Dot", settingsManager.numberGrouping)

        // 5. Toggle Large Buttons
        settingsManager.isLargeButtonsEnabled = true
        assertTrue(settingsManager.isLargeButtonsEnabled)

        // 6. Toggle Haptics and Sound
        settingsManager.isVibrationEnabled = false
        assertFalse(settingsManager.isVibrationEnabled)
        settingsManager.isSoundEnabled = true
        assertTrue(settingsManager.isSoundEnabled)

        // 7. Select Theme
        settingsManager.selectedPaletteName = ThemeRepository.Mint.name
        settingsManager.themeMode = ThemeMode.DARK.name

        // Simulate app restart by instantiating a new ViewModel
        val newVm = CalculatorViewModel(app)
        val state = newVm.uiState.value

        assertEquals(CalculatorMode.SCIENTIFIC, state.calculatorMode)
        assertEquals(AngleMode.RAD, state.angleMode)
        assertEquals(",", state.decimalSeparator)
        assertEquals("Dot", state.numberGrouping)
        assertTrue(state.isLargeButtonsEnabled)
        assertFalse(state.isVibrationEnabled)
        assertTrue(state.isSoundEnabled)
        assertEquals(ThemeRepository.Mint.name, state.selectedPalette.name)
        assertEquals(ThemeMode.DARK, state.themeMode)
    }
}
