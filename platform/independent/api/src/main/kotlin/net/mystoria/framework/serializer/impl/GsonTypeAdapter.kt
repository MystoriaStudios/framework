package net.mystoria.framework.serializer.impl

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.mystoria.framework.serializer.FrameworkTypeAdapter

abstract class GsonTypeAdapter<T> : TypeAdapter<T>(), FrameworkTypeAdapter