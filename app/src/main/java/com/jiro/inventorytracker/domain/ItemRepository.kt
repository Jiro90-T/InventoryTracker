package com.jiro.inventorytracker.domain

import com.jiro.inventorytracker.data.Item
import com.jiro.inventorytracker.data.ItemDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepository @Inject constructor(
    private val itemDao: ItemDao
) {
    fun observeAll(): Flow<List<Item>> = itemDao.observeAll()

    fun observeById(id: Long): Flow<Item?> = itemDao.observeById(id)

    fun search(query: String): Flow<List<Item>> = itemDao.search(query)

    fun filterByCategory(category: String): Flow<List<Item>> = itemDao.filterByCategory(category)

    suspend fun get(id: Long): Item? = itemDao.getById(id)

    suspend fun upsert(item: Item): Long {
        val withTimestamp = item.copy(updatedAt = System.currentTimeMillis())
        return if (item.id == 0L) {
            itemDao.insert(withTimestamp)
        } else {
            itemDao.update(withTimestamp)
            item.id
        }
    }

    suspend fun observeAllOnce(): List<Item> = itemDao.observeAllOnce()
    suspend fun allCategories(): List<String> = itemDao.allCategories()

    suspend fun delete(id: Long) = itemDao.deleteById(id)
}
