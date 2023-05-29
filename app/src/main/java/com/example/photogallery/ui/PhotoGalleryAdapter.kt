package com.example.photogallery.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.photogallery.R
import com.example.photogallery.databinding.PhotoGalleryListItemBinding
import com.example.photogallery.model.Photo

class PhotoGalleryAdapter(
    private val photos: List<Photo>,
    private val onPhotoClicked: (Photo) -> Unit
) :
    RecyclerView.Adapter<PhotoGalleryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PhotoGalleryListItemBinding.inflate(inflater, parent, false)
        return PhotoGalleryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return photos.size
    }

    override fun onBindViewHolder(holder: PhotoGalleryViewHolder, position: Int) {
        holder.bind(photos[position], onPhotoClicked)
    }

}

class PhotoGalleryViewHolder(private val binding: PhotoGalleryListItemBinding) :
    ViewHolder(binding.root) {

    fun bind(photo: Photo, onPhotoClicked: (Photo) -> Unit) {
        binding.photoImageView.apply {
            load(photo.urlSmall) {
                placeholder(R.drawable.ic_launcher_foreground)
                crossfade(true)
            }
            contentDescription = photo.title
            setOnClickListener { onPhotoClicked(photo) }
        }
    }

}