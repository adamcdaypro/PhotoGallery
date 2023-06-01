package com.example.photogallery.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.photogallery.databinding.FragmentPhotoBinding

class PhotoFragment : Fragment() {

    private val args: PhotoFragmentArgs by navArgs()

    private var _binding: FragmentPhotoBinding? = null
    private val binding: FragmentPhotoBinding
        get() = checkNotNull(_binding) { "Binding not inflated" }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photoWebView.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            loadUrl(args.uri.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}