package net.revive.framework.cache.api

import net.revive.framework.cache.UUIDCacheHelper
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID


// TODO: move annotations to indenpendant
interface MojangUUIDCacheService {

    @POST("uuid-cache/uuid/{uuid}")
    fun cacheByUUID(@Path("uuid") uuid: UUID): Call<UUIDCacheHelper.MojangResponse?>

    @POST("uuid-cache/username/{username}")
    fun cacheByUsername(@Path("username") username: String): Call<UUIDCacheHelper.MojangResponse?>
}