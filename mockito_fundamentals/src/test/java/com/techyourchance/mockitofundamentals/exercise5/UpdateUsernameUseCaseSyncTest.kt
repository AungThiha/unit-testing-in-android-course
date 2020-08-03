package com.techyourchance.mockitofundamentals.exercise5

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync
import com.techyourchance.mockitofundamentals.exercise5.users.User
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*

class UpdateUsernameUseCaseSyncTest {

  private val USER_ID = "userId"
  private val USERNAME = "username"

  lateinit var updateUsernameHttpEndpointSyncMock: UpdateUsernameHttpEndpointSync
  lateinit var usersCacheMock: UsersCache
  lateinit var eventBusPosterMock: EventBusPoster

  lateinit var SUT: UpdateUsernameUseCaseSync

  @Before
  fun setup() {
    updateUsernameHttpEndpointSyncMock = mock(UpdateUsernameHttpEndpointSync::class.java)
    usersCacheMock = mock(UsersCache::class.java)
    eventBusPosterMock = mock(EventBusPoster::class.java)
    SUT = UpdateUsernameUseCaseSync(
        updateUsernameHttpEndpointSyncMock, usersCacheMock, eventBusPosterMock
    )
    success()
  }

  @Test
  fun updateUsernameSync_userIdUsernamePassed() {
    val captor = ArgumentCaptor.forClass(String::class.java)
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verify(updateUsernameHttpEndpointSyncMock).updateUsername(captor.capture(), captor.capture())
    val captures = captor.allValues
    assertEquals(USER_ID, captures[0])
    assertEquals(USERNAME, captures[1])
  }

  @Test
  fun updateUsernameSync_success_userCached() {
    val captor = ArgumentCaptor.forClass(User::class.java)
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verify(usersCacheMock).cacheUser(captor.capture())
    val value = captor.value
    assertEquals(USER_ID, value.userId)
    assertEquals(USERNAME, value.username)
  }

  @Test
  fun updateUsernameSync_authError_userNotCached() {
    authError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(usersCacheMock)
  }

  @Test
  fun updateUsernameSync_serverError_userNotCached() {
    serverError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(usersCacheMock)
  }

  @Test
  fun updateUsernameSync_generalError_userNotCached() {
    generalError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(usersCacheMock)
  }

  @Test
  fun updateUsernameSync_networkError_userNotCached() {
    networkError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(usersCacheMock)
  }

  @Test
  fun updateUsernameSync_success_eventBusPosted() {
    val captor = ArgumentCaptor.forClass(Object::class.java)
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verify(eventBusPosterMock).postEvent(captor.capture())
    val value = captor.value
    assertTrue(value is UserDetailsChangedEvent)
  }

  @Test
  fun updateUsernameSync_authError_eventBusNotPosted() {
    authError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(eventBusPosterMock)
  }

  @Test
  fun updateUsernameSync_serverError_eventBusNotPosted() {
    serverError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(eventBusPosterMock)
  }

  @Test
  fun updateUsernameSync_generalError_eventBusNotPosted() {
    generalError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(eventBusPosterMock)
  }

  @Test
  fun updateUsernameSync_networkError_eventBusNotPosted() {
    networkError()
    SUT.updateUsernameSync(USER_ID, USERNAME)
    verifyNoMoreInteractions(eventBusPosterMock)
  }

  @Test
  fun updateUsernameSync_success_successReturned() {
    val result = SUT.updateUsernameSync(USER_ID, USERNAME)
    assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS, result)
  }

  @Test
  fun updateUsernameSync_authError_failureReturned() {
    authError()
    val result = SUT.updateUsernameSync(USER_ID, USERNAME)
    assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE, result)
  }

  @Test
  fun updateUsernameSync_serverError_failureReturned() {
    serverError()
    val result = SUT.updateUsernameSync(USER_ID, USERNAME)
    assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE, result)
  }

  @Test
  fun updateUsernameSync_generalError_failureReturned() {
    generalError()
    val result = SUT.updateUsernameSync(USER_ID, USERNAME)
    assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE, result)
  }

  @Test
  fun updateUsernameSync_networkError_failureReturned() {
    networkError()
    val result = SUT.updateUsernameSync(USER_ID, USERNAME)
    assertEquals(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR, result)
  }

  private fun success() {
    val endpointResult = UpdateUsernameHttpEndpointSync.EndpointResult(
        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS,
        USER_ID,
        USERNAME
    )
    `when`(updateUsernameHttpEndpointSyncMock
        .updateUsername(any(String::class.java), any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun authError() {
    val endpointResult = UpdateUsernameHttpEndpointSync.EndpointResult(
        UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
        "",
        ""
    )
    `when`(updateUsernameHttpEndpointSyncMock
        .updateUsername(any(String::class.java), any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun serverError() {
    val endpointResult = UpdateUsernameHttpEndpointSync.EndpointResult(
        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
        "",
        ""
    )
    `when`(updateUsernameHttpEndpointSyncMock
        .updateUsername(any(String::class.java), any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun generalError() {
    val endpointResult = UpdateUsernameHttpEndpointSync.EndpointResult(
        UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
        "",
        ""
    )
    `when`(updateUsernameHttpEndpointSyncMock
        .updateUsername(any(String::class.java), any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun networkError() {
    `when`(updateUsernameHttpEndpointSyncMock
        .updateUsername(any(String::class.java), any(String::class.java)))
        .thenThrow(NetworkErrorException())
  }

}