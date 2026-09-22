package com.ardrawing.gocho

import android.content.Context
import com.ardrawing.gocho.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.util.concurrent.TimeUnit

class CatalogRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun fetchImages(context: Context): Result<List<CatalogImage>> = withContext(Dispatchers.IO) {
        val baseUrl = BuildConfig.SUPABASE_URL.trim()
        val anonKey = BuildConfig.SUPABASE_ANON_KEY.trim()

        if (baseUrl.isEmpty() || anonKey.isEmpty()) {
            return@withContext Result.failure(
                IllegalStateException(context.getString(R.string.supabase_not_configured)),
            )
        }

        val selectWithCategory =
            "$baseUrl/rest/v1/images?select=id,title,thumbnail_url,image_url,is_premium,category,sort_order&order=sort_order.asc"
        val selectLegacy =
            "$baseUrl/rest/v1/images?select=id,title,thumbnail_url,image_url,is_premium,sort_order&order=sort_order.asc"

        runCatching {
            executeCatalogRequest(selectWithCategory, anonKey, context)
        }.recoverCatching {
            executeCatalogRequest(selectLegacy, anonKey, context)
        }
    }

    private fun executeCatalogRequest(
        url: String,
        anonKey: String,
        context: Context,
    ): List<CatalogImage> {
        val request = Request.Builder()
            .url(url)
            .header("apikey", anonKey)
            .header("Authorization", "Bearer $anonKey")
            .header("Accept", "application/json")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                error(context.getString(R.string.supabase_error, response.code))
            }
            val body = response.body?.string() ?: "[]"
            return parseImages(body)
        }
    }

    private fun parseImages(json: String): List<CatalogImage> {
        val array = JSONArray(json)
        val images = mutableListOf<CatalogImage>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            images.add(
                CatalogImage(
                    id = obj.getString("id"),
                    title = obj.optString("title", ""),
                    thumbnailUrl = obj.getString("thumbnail_url"),
                    imageUrl = obj.getString("image_url"),
                    isPremium = obj.optBoolean("is_premium", false),
                    category = obj.optString("category", CatalogCategory.ANIMALS.key)
                        .ifBlank { CatalogCategory.ANIMALS.key },
                    sortOrder = obj.optInt("sort_order", 0),
                ),
            )
        }
        return images
    }
}
