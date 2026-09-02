package com.ardrawing.gocho

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.util.concurrent.TimeUnit

object ImageDownloadHelper {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun downloadTemplate(context: Context, image: CatalogImage): Result<Uri> =
        withContext(Dispatchers.IO) {
            runCatching {
                val dir = File(context.cacheDir, "templates").apply { mkdirs() }
                val extension = image.imageUrl.substringAfterLast('.', "jpg")
                    .substringBefore('?')
                    .take(4)
                val file = File(dir, "${image.id}.$extension")

                if (!file.exists() || file.length() == 0L) {
                    val request = Request.Builder().url(image.imageUrl).build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) error("Download failed: ${response.code}")
                        val bytes = response.body?.bytes() ?: error("Empty response")
                        file.writeBytes(bytes)
                    }
                }

                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file,
                )
            }
        }
}
