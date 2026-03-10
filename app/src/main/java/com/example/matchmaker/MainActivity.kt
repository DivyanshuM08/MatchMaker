package com.example.matchmaker

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
                }
            }
        }
    }
}
