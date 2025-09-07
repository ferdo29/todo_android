package com.ferd.todo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferd.todo.entities.task.TasksViewModel
import com.ferd.todo.pages.tasks.TasksPage
import com.ferd.todo.shared.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoTheme {
                val vm: TasksViewModel = viewModel(factory = TasksViewModel.Factory(application))
                TasksPage(vm)
            }
        }
    }
}
