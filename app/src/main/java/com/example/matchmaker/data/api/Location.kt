package com.example.matchmaker.data.api

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

data class Location(
    val street: Street? = null,
    val city: String? = null,
    val state: String? = null,
    val country: String? = null,
    val postcode: String? = null,
    val coordinates: Coordinates? = null,
    val timezone: Timezone? = null
)

data class Street(
    val number: Int? = null,
    val name: String? = null
)

data class Coordinates(
    val latitude: String? = null,
    val longitude: String? = null
)

data class Timezone(
    val offset: String? = null,
    val description: String? = null
)

/**
 * RandomUser API returns postcode as Int or String. This deserializer normalises to String.
 */
class LocationDeserializer : JsonDeserializer<Location> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Location {
        val obj = json.asJsonObject
        val postcodeStr = obj.get("postcode")?.let { postcodeEl ->
            when {
                postcodeEl.isJsonPrimitive && postcodeEl.asJsonPrimitive.isNumber -> postcodeEl.asInt.toString()
                postcodeEl.isJsonPrimitive && postcodeEl.asJsonPrimitive.isString -> postcodeEl.asString
                else -> ""
            }
        } ?: ""
        val street = obj.get("street")?.let { context.deserialize<Street>(it, Street::class.java) }
        val city = obj.get("city")?.takeIf { it.isJsonPrimitive }?.asJsonPrimitive?.asString
        val state = obj.get("state")?.takeIf { it.isJsonPrimitive }?.asJsonPrimitive?.asString
        val country = obj.get("country")?.takeIf { it.isJsonPrimitive }?.asJsonPrimitive?.asString
        val coordinates = obj.get("coordinates")?.let { context.deserialize<Coordinates>(it, Coordinates::class.java) }
        val timezone = obj.get("timezone")?.let { context.deserialize<Timezone>(it, Timezone::class.java) }
        return Location(street = street, city = city, state = state, country = country, postcode = postcodeStr, coordinates = coordinates, timezone = timezone)
    }
}
