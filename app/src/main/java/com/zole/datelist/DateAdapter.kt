package com.zole.datelist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class DateAdapter :
    ListAdapter<DateItem, DateViewHolder>(ItemDiffCallback()) {
    private var layoutInflater: LayoutInflater? = null

    private val selectedIndex = ArrayList<Int>()

    fun getSelectedItems(): List<DateItem> {
        return currentList.filter { it.selected }
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
                && item.formatValue.isNotBlank()
        if (item.type == DateItem.TYPE_DAY) {
            itemView.setOnClickListener {
                handleSelected(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    private fun handleSelected(position: Int) {
        val item = getItem(position)
        // 计算当前选中的天数
        if (selectedIndex.size > 1) {
            // 选中的是一段时间，清除之前的选中项目
            notifySelectedState(false)
            selectedIndex.clear()
        }

        item.selected = !item.selected
        // 暂存当前选中的项目
        if (item.selected) {
            selectedIndex.add(position)
        } else {
            selectedIndex.remove(position)
        }
        if (selectedIndex.size > 1) {
            // 选中的是一段时间
            notifySelectedState(true)
        } else {
            notifyItemChanged(position)
        }
    }

    private fun notifySelectedState(selected: Boolean) {
        val firstIndex = selectedIndex.min()
        val lastIndex = selectedIndex.max()
        // 将选中的项目之间的项目全部选中或反选
        for (index in firstIndex .. lastIndex) {
            currentList[index].selected = selected
        }
        notifyItemRangeChanged(firstIndex, lastIndex - firstIndex + 1)
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
