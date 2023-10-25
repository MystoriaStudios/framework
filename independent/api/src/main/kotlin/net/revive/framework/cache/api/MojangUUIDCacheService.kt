package net.revive.framework.cache.api

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Filters.eq
import net.revive.framework.Framework
import net.revive.framework.cache.UUIDCacheHelper
import net.revive.framework.storage.storable.IStorable
import org.bson.Document
import org.bson.conversions.Bson
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.*
import kotlin.reflect.KClass

// TODO: move annotations to indenpendant
interface MojangUUIDCacheService {

    @POST("uuid-cache/uuid/{uuid}")
    fun cacheByUUID(@Path("uuid") uuid: UUID): Call<UUIDCacheHelper.MojangResponse?>

    @POST("uuid-cache/username/{username}")
    fun cacheByUsername(@Path("username") username: String): Call<UUIDCacheHelper.MojangResponse?>
}