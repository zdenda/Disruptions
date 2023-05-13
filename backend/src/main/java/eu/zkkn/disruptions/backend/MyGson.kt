package eu.zkkn.disruptions.backend

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object MyGson {

    private val gson = lazy {
        GsonBuilder().apply {
            // LocalDateTime serialization
            registerTypeAdapter(
                LocalDateTime::class.java,
                object : JsonSerializer<LocalDateTime?>, JsonDeserializer<LocalDateTime?> {
                    override fun serialize(
                        src: LocalDateTime?,
                        typeOfSrc: Type?,
                        context: JsonSerializationContext?
                    ): JsonElement {
                        return if (src != null) {
                            JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                        } else {
                            JsonNull.INSTANCE
                        }
                    }

                    override fun deserialize(
                        json: JsonElement?,
                        typeOfT: Type?,
                        context: JsonDeserializationContext?
                    ): LocalDateTime? {
                        return if (json != null && !json.isJsonNull) {
                            LocalDateTime.parse(
                                json.asString,
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                            )
                        } else {
                            null
                        }
                    }
                })
            serializeNulls()
        }.create()
    }

    fun get(): Gson = this.gson.value
}