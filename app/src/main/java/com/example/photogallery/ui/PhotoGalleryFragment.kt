package com.example.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import com.example.photogallery.ui.PhotoGalleryAdapter
import com.example.photogallery.ui.PhotoGalleryViewModel
import com.example.photogallery.worker.PollWorker
import kotlinx.coroutines.launch

class PhotoGalleryFragment : Fragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "View not available" }

    private var searchView: SearchView? = null

    private val viewModel: PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workRequest = OneTimeWorkRequest.Builder(PollWorker::class.java)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(workRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.photoGallery.layoutManager = GridLayoutManager(requireContext(), 3)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { uiState ->
                    binding.photoGallery.adapter = PhotoGalleryAdapter(uiState.photos)
                    searchView?.setQuery(uiState.searchText, false)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_photo_gallery, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.d(TAG, "Search action view query $query")
                viewModel.searchFor(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d(TAG, "Search action view text changed $newText")
                return false
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                viewModel.searchFor("")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        searchView = null
    }

    companion object {

        private const val TAG = "PhotoGalleryFragment"
    }

}