package com.example.quantumit

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(private val context: Context) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private val imageUrls = mutableListOf<String>()
    private val imageLoader = ImageLoader(context)

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageUrl = imageUrls[position]
        imageLoader.loadImage(imageUrl, holder.imageView)
    }

    override fun getItemCount() = imageUrls.size

    fun setImages(urls: List<String>) {
        imageUrls.clear()
        imageUrls.addAll(urls)
        notifyDataSetChanged()
    }
}
