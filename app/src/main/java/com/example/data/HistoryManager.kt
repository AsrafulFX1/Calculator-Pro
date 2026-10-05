package com.example.data

import kotlinx.coroutines.flow.Flow

class HistoryManager(private val storage: HistoryStorage) {

    val historyFlow: Flow<List<HistoryItem>> = storage.getAllHistory()

    suspend fun saveCalculation(
        expression: String,
        result: String,
        calculatorMode: String = "Basic",
        angleMode: String? = null
    ) {
        if (expression.isNotBlank() && result.isNotBlank()) {
            val item = HistoryItem(
                expression = expression.trim(),
                result = result.trim(),
                timestamp = System.currentTimeMillis(),
                calculatorMode = calculatorMode,
                angleMode = angleMode
            )
            storage.save(item)
        }
    }

    suspend fun deleteItem(id: Long) {
        storage.delete(id)
    }

    suspend fun clearAll() {
        storage.clear()
    }
}
