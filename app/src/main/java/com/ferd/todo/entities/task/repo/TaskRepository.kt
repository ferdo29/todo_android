package com.ferd.todo.entities.task.repo

import kotlinx.coroutines.flow.Flow
import com.ferd.todo.entities.task.data.TaskDao
import com.ferd.todo.entities.task.data.TaskEntity

class TaskRepository(private val dao: TaskDao) {
    fun observeTasks(): Flow<List<TaskEntity>> = dao.getAll()

    suspend fun addTask(title: String, description: String, dueAtMillis: Long) {
        dao.insert(TaskEntity(title = title, description = description, dueAtMillis = dueAtMillis))
    }

    suspend fun updateTask(id: Long, title: String, description: String, dueAtMillis: Long) {
        dao.update(TaskEntity(id = id, title = title, description = description, dueAtMillis = dueAtMillis))
    }

    suspend fun deleteTask(task: TaskEntity) {
        dao.delete(task)
    }
}
