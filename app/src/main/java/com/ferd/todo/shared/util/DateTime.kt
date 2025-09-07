package com.ferd.todo.shared.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatMillis(millis: Long): String {
    val zdt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
    return zdt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
}

fun formatDate(date: LocalDate): String = date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
fun formatTime(time: LocalTime): String = time.format(DateTimeFormatter.ofPattern("HH:mm"))

