package com.example.eventplanner.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridView
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import com.example.eventplanner.R
import com.example.eventplanner.adapters.CalendarAdapter

class CustomCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var currentMonthTextView: TextView
    private lateinit var prevMonthButton: ImageButton
    private lateinit var nextMonthButton: ImageButton
    private lateinit var calendarGridView: GridView
    private var currentCalendar: Calendar = Calendar.getInstance()
    private val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    private val eventDays = HashSet<Long>()

    private var onDateClickListener: OnDateClickListener? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_calendar_layout, this, true)
        orientation = VERTICAL

        currentMonthTextView = findViewById(R.id.currentMonthTextView)
        prevMonthButton = findViewById(R.id.prevMonthButton)
        nextMonthButton = findViewById(R.id.nextMonthButton)
        calendarGridView = findViewById(R.id.calendarGridView)

        setupButtonListeners()
        updateCalendar()
    }

    private fun setupButtonListeners() {
        prevMonthButton.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        nextMonthButton.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateCalendar()
        }
    }

    fun updateCalendar() {
        currentMonthTextView.text = monthYearFormat.format(currentCalendar.time)

        val cells = ArrayList<Date>()
        val calendar = currentCalendar.clone() as Calendar

        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val monthBeginningCell = calendar.get(Calendar.DAY_OF_WEEK) - (calendar.firstDayOfWeek)
        calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        while (cells.size < 42) {
            cells.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val adapter = CalendarAdapter(context, cells, currentCalendar, eventDays)
        adapter.setOnDateClickListener(onDateClickListener)
        calendarGridView.adapter = adapter

        calendarGridView.setOnItemClickListener { _, _, position, _ ->
            val selectedDate = cells[position]
            onDateClickListener?.onDateClick(selectedDate)
        }
    }

    fun addEvent(date: Date) {
        val normalizedTime = getNormalizedTime(date)
        eventDays.add(normalizedTime)
        updateCalendar()
    }

    fun removeEvent(date: Date) {
        val normalizedTime = getNormalizedTime(date)
        eventDays.remove(normalizedTime)
        updateCalendar()
    }

    fun setEvents(dates: Set<Date>) {
        eventDays.clear()
        dates.forEach { date ->
            eventDays.add(getNormalizedTime(date))
        }
        updateCalendar()
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

    fun setOnDateClickListener(listener: OnDateClickListener?) {
        this.onDateClickListener = listener
        (calendarGridView.adapter as? CalendarAdapter)?.setOnDateClickListener(listener)
    }

    interface OnDateClickListener {
        fun onDateClick(date: Date)
    }
}