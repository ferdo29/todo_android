package com.ferd.todo.entities.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferd.todo.shared.database.AppDatabase
import com.ferd.todo.entities.task.data.TaskEntity
import com.ferd.todo.entities.task.repo.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TasksViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _tasks = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasks: StateFlow<List<TaskEntity>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeTasks().collectLatest { _tasks.value = it }
        }
    }

    fun addTask(title: String, description: String, dueAtMillis: Long) {
        viewModelScope.launch { repository.addTask(title, description, dueAtMillis) }
    }

    fun updateTask(id: Long, title: String, description: String, dueAtMillis: Long) {
        viewModelScope.launch { repository.updateTask(id, title, description, dueAtMillis) }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch { repository.deleteTask(task) }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(application)
            val repo = TaskRepository(db.taskDao())
            return TasksViewModel(repo) as T
        }
    }
}
