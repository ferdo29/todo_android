package com.ferd.todo.pages.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import com.ferd.todo.R
import com.ferd.todo.entities.task.TasksViewModel
import com.ferd.todo.entities.task.data.TaskEntity
import com.ferd.todo.features.task.editor.TaskEditorDialog
import com.ferd.todo.features.task.list.TaskRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksPage(vm: TasksViewModel) {
    val tasks by vm.tasks.collectAsState(initial = emptyList())
    var editorOpen by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<TaskEntity?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.title_tasks)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingTask = null
                editorOpen = true
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(id = R.string.cd_add_task))
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    onClick = {
                        editingTask = task
                        editorOpen = true
                    },
                    onDelete = { vm.deleteTask(task) }
                )
            }
        }

        if (editorOpen) {
            TaskEditorDialog(
                initial = editingTask,
                onDismiss = { editorOpen = false },
                onSave = { title, description, dueAtMillis ->
                    if (editingTask == null) {
                        vm.addTask(title, description, dueAtMillis)
                    } else {
                        vm.updateTask(editingTask!!.id, title, description, dueAtMillis)
                    }
                    editorOpen = false
                }
            )
        }
    }
}

