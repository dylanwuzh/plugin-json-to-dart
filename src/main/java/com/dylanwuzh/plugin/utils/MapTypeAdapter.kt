package com.dylanwuzh.plugin.utils

import com.google.gson.TypeAdapter
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.lang.IllegalStateException
import java.util.ArrayList

class MapTypeAdapter : TypeAdapter<Any?>() {

    @Throws(IOException::class)
    override fun read(reader: JsonReader): Any? {
        return when (reader.peek()) {
            JsonToken.BEGIN_ARRAY -> {
                val list: MutableList<Any?> = ArrayList()
                reader.beginArray()
                while (reader.hasNext()) {
                    list.add(read(reader))
                }
                reader.endArray()
                list
            }
            JsonToken.BEGIN_OBJECT -> {
                val map: MutableMap<String, Any?> =
                    LinkedTreeMap()
                reader.beginObject()
                while (reader.hasNext()) {
                    map[reader.nextName()] = read(reader)
                }
                reader.endObject()
                map
            }
            JsonToken.STRING -> reader.nextString()
            JsonToken.NUMBER -> {
                // 改写数字的处理逻辑，将数字值分为整型与浮点型。
                val dbNum = reader.nextString()
                if (!dbNum.contains(".")) 0 else 0.0
            }
            JsonToken.BOOLEAN -> reader.nextBoolean()
            JsonToken.NULL -> {
                reader.nextNull()
                null
            }
            else -> throw IllegalStateException()
        }
    }

    override fun write(out: JsonWriter, value: Any?) {
        // 序列化无需实现
    }
}
