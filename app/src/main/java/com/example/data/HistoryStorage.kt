package com.example.data

import kotlinx.coroutines.flow.Flow

interface HistoryStorage {
    fun getAllHistory(): Flow<List<HistoryItem>>
    suspend fun save(item: HistoryItem)
    suspend fun delete(id: Long)
    suspend fun clear()
}

class RoomHistoryStorage(private val historyDao: HistoryDao) : HistoryStorage {
    override fun getAllHistory(): Flow<List<HistoryItem>> = historyDao.getAllHistory()

    override suspend fun save(item: HistoryItem) {
        historyDao.insert(item)
    }

    override suspend fun delete(id: Long) {
        historyDao.deleteById(id)
    }

    override suspend fun clear() {
        historyDao.clearAll()
    }
}
