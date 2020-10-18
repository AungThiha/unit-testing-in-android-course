package com.techyourchance.testdrivendevelopment.exercise8

import com.nhaarman.mockitokotlin2.mock
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.*
import com.nhaarman.mockitokotlin2.any
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema
import com.techyourchance.testdrivendevelopment.exercise8.networking.toContact

@RunWith(MockitoJUnitRunner::class)
class FetchContactsUseCaseTest {

  private val MATCH = "match"
  private val MATCH_ID_CONTACT = ContactSchema("m,match_=-", "adfadf", "afad", "adfda", 22.0)
  private val MATCH_FULL_NAME_CONTACT = ContactSchema("aaungb", "234match``", "=aung.adf", "adrere", 22.0)
  private val MATCH_FULL_PHONE_NUMBER_CONTACT = ContactSchema("unbooadf", "adbooeee", "adfdamatchadfdaf", "", 22.0)
  private val MATCH_IMAGE_URL_CONTACT = ContactSchema("unbaradf", "adbareee", "adfdaere", "admatchfd", 22.0)
  private val NO_MATCH_CONTACT = ContactSchema("adfda", "adfad", "afdafd", "dafd", 22.0)

  private val ALL_CONTACT_SCHEMES = listOf(
      MATCH_ID_CONTACT,
      MATCH_FULL_NAME_CONTACT,
      MATCH_FULL_PHONE_NUMBER_CONTACT,
      MATCH_IMAGE_URL_CONTACT,
      NO_MATCH_CONTACT
  )
  private val ALL_CONTACTS = ALL_CONTACT_SCHEMES.map { it.toContact() }
  private val MATCH_CONTACTS = ALL_CONTACTS.subList(0, 4)


  private val getContactsHttpEndpointMock = mock<GetContactsHttpEndpoint>()
  private val fetchContactsListenerMock1 = mock<FetchContactsUseCase.Listener>()
  private val fetchContactsListenerMock2 = mock<FetchContactsUseCase.Listener>()

  lateinit var SUT: FetchContactsUseCase


  @Before
  fun setup() {
    SUT = FetchContactsUseCase(getContactsHttpEndpointMock)
  }

  // correct arg passed
  @Test
  fun fetchContactsAndNotify_success_correctArgPassed() {
    SUT.fetchContactsAndNotify(MATCH)

    verify(getContactsHttpEndpointMock).getContacts(eq(MATCH), any())
  }


  @Test
  fun fetchContactsAndNotify_success_contactsNotified() {
    success()

    SUT.registerListener(fetchContactsListenerMock1)
    SUT.registerListener(fetchContactsListenerMock2)
    SUT.fetchContactsAndNotify(MATCH)

    verify(fetchContactsListenerMock1).onContactsFetched(any())
    verify(fetchContactsListenerMock2).onContactsFetched(any())
  }

  @Test
  fun fetchContactsAndNotify_success_matchedContactsNotified() {
    success()

    SUT.registerListener(fetchContactsListenerMock1)
    SUT.registerListener(fetchContactsListenerMock2)
    SUT.fetchContactsAndNotify(MATCH)

    verify(fetchContactsListenerMock1).onContactsFetched(MATCH_CONTACTS)
    verify(fetchContactsListenerMock2).onContactsFetched(MATCH_CONTACTS)
  }

  @Test
  fun fetchContactsAndNotify_success_unregisteredObserverNotNotified() {
    success()

    SUT.registerListener(fetchContactsListenerMock1)
    SUT.registerListener(fetchContactsListenerMock2)
    SUT.unregisterListener(fetchContactsListenerMock2)
    SUT.fetchContactsAndNotify(MATCH)

    verify(fetchContactsListenerMock1).onContactsFetched(any())
    verifyNoMoreInteractions(fetchContactsListenerMock2)
  }

  @Test
  fun fetchContactsAndNotify_generalError_failureNotified() {
    generalError()

    SUT.registerListener(fetchContactsListenerMock1)
    SUT.registerListener(fetchContactsListenerMock2)
    SUT.fetchContactsAndNotify(MATCH)

    verify(fetchContactsListenerMock1).onContactsFetchFailed()
    verify(fetchContactsListenerMock2).onContactsFetchFailed()
  }

  @Test
  fun fetchContactsAndNotify_networkError_failureNotified() {
    networkError()

    SUT.registerListener(fetchContactsListenerMock1)
    SUT.registerListener(fetchContactsListenerMock2)
    SUT.fetchContactsAndNotify(MATCH)

    verify(fetchContactsListenerMock1).onContactsFetchFailed()
    verify(fetchContactsListenerMock2).onContactsFetchFailed()
  }

  // region arrange helpers
  private fun success() {
    doAnswer {
      val callback = it.arguments[1] as GetContactsHttpEndpoint.Callback
      callback.onGetContactsSucceeded(ALL_CONTACT_SCHEMES)
      null
    }.`when`(getContactsHttpEndpointMock).getContacts(any(), any())
  }

  private fun generalError() {
    doAnswer {
      val callback = it.arguments[1] as GetContactsHttpEndpoint.Callback
      callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR)
      null
    }.`when`(getContactsHttpEndpointMock).getContacts(any(), any())
  }

  private fun networkError() {
    doAnswer {
      val callback = it.arguments[1] as GetContactsHttpEndpoint.Callback
      callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR)
      null
    }.`when`(getContactsHttpEndpointMock).getContacts(any(), any())
  }

  // endregion arrange helpers

}