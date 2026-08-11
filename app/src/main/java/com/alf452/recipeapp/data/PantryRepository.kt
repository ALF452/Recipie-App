package com.alf452.recipeapp.data

import kotlinx.coroutines.flow.Flow

class PantryRepository(private val dao: PantryDao) {

    val allItems: Flow<List<PantryItem>> = dao.getAllPantryItems()

    suspend fun addItem(name: String) {
        dao.insertPantryItem(PantryItem(name = name))
    }

    suspend fun deleteItem(item: PantryItem) {
        dao.deletePantryItem(item)
    }
}
