package com.example.homeassistantapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeAssistantState(
    @SerialName("entity_id")
    val entityId: String,
    val state: String,
    val attributes: Map<String, kotlinx.serialization.json.JsonElement> = emptyMap(),
    @SerialName("last_changed")
    val lastChanged: String = "",
    @SerialName("last_updated")
    val lastUpdated: String = ""
)

@Serializable
data class ServiceCall(
    val domain: String,
    val service: String,
    @SerialName("service_data")
    val serviceData: Map<String, String>? = null
)

sealed class Entity {
    abstract val id: String
    abstract val name: String
    abstract val state: String
    
    data class Light(
        override val id: String,
        override val name: String,
        override val state: String,
        val isOn: Boolean
    ) : Entity()
    
    data class Sensor(
        override val id: String,
        override val name: String,
        override val state: String,
        val unit: String?
    ) : Entity()
    
    data class Switch(
        override val id: String,
        override val name: String,
        override val state: String,
        val isOn: Boolean
    ) : Entity()
}

fun HomeAssistantState.toEntity(): Entity? {
    return when {
        entityId.startsWith("light.") -> Entity.Light(
            id = entityId,
            name = entityId.substringAfter("light.").replace("_", " ").replaceFirstChar { it.uppercase() },
            state = state,
            isOn = state == "on"
        )
        entityId.startsWith("sensor.") -> Entity.Sensor(
            id = entityId,
            name = entityId.substringAfter("sensor.").replace("_", " ").replaceFirstChar { it.uppercase() },
            state = state,
            unit = attributes["unit_of_measurement"]?.toString()?.trim('"')
        )
        entityId.startsWith("switch.") -> Entity.Switch(
            id = entityId,
            name = entityId.substringAfter("switch.").replace("_", " ").replaceFirstChar { it.uppercase() },
            state = state,
            isOn = state == "on"
        )
        else -> null
    }
}
