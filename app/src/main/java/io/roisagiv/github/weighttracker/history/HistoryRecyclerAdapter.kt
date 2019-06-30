package io.roisagiv.github.weighttracker.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.grivos.spanomatic.utils.addSpansFromAnnotations
import io.roisagiv.github.weighttracker.R
import io.roisagiv.github.weighttracker.entity.WeightItem
import org.threeten.bp.format.TextStyle
import java.util.Locale

/**
 *
 */
class HistoryRecyclerAdapter :
    ListAdapter<WeightItem, HistoryRecyclerAdapter.ItemHolder>(HistoryDataItemCallback()) {
    /**
     *
     */
    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    /**
     *
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        // Inflate the custom layout
        val view = inflater.inflate(R.layout.item_history, parent, false)
        return ItemHolder(view)
    }

    /**
     *
     */
    class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val dateTextView: TextView = itemView.findViewById(R.id.textview_date)
        private val weightTextView: TextView = itemView.findViewById(R.id.textview_weight)
        private val notesTextView: TextView = itemView.findViewById(R.id.textview_notes)

        /**
         *
         */
        fun bindTo(item: WeightItem) {
            bindDate(item, dateTextView)
            bindWeight(item, weightTextView)
            notesTextView.text = item.notes
        }

        /**
         *
         */
        private fun bindWeight(item: WeightItem, weightTextView: TextView) {
            val weightText = weightTextView.context.addSpansFromAnnotations(
                R.string.history_item_weight_template,
                item.weight,
                "kg"
            )
            weightTextView.setText(weightText, TextView.BufferType.SPANNABLE)
        }

        /**
         *
         */
        private fun bindDate(item: WeightItem, dateTextView: TextView) {
            val dayOfWeek = item.date.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale.getDefault()
            )
            val dayOfMonth = item.date.dayOfMonth

            val dateText = dateTextView.context.addSpansFromAnnotations(
                R.string.history_item_date_template,
                dayOfMonth,
                dayOfWeek
            )
            dateTextView.setText(dateText, TextView.BufferType.SPANNABLE)
        }
    }

    /**
     *
     */
    class HistoryDataItemCallback : DiffUtil.ItemCallback<WeightItem>() {
        /**
         *
         */
        override fun areItemsTheSame(oldItem: WeightItem, newItem: WeightItem): Boolean =
            oldItem.id == newItem.id

        /**
         *
         */
        override fun areContentsTheSame(
            oldItem: WeightItem,
            newItem: WeightItem
        ): Boolean = oldItem == newItem
    }
}
