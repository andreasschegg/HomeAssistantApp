package com.example.homeassistantapp.data.repository

import com.example.homeassistantapp.data.api.HomeAssistantApi
import com.example.homeassistantapp.data.model.Entity
import com.example.homeassistantapp.data.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HomeAssistantRepository(
    private val api: HomeAssistantApi
) {
    fun getEntities(): Flow<Result<List<Entity>>> = flow {
        val result = api.getStates()
        emit(result.map { states ->
            states.mapNotNull { it.toEntity() }
        })
    }

    fun getEntity(entityId: String): Flow<Result<Entity?>> = flow {
        val result = api.getState(entityId)
        emit(result.map { it.toEntity() })
    }

    suspend fun toggleEntity(entityId: String): Result<Unit> {
        return api.toggle(entityId)
    }

    suspend fun turnOn(entityId: String): Result<Unit> {
        return api.turnOn(entityId)
    }

    suspend fun turnOff(entityId: String): Result<Unit> {
        return api.turnOff(entityId)
    }
}
