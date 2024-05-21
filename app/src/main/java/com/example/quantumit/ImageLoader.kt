package com.example.quantumit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.ImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ImageLoader(private val context: Context) {
    private val memoryCache: LruCache<String, Bitmap>

    init {
        val cacheSize = (Runtime.getRuntime().maxMemory() / 1024 / 8).toInt()
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun loadImage(url: String, imageView: ImageView) {
        val bitmap = memoryCache.get(url)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageResource(R.drawable.placeholder)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream = URL(url).openStream()
                    val bmp = BitmapFactory.decodeStream(inputStream)
                    memoryCache.put(url, bmp)
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bmp)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        imageView.setImageResource(R.drawable.placeholder)
                    }
                }
            }
        }
    }
}
