package com.example.eventplanner.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventplanner.adapters.EventAdapter
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
        eventAdapter = EventAdapter (
            onEditClick = { event ->
                val action = EventListFragmentDirections.actionEventListFragmentToEventEditFragment(
                    eventId = event.id,
                    title = "Редагувати Подію"
                )
                findNavController().navigate(action)
            },
            onDeleteClick = { event ->
                EventRepository.deleteEvent(event.id)
                Toast.makeText(context, "Подію '${event.title}' видалено", Toast.LENGTH_SHORT).show()
            }
        )

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
            val action = EventListFragmentDirections.actionEventListFragmentToEventEditFragment(
                eventId = null,
                title = "Додати Подію"
            )
            findNavController().navigate(action)
        }
        binding.openCalendarButton.setOnClickListener {
            val action = EventListFragmentDirections.actionEventListFragmentToCalendarFragment()
            findNavController().navigate(action)
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