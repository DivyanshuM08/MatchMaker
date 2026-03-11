package com.example.matchmaker

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchmaker.databinding.ActivityMainBinding
import com.example.matchmaker.ui.list.MatchAdapter
import com.example.matchmaker.ui.main.MatchListViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MatchListViewModel by viewModels {
        MatchListViewModel.Factory((application as MatchMateApplication).matchRepository)
    }

    private val adapter = MatchAdapter(
        onAccept = { viewModel.accept(it) },
        onDecline = { viewModel.decline(it) }
    )

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerMatches.layoutManager = LinearLayoutManager(this)
        binding.recyclerMatches.adapter = adapter

        binding.buttonRetry.setOnClickListener {
            viewModel.clearError()
            viewModel.refresh()
        }

        viewModel.refresh()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.items)
                    binding.progressLoading.isVisible = state.isLoading
                    binding.errorLayout.isVisible = state.error != null
                    binding.textError.text = state.error
                    binding.textEmpty.isVisible = state.isEmpty && !state.isLoading && state.error == null
                    binding.recyclerMatches.isVisible = state.items.isNotEmpty()

                    if (state.error != null) {
                        Snackbar.make(binding.main, state.error!!, Snackbar.LENGTH_LONG)
                            .setAction("Retry") {
                                viewModel.clearError()
                                viewModel.refresh()
                            }
                            .show()
                    }
                }
            }
        }

        registerNetworkCallback()
    }

    private fun registerNetworkCallback() {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                runOnUiThread {
                    Snackbar.make(binding.main, "You're offline", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onAvailable(network: Network) {
                runOnUiThread {
                    Snackbar.make(binding.main, "Back online", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        cm.registerNetworkCallback(request, networkCallback!!)
    }

    override fun onDestroy() {
        networkCallback?.let {
            getSystemService(ConnectivityManager::class.java)?.unregisterNetworkCallback(it)
        }
        networkCallback = null
        super.onDestroy()
    }
}
