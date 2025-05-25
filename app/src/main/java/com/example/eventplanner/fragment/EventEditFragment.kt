package com.example.eventplanner.fragment

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.navigation.fragment.findNavController
import com.example.eventplanner.R
import com.example.eventplanner.databinding.FragmentEventEditBinding
import com.example.eventplanner.data.Event
import com.example.eventplanner.data.EventRepository
import java.text.SimpleDateFormat
import java.util.Locale

class EventEditFragment : Fragment() {
    private var _binding: FragmentEventEditBinding? = null
    private val binding get() = _binding!!

    private val args: EventEditFragmentArgs by navArgs()
    private var currentEvent: Event? = null

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        args.eventId?.let { eventId ->
            currentEvent = EventRepository.getEventById(eventId)
            currentEvent?.let { event ->
                binding.editTextTitle.setText(event.title)
                binding.editTextDescription.setText(event.description)
                calendar.timeInMillis = event.date
                updateDateTimeEditText()
                findNavController().currentDestination?.label = "Редагувати Подію"
            }
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.editTextDate.setOnClickListener {
            showDateTimePicker()
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.buttonSave.setOnClickListener {
            saveEvent()
        }
    }

    private fun showDateTimePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                showTimePicker()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateDateTimeEditText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    private fun updateDateTimeEditText() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        binding.editTextDate.setText(dateFormat.format(calendar.time))
    }

    private fun saveEvent() {
        val title = binding.editTextTitle.text.toString().trim()
        val description = binding.editTextDescription.text.toString().trim()
        val dateMillis = calendar.timeInMillis

        if (title.isEmpty() || description.isEmpty() || dateMillis == 0L) {
            Toast.makeText(context, "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentEvent == null) {
            val newEvent = Event(
                title = title,
                date = dateMillis,
                description = description
            )
            EventRepository.addEvents(newEvent)
            Toast.makeText(context, "Подію '${newEvent.title}' додано", Toast.LENGTH_SHORT).show()
        } else {
            currentEvent?.apply {
                this.title = title
                this.date = dateMillis
                this.description = description
                EventRepository.updateEvent(this)
                Toast.makeText(context, "Подію '${this.title}' оновлено", Toast.LENGTH_SHORT).show()
            }
        }
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}