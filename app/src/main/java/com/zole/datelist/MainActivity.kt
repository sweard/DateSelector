package com.zole.datelist

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val dateList = findViewById<RecyclerView>(R.id.dateList)
        val data = createDateList()
        val adapter = DateAdapter().apply {
            submitList(data)
        }
        dateList.adapter = adapter
        val gridManager = GridLayoutManager(this, 7)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    DateItem.TYPE_YEAR -> 7
                    DateItem.TYPE_MONTH -> 7
                    else -> 1
                }
            }
        }
        dateList.layoutManager = gridManager
    }

    private fun createDateList(): List<DateItem> {
        val dateList = ArrayList<DateItem>()
        val curDate = Calendar.getInstance()
        curDate.set(2024, 6 - 1, 15)
        curDate.set(Calendar.HOUR_OF_DAY, 0)
        val todayMillis = curDate.timeInMillis
        val dayRange = 24 * 60 * 60 * 1000L
        for (i in 0..90) {
            val targetMillis = todayMillis + (i * dayRange)
            curDate.timeInMillis = targetMillis
            val year = curDate.get(Calendar.YEAR)
            val month = curDate.get(Calendar.MONTH) + 1
            val day = curDate.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = curDate.get(Calendar.DAY_OF_WEEK)
            val formatValue = "$year-$month-$day"
            Log.d("日历", "createDateList item: $formatValue dayOfWeek:$dayOfWeek")
            val item =
                DateItem(targetMillis, year, month, day, formatValue = day.toString(), type = DateItem.TYPE_DAY, selected = false)
            if (day == 1 || i == 0) {
                // 一个月第一天，或者整个数据第一天添加月份
                val monthItem = DateItem(
                    targetMillis,
                    year,
                    month,
                    0,
                    formatValue = "$year-$month",
                    type = DateItem.TYPE_MONTH,
                    selected = false
                )
                dateList.add(monthItem)
                dateList.addAll(getBlankWeekDay(dayOfWeek))
            }

            dateList.add(item)
        }
        return dateList
    }

    private fun getBlankWeekDay(dayOfWeek: Int): List<DateItem> {
        // 1-7 sunday - saturday
        val count = dayOfWeek - 1
        val dateList = ArrayList<DateItem>()
        // 添加周日到周六
        for (i in 0 until 7) {
            val item = DateItem(0, 0, 0, 0, getWeekName(i + 1), type = DateItem.TYPE_WEEK, selected = false)
            dateList.add(item)
        }
        // 补全空白处
        for (i in 0 until count) {
            val item =
                DateItem(0, 0, 0, 0, formatValue = "", type = DateItem.TYPE_DAY, selected = false)
            dateList.add(item)
        }
        return dateList
    }

    private fun getWeekName(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "Sun"
            2 -> "Mon"
            3 -> "Tue"
            4 -> "Wed"
            5 -> "Thu"
            6 -> "Fri"
            7 -> "Sat"
            else -> ""
        }
    }
}