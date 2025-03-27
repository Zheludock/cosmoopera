package com.example.l21v3.model.data

import com.example.l21v3.model.CoroutineModule
import com.example.l21v3.model.ResourceEntity
import com.example.l21v3.model.ResourceType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ResourcesRepository @Inject constructor(
    private val dao: ResourcesDao,
    @CoroutineModule.IODispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val _resourcesFlow = MutableStateFlow<Map<ResourceType, Int>>(emptyMap())
    val resourcesFlow: StateFlow<Map<ResourceType, Int>> = _resourcesFlow.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    init {
        startObserving()
    }

    suspend fun initResources() {
        if (dao.getAll().isEmpty()) {
            ResourceType.entries.forEach { type ->
                dao.upsert(ResourceEntity(type, type.initialAmount))
            }
        }
    }

    private fun startObserving() {
        scope.launch {
            dao.getAllStream().collect { list ->
                _resourcesFlow.value = list.associate { it.type to it.amount }
            }
        }
    }

    suspend fun updateResource(type: ResourceType, amount: Int) {
        withContext(dispatcher) {
            dao.upsert(ResourceEntity(type, amount))
        }
    }

    fun cancel() {
        scope.cancel()
    }
}