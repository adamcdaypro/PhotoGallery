package com.example.photogallery

import android.content.Intent
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
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.photogallery.databinding.FragmentPhotoGalleryBinding
import com.example.photogallery.ui.PhotoGalleryAdapter
import com.example.photogallery.ui.PhotoGalleryViewModel
import com.example.photogallery.worker.PollWorker
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PhotoGalleryFragment : Fragment() {

    private var _binding: FragmentPhotoGalleryBinding? = null
    private val binding
        get() = checkNotNull(_binding) { "View not available" }

    private var searchView: SearchView? = null
    private var pollingMenuItem: MenuItem? = null

    private val viewModel: PhotoGalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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
                    Log.d(TAG, "$TAG collected UI State")
                    binding.photoGallery.adapter = PhotoGalleryAdapter(uiState.photos) { photo ->
                        startActivity(Intent(Intent.ACTION_VIEW, photo.photoPageUri))
                    }
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
        onCreateSearchView()
        pollingMenuItem = menu.findItem(R.id.menu_item_toggle_polling)
        onCreatePollingMenuItem()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_clear -> {
                viewModel.searchFor("")
                true
            }

            R.id.menu_item_toggle_polling -> {
                handleTogglePolling()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu()
        searchView = null
        pollingMenuItem = null
    }

    private fun onCreateSearchView() {
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

    private fun onCreatePollingMenuItem() {
        val isPolling = viewModel.uiState.value.isPolling
        pollingMenuItem?.title = if (isPolling) {
            getString(R.string.stop_polling)
        } else {
            getString(R.string.stop_polling)
        }
    }

    private fun handleTogglePolling(): Boolean {
        if (pollingMenuItem == null) return false
        val startPolling = getString(R.string.start_polling)
        val stopPolling = getString(R.string.stop_polling)
        when (pollingMenuItem!!.title.toString()) {
            startPolling -> {
                pollingMenuItem!!.title = stopPolling
                viewModel.setIsPollingTo(true)
                startPollingWorker()
                Log.d(
                    TAG,
                    "User started polling; menu title changed from $startPolling to $stopPolling"
                )
            }

            stopPolling -> {
                pollingMenuItem!!.title = startPolling
                viewModel.setIsPollingTo(false)
                stopPollingWorker()
                Log.d(
                    TAG,
                    "User stopped polling; menu title changed from $stopPolling to $startPolling"
                )
            }
        }
        return true
    }

    private fun startPollingWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()
        val workerRequest =
            PeriodicWorkRequest.Builder(PollWorker::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            PollWorker.TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            workerRequest
        )
        Log.d(TAG, "${PollWorker.TAG}, $workerRequest started")
    }

    private fun stopPollingWorker() {
        WorkManager.getInstance(requireContext()).cancelUniqueWork(PollWorker.TAG)
        Log.d(TAG, "${PollWorker.TAG} cancelled")
    }

    companion object {

        private const val TAG = "PhotoGalleryFragment"
    }

}