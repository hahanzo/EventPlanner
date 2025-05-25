package com.example.eventplanner.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.eventplanner.customView.CustomCalendarView
import java.util.Calendar
import java.util.Date
import java.util.HashSet
import com.example.eventplanner.R

class CalendarAdapter(
    context: Context,
    private val days: List<Date>,
    private val currentDisplayCalendar: Calendar,
    private val eventDays: HashSet<Long>
) : ArrayAdapter<Date>(context, R.layout.calendar_day_layout, days) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var onDateClickListener: CustomCalendarView.OnDateClickListener? = null

    fun setOnDateClickListener(listener: CustomCalendarView.OnDateClickListener?) {
        this.onDateClickListener = listener
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val date = getItem(position) ?: return inflater.inflate(R.layout.calendar_day_layout, parent, false)

        val dateCalendar = Calendar.getInstance()
        dateCalendar.time = date

        val dayOfMonth = dateCalendar.get(Calendar.DAY_OF_MONTH)
        val displayMonth = dateCalendar.get(Calendar.MONTH)
        val displayYear = dateCalendar.get(Calendar.YEAR)

        val currentMonth = currentDisplayCalendar.get(Calendar.MONTH)
        val currentYear = currentDisplayCalendar.get(Calendar.YEAR)

        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.calendar_day_layout, parent, false)
            viewHolder = ViewHolder(
                view.findViewById(R.id.dayTextView),
                view.findViewById(R.id.eventIndicator)
            )
            view.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        viewHolder.dayTextView.text = dayOfMonth.toString()

        if (displayMonth == currentMonth && displayYear == currentYear) {
            viewHolder.dayTextView.setTypeface(null, Typeface.NORMAL)
        } else {
            viewHolder.dayTextView.setTextColor(Color.LTGRAY)
            viewHolder.dayTextView.setTypeface(null, Typeface.ITALIC)
        }

        val today = Calendar.getInstance()
        if (dayOfMonth == today.get(Calendar.DAY_OF_MONTH) &&
            displayMonth == today.get(Calendar.MONTH) &&
            displayYear == today.get(Calendar.YEAR)) {
            viewHolder.dayTextView.setTypeface(null, Typeface.BOLD)
            viewHolder.dayTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
        }

        val normalizedTime = getNormalizedTime(date)
        if (eventDays.contains(normalizedTime)) {
            viewHolder.eventIndicator.visibility = View.VISIBLE
        } else {
            viewHolder.eventIndicator.visibility = View.GONE
        }

        return view
    }

    private fun getNormalizedTime(date: Date): Long {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    private class ViewHolder(val dayTextView: TextView, val eventIndicator: View)
}