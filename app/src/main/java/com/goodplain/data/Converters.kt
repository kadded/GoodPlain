package com.goodplan.app.data

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Converters {
    private val fmt = DateTimeFormatter.ISO_LOCAL_DATE

    @TypeConverter
    fun fromDate(date: LocalDate): String = date.format(fmt)

    @TypeConverter
    fun toDate(value: String): LocalDate = LocalDate.parse(value, fmt)
}