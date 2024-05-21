package com.example.quantumit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = ImageAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        fetchImages { urls ->
            Log.d("MainActivity", "Fetched ${urls.size} images")
            adapter.setImages(urls)
        }
    }

    private fun fetchImages(callback: (List<String>) -> Unit) {
        val url = "https://api.unsplash.com/photos/random?count=80"
        val apiKey = "XeoQzwU1z3dk6e7FHOTx7WMDAgUTH4BI9rNQfyx9OGw"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val connection = URL(url).openConnection() as HttpURLConnection
                connection.setRequestProperty("Authorization", "Client-ID $apiKey")
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                Log.d("MainActivity", "JSON Response: $response")
                val jsonArray = JSONArray(response)
                val urls = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    if (jsonObject.has("urls")) {
                        val imageUrl = jsonObject.getJSONObject("urls").getString("regular")
                        Log.d("MainActivity", "Image URL: $imageUrl")
                        urls.add(imageUrl)
                    } else {
                        Log.e("MainActivity", "No 'urls' key found in JSON object")
                    }
                }
                withContext(Dispatchers.Main) {
                    Log.d("MainActivity", "Fetched ${urls.size} images")
                    callback(urls)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("MainActivity", "Error fetching images", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error fetching images", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
