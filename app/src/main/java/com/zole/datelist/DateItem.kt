package com.zole.datelist

data class DateItem(
    val utcTime: Long,
    val year:Int,
    val month:Int,
    val day:Int,
    val formatValue: String = "$year-$month-$day",
    val type: Int,
    var selected: Boolean
) {

    companion object {
        const val TYPE_YEAR = 0
        const val TYPE_MONTH = 1
        const val TYPE_DAY = 2
        const val TYPE_WEEK = 3
    }
}