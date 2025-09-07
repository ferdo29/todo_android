package com.ferd.todo.features.task.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ferd.todo.R
import com.ferd.todo.entities.task.data.TaskEntity
import com.ferd.todo.shared.util.formatDate
import com.ferd.todo.shared.util.formatTime
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorBottomSheet(
    initial: TaskEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, dueAtMillis: Long) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }

    val zone = ZoneId.systemDefault()
    val startDateTime = remember(initial) {
        initial?.dueAtMillis?.let { Instant.ofEpochMilli(it).atZone(zone).toLocalDateTime() }
            ?: LocalDateTime.now().plusHours(1)
    }

    var date by remember { mutableStateOf(startDateTime.toLocalDate()) }
    var time by remember { mutableStateOf(startDateTime.toLocalTime().withSecond(0).withNano(0)) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (initial == null) stringResource(id = R.string.title_add_task) else stringResource(id = R.string.title_edit_task),
                style = MaterialTheme.typography.titleMedium
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(id = R.string.label_title)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(id = R.string.label_description)) },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { showDatePicker = true }) {
                    Text(text = stringResource(id = R.string.btn_pick_date, formatDate(date)))
                }
                TextButton(onClick = { showTimePicker = true }) {
                    Text(text = stringResource(id = R.string.btn_pick_time, formatTime(time)))
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDismiss) { Text(text = stringResource(id = R.string.action_cancel)) }
                TextButton(
                    onClick = {
                        val dueMillis = LocalDateTime.of(date, time).atZone(zone).toInstant().toEpochMilli()
                        onSave(title.trim(), description.trim(), dueMillis)
                    },
                    enabled = title.isNotBlank()
                ) { Text(text = stringResource(id = R.string.action_save)) }
            }
        }
    }

    if (showDatePicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = date.atStartOfDay(zone).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = state.selectedDateMillis
                    if (selected != null) {
                        date = Instant.ofEpochMilli(selected).atZone(zone).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(text = stringResource(id = R.string.action_ok)) }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text(text = stringResource(id = R.string.action_cancel)) } }
        ) {
            DatePicker(state = state)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            initial = time,
            onDismiss = { showTimePicker = false },
            onConfirm = { newTime ->
                time = newTime
                showTimePicker = false
            }
        )
    }
}

@Composable
private fun TimePickerDialog(
    initial: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val context = LocalContext.current
    val initHour = initial.hour
    val initMinute = initial.minute

    LaunchedEffect(Unit) {
        val dialog = android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute -> onConfirm(LocalTime.of(hourOfDay, minute)) },
            initHour,
            initMinute,
            true
        )
        dialog.setOnDismissListener { onDismiss() }
        dialog.show()
    }
}
