package com.ejemplo.galvanmorales.repository

import com.ejemplo.galvanmorales.model.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class EventRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val eventsCollection = firestore.collection("events")

    fun getEvents(): Flow<List<Event>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""

        val subscription = eventsCollection
            .whereEqualTo("userId", userId)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val events = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Event::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(events)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun addEvent(event: Event): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: return false
            val eventConUserId = event.copy(userId = userId)
            eventsCollection.add(eventConUserId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    suspend fun updateEvent(event: Event): Boolean {
        return try {
            if (event.id.isEmpty()) return false

            val eventMap = mapOf(
                "titulo" to event.titulo,
                "fecha" to event.fecha,
                "descripcion" to event.descripcion,
                "userId" to event.userId
            )

            eventsCollection.document(event.id).update(eventMap).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        return try {
            if (eventId.isEmpty()) return false
            eventsCollection.document(eventId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
