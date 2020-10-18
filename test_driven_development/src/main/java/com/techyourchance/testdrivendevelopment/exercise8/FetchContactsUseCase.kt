package com.techyourchance.testdrivendevelopment.exercise8

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback
import com.techyourchance.testdrivendevelopment.exercise8.networking.toContact

class FetchContactsUseCase(
    private val getContactsHttpEndpoint: GetContactsHttpEndpoint
) {

  val listeners = mutableListOf<Listener>()

  fun fetchContactsAndNotify(match: String) {
    getContactsHttpEndpoint.getContacts(match, object: Callback {
      override fun onGetContactsSucceeded(cartItems: MutableList<ContactSchema>?) {
        val contacts = cartItems
            ?.filter {
              it.id.contains(match) || it.fullName.contains(match)
                  || it.fullPhoneNumber.contains(match) || it.imageUrl.contains(match)
            }
            ?.map { it.toContact() }
            ?: emptyList()
        listeners.forEach { listener ->
          listener.onContactsFetched(contacts)
        }
      }

      override fun onGetContactsFailed(failReason: GetContactsHttpEndpoint.FailReason?) {
        when (failReason) {
          GetContactsHttpEndpoint.FailReason.GENERAL_ERROR, GetContactsHttpEndpoint.FailReason.NETWORK_ERROR -> {
            listeners.forEach { listener ->
              listener.onContactsFetchFailed()
            }
          }
        }
      }
    })
  }

  fun registerListener(listener: Listener) {
    listeners.add(listener)
  }

  fun unregisterListener(listener: Listener) {
    listeners.remove(listener)
  }

  interface Listener {
    fun onContactsFetched(contacts: List<Contact>)
    fun onContactsFetchFailed()
  }

}