package com.aziza.spacexrocketlaunchers

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.aziza.shared.SpaceXSDK
import com.aziza.shared.cache.DatabaseDriverFactory
import com.aziza.spacexrocketlaunchers.databinding.ActivityMainBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val mainScope = MainScope()

    private lateinit var binding: ActivityMainBinding

    private val sdk = SpaceXSDK(DatabaseDriverFactory(this))

    private val launchesRvAdapter = LaunchesRvAdapter(listOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "SpaceX Launches"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initRecyclerView()

        displayLaunches(false)

        binding.swipeContainer.setOnRefreshListener {
            binding.swipeContainer.isRefreshing = false
            displayLaunches(true)
        }
    }
    private fun initRecyclerView() {
        binding.launchesListRv.apply {
            adapter = launchesRvAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
    private fun displayLaunches(needReload: Boolean) {
        binding.progressBar.isVisible = true
        mainScope.launch {
            kotlin.runCatching {
                sdk.getLaunches(needReload)
            }.onSuccess {
                launchesRvAdapter.launches = it
                launchesRvAdapter.notifyDataSetChanged()
            }.onFailure {
                Toast.makeText(this@MainActivity, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
            binding.progressBar.isVisible = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}