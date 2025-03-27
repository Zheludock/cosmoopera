package com.example.l21v3

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.l21v3.databinding.ActivityMainBinding
import com.example.l21v3.model.ResourceType
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: ResourcesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.resources.observe(this) { resources ->
            resources?.let { updateResourceBar(it) }
        }

        // 2. Подписываемся на LiveData с viewLifecycleOwner
        viewModel.resources.observe(this) { resources ->  // ✅ this = LifecycleOwner для Activity
            Log.d("MainActivity", "Resources: $resources")
            val container = findViewById<LinearLayout>(R.id.resources_container)
            container?.removeAllViews() ?: Log.e("MainActivity", "Контейнер не найден!")

            resources?.forEach { (type, amount) ->
                val itemView = LayoutInflater.from(this)  // ✅ this = Context
                    .inflate(R.layout.resource_item, container, false)

                itemView.findViewById<ImageView>(R.id.icon).setImageResource(type.iconRes)
                itemView.findViewById<TextView>(R.id.amount).text = amount.toString()

                container.addView(itemView)
            }
        }

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    private fun updateResourceBar(resources: Map<ResourceType, Int>) {
        if (binding.resourcesContainer.childCount != resources.size) {
            binding.resourcesContainer.removeAllViews()
            resources.keys.forEach { type ->
                val view = layoutInflater.inflate(R.layout.resource_item, binding.resourcesContainer, false)
                view.findViewById<ImageView>(R.id.icon).setImageResource(type.iconRes)
                binding.resourcesContainer.addView(view)
            }
        }
        resources.values.forEachIndexed { index, amount ->
            binding.resourcesContainer.getChildAt(index)
                .findViewById<TextView>(R.id.amount).text = amount.toString()
        }
    }
}