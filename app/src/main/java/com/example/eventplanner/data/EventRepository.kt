package com.example.eventplanner.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object EventRepository {
    private val eventsList = mutableListOf<Event>()

    private val _eventsLiveData = MutableLiveData<List<Event>>(emptyList())
    val eventsLiveData: LiveData<List<Event>> get() = _eventsLiveData

    private fun refreshLiveData() {
        _eventsLiveData.postValue(ArrayList(eventsList))
    }

    fun getAllEvents(): List<Event> {
        return ArrayList(eventsList)
    }

    fun getEventById(eventId: String): Event? {
        return eventsList.find { it.id == eventId }
    }

    fun addEvents(event: Event) {
        eventsList.add(event)
        refreshLiveData()
    }

    fun deleteEvent(eventId: String) {
        eventsList.removeAll{ it.id == eventId }
        refreshLiveData()
    }

    fun updateEvent(updatedEvent: Event) {
        val index = eventsList.indexOfFirst { it.id == updatedEvent.id }
        if (index != -1) {
            eventsList[index] = updatedEvent
            refreshLiveData()
        }
    }
}