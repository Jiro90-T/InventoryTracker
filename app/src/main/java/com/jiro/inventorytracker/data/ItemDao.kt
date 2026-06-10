package com.jiro.inventorytracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    @Query("SELECT * FROM items ORDER BY updated_at DESC")
    fun observeAll(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<Item?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Item?

    @Query("""
        SELECT * FROM items
        WHERE name LIKE '%' || :query || '%'
           OR category LIKE '%' || :query || '%'
           OR barcode LIKE '%' || :query || '%'
           OR location LIKE '%' || :query || '%'
        ORDER BY updated_at DESC
    """)
    fun search(query: String): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE category = :category ORDER BY updated_at DESC")
    fun filterByCategory(category: String): Flow<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("DELETE FROM items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM items")
    suspend fun count(): Int

    @Query("SELECT * FROM items ORDER BY updated_at DESC")
    suspend fun observeAllOnce(): List<Item>

    @Query("SELECT DISTINCT category FROM items WHERE category IS NOT NULL AND category != '' ORDER BY category")
    suspend fun allCategories(): List<String>

    @Query("SELECT category, COUNT(*) as cnt FROM items WHERE category IS NOT NULL AND category != '' GROUP BY category ORDER BY cnt DESC")
    fun categoryCounts(): Flow<List<CategoryCount>>

    data class CategoryCount(val category: String, val cnt: Int)
}
