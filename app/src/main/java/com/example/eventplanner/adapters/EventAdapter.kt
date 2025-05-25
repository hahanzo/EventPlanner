package com.example.eventplanner.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.eventplanner.R
import com.example.eventplanner.data.Event
import com.example.eventplanner.databinding.ItemEventBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(
    private val onEditClick: (Event) -> Unit,
    private val onDeleteClick: (Event) -> Unit
) : ListAdapter<Event, EventAdapter.EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentEvent = getItem(position)
        holder.bind(currentEvent)
    }

    class EventViewHolder(
        private val binding: ItemEventBinding,
        private val onEditClick: (Event) -> Unit,
        private val onDeleteClick: (Event) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var currentEvent: Event

        init {
            binding.root.setOnClickListener {
                showPopupMenu(it)
            }
        }

        fun bind(event: Event) {
            currentEvent = event
            binding.eventTitleTextView.text = event.title
            val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            binding.eventDateTextView.text = dateFormat.format(Date(event.date))
            binding.eventDescriptionTextView.text = event.description
        }

        private fun showPopupMenu(view: View) {
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.menuInflater.inflate(R.menu.event_item_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        onEditClick(currentEvent)
                        true
                    }
                    R.id.action_delete -> {
                        onDeleteClick(currentEvent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}