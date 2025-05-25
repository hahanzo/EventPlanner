package com.example.eventplanner.fragment

import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.eventplanner.R
import com.example.eventplanner.customView.CustomCalendarView
import com.example.eventplanner.data.EventRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CalendarFragment : Fragment() {
    private var customCalendarView: CustomCalendarView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customCalendarView = view.findViewById(R.id.customCalendar)

        customCalendarView?.setOnDateClickListener(object : CustomCalendarView.OnDateClickListener {
            override fun onDateClick(date: Date) {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                Toast.makeText(context, "Обрано: ${sdf.format(date)}", Toast.LENGTH_SHORT).show()
            }
        })

        EventRepository.eventsLiveData.observe(viewLifecycleOwner) { events ->
            val datesWithEvents: Set<Date> = events.map { Date(it.date) }.toSet()

            customCalendarView?.setEvents(datesWithEvents)
        }

        val initialEvents = EventRepository.getAllEvents()
        val initialDatesWithEvents: Set<Date> = initialEvents.map { Date(it.date) }.toSet()
        customCalendarView?.setEvents(initialDatesWithEvents)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        customCalendarView = null
    }

}