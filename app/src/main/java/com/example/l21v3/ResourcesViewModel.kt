package com.example.l21v3

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.l21v3.model.CoroutineModule
import com.example.l21v3.model.ResourceType
import com.example.l21v3.model.data.ResourcesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResourcesViewModel @Inject constructor(
    private val repository: ResourcesRepository,
    @CoroutineModule.MainDispatcher private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {
    val resources: LiveData<Map<ResourceType, Int>> = repository.resourcesFlow
        .asLiveData(viewModelScope.coroutineContext)

    fun updateResource(type: ResourceType, delta: Int) {
        viewModelScope.launch {
            val current = resources.value?.get(type) ?: type.initialAmount
            repository.updateResource(type, current + delta)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancel()
    }
}