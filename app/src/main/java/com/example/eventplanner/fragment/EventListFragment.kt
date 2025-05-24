package com.example.eventplanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventplanner.EventAdapter
import com.example.eventplanner.R
import com.example.eventplanner.data.Event
import com.example.eventplanner.data.EventRepository
import com.example.eventplanner.databinding.FragmentEventListBinding
import java.util.Calendar

class EventListFragment : Fragment() {

    private var _binding: FragmentEventListBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        if (EventRepository.getAllEvents().isEmpty()) {
            addSampleEvents()
        }
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter { event ->
        }
        binding.eventsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = eventAdapter
        }
    }

    private fun setupObservers() {
        EventRepository.eventsLiveData.observe(viewLifecycleOwner) { events ->
            eventAdapter.submitList(events)
        }
    }

    private fun setupClickListeners() {
        binding.addEventButton.setOnClickListener {
            val newEvent = Event(
                title = "Нова подія ${System.currentTimeMillis() % 1000}",
                date = System.currentTimeMillis() + (1000 * 60 * 60 * 24 * (1..7).random()), // Через 1-7 днів
                description = "Це щойно додана подія."
            )
            EventRepository.addEvents(newEvent)
        }
    }

    private fun addSampleEvents() {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val tomorrow = calendar.timeInMillis

        calendar.timeInMillis = now
        calendar.add(Calendar.DAY_OF_MONTH, 7)
        val nextWeek = calendar.timeInMillis

        EventRepository.addEvents(
            Event(
                title = "Зустріч з командою",
                date = tomorrow,
                description = "Обговорення прогресу проекту та наступних кроків."
            )
        )
        EventRepository.addEvents(
            Event(
                title = "День народження колеги",
                date = nextWeek,
                description = "Не забудьте привітати Марію!"
            )
        )
        EventRepository.addEvents(
            Event(
                title = "Пройти курс з Kotlin",
                date = now + (1000 * 60 * 60 * 24 * 14),
                description = "Вивчити нові можливості Kotlin для Android розробки."
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}