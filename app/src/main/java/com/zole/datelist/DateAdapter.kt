package com.zole.datelist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class DateAdapter :
    ListAdapter<DateItem, DateViewHolder>(ItemDiffCallback()) {
    private var layoutInflater: LayoutInflater? = null

    private val selectedDays = HashSet<DateItem>()

    fun getSelectedItems(): List<DateItem> {
        return currentList.filter { it.selected }.sortedBy { it.utcTime }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.context)
        }
        val view = when (viewType) {
            DateItem.TYPE_YEAR -> {
                layoutInflater!!.inflate(R.layout.item_year, parent, false)
            }

            DateItem.TYPE_MONTH -> {
                layoutInflater!!.inflate(R.layout.item_month, parent, false)
            }

            DateItem.TYPE_WEEK -> {
                layoutInflater!!.inflate(R.layout.item_week, parent, false)
            }

            else -> {
                layoutInflater!!.inflate(R.layout.item_day, parent, false)
            }
        }
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val item = getItem(position)
        val itemView = holder.itemView
        val content = itemView.findViewById<TextView>(R.id.content)
        content.text = item.formatValue
        content.isSelected = item.selected
        if (item.type == DateItem.TYPE_DAY) {
            itemView.setOnClickListener {
                handleSelected(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun handleSelected(position: Int) {
        val item = getItem(position)
        val selectedCount = currentList.count { it.selected }
        if (selectedCount > 1) {
            currentList.forEach { it.selected = false }
            selectedDays.clear()
            item.selected = true
            notifyDataSetChanged()
        } else {
            item.selected = !item.selected
        }
        if (item.selected) {
            selectedDays.add(item)
        } else {
            selectedDays.remove(item)
        }
        if (selectedDays.size >=2 ) {
            val first = selectedDays.minBy { it.utcTime }
            val last = selectedDays.maxBy { it.utcTime }
            val firstIndex = currentList.indexOf(first)
            val lastIndex = currentList.indexOf(last)
            currentList.forEachIndexed { index, dateItem ->
                dateItem.selected = index in firstIndex..lastIndex
            }
            notifyItemRangeChanged(firstIndex, lastIndex-firstIndex + 1)
        } else {
            notifyItemChanged(position)
        }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<DateItem>() {
    override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
        return oldItem.toString() == newItem.toString()
    }

    override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem): Boolean {
        return oldItem.toString() == newItem.toString()
    }
}
