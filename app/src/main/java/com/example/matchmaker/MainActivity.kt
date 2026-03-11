package com.example.matchmaker

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    val application = MatchMateApplication()

    private val viewModel: MatchListViewModel by viewModels {
        MatchListViewModel.Factory(application.matchRepository, this)
    }

    private val adapter = MatchAdapter(
        onAccept = { viewModel.accept(it) },
        onDecline = { viewModel.decline(it) }
    )

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /** True when the device has no internet. Refresh button is shown only when offline. */
    private var isOffline: Boolean = false
        set(value) {
            if (field != value) {
                field = value
                invalidateOptionsMenu()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.recyclerMatches.layoutManager = LinearLayoutManager(this)
        binding.recyclerMatches.adapter = adapter

        isOffline = !isNetworkAvailable()

        viewModel.refresh()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.items)
                    binding.progressLoading.isVisible = state.isLoading
                    binding.textEmpty.isVisible = state.isEmpty && !state.isLoading && state.error == null
                    binding.recyclerMatches.isVisible = state.items.isNotEmpty()

                    if (state.error != null) {
                        Snackbar.make(binding.main, state.error!!, Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.retry)) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_refresh)?.isVisible = isOffline
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                if (isNetworkAvailable()) {
                    viewModel.refresh()
                } else {
                    Snackbar.make(binding.main, getString(R.string.no_internet_try_again), Snackbar.LENGTH_LONG).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return false
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerNetworkCallback() {
        val cm = getSystemService(ConnectivityManager::class.java) ?: return
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                runOnUiThread {
                    val wasOnline = !isOffline
                    isOffline = true
                    invalidateOptionsMenu()
                    if (wasOnline) {
                        Snackbar.make(binding.main, getString(R.string.youre_offline), Snackbar.LENGTH_LONG).show()
                    }
                }
            }

            override fun onAvailable(network: Network) {
                runOnUiThread {
                    val wasOffline = isOffline
                    isOffline = false
                    invalidateOptionsMenu()
                    if (wasOffline) {
                        Snackbar.make(binding.main, getString(R.string.back_online), Snackbar.LENGTH_LONG).show()
                    }
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
