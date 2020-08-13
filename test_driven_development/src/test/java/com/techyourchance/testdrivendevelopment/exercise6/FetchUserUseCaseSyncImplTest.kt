package com.techyourchance.testdrivendevelopment.exercise6

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException
import com.techyourchance.testdrivendevelopment.exercise6.users.User
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor

import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.Mockito.*

const val USER_ID = "userId"
const val USERNAME = "username"
val USER = User(USER_ID, USERNAME)

@RunWith(MockitoJUnitRunner::class)
class FetchUserUseCaseSyncImplTest {

  private lateinit var SUT: FetchUserUseCaseSyncImpl

  @Mock
  lateinit var fetchUserHttpEndpointSyncMock: FetchUserHttpEndpointSync
  @Mock
  lateinit var usersCacheMock: UsersCache

  @Before
  fun setup() {
    SUT = FetchUserUseCaseSyncImpl(
        fetchUserHttpEndpointSyncMock, usersCacheMock
    )
  }

  @Test
  fun fetchUserSync_getUserCacheCalled() {
    withCache()
    SUT.fetchUserSync(USER_ID)
    verify(usersCacheMock).getUser(USER_ID)
  }

  // region no cache

  @Test
  fun fetchUserSync_noCache_correctParamsPassedIntoFetchUserHttpEndpointSyncMock() {
    noCache()
    success()
    val argumentCaptor = ArgumentCaptor.forClass(String::class.java)

    SUT.fetchUserSync(USER_ID)

    verify(fetchUserHttpEndpointSyncMock).fetchUserSync(argumentCaptor.capture())
    assertEquals(USER_ID, argumentCaptor.value)
  }

  @Test
  fun fetchUserSync_noCacheAndSuccess_successReturned() {
    noCache()
    success()

    val result = SUT.fetchUserSync(USER_ID)

    assertEquals(FetchUserUseCaseSync.Status.SUCCESS, result.status)
  }

  @Test
  fun fetchUserSync_noCacheAndSuccess_userCached() {
    noCache()
    success()
    val argumentCaptor = ArgumentCaptor.forClass(User::class.java)

    val result = SUT.fetchUserSync(USER_ID)

    verify(usersCacheMock).cacheUser(argumentCaptor.capture())
    val user = argumentCaptor.value
    assertEquals(USER_ID, user.userId)
    assertEquals(USERNAME, user.username)
  }

  @Test
  fun fetchUserSync_noCacheAndSuccess_userReturned() {
    noCache()
    success()

    val result = SUT.fetchUserSync(USER_ID)

    assertEquals(FetchUserUseCaseSync.Status.SUCCESS, result.status)
    result.user!!.run {
      assertEquals(USER_ID, userId)
      assertEquals(USERNAME, username)
    }
  }

  @Test
  fun fetchUserSync_noCacheAndAuthError_failureReturned() {
    noCache()
    authError()

    val result = SUT.fetchUserSync(USER_ID)

    assertEquals(FetchUserUseCaseSync.Status.FAILURE, result.status)
  }

  @Test
  fun fetchUserSync_noCacheAndAuthError_userNotCached() {
    noCache()
    authError()

    val result = SUT.fetchUserSync(USER_ID)

    verify(usersCacheMock, never()).cacheUser(any(User::class.java))
  }

  @Test
  fun fetchUserSync_noCacheAndAuthError_userNullReturned() {
    noCache()
    authError()

    val result = SUT.fetchUserSync(USER_ID)

    assertNull(result.user)
  }

  @Test
  fun fetchUserSync_noCacheAndGeneralError_failureReturned() {
    noCache()
    generalError()

    val result = SUT.fetchUserSync(USER_ID)

    assertEquals(FetchUserUseCaseSync.Status.FAILURE, result.status)
  }

  @Test
  fun fetchUserSync_noCacheAndGeneralError_userNotCached() {
    noCache()
    generalError()

    val result = SUT.fetchUserSync(USER_ID)

    verify(usersCacheMock, never()).cacheUser(any(User::class.java))
  }

  @Test
  fun fetchUserSync_noCacheAndNetworkError_networkErrorReturned() {
    noCache()
    networkError()

    val result = SUT.fetchUserSync(USER_ID)

    assertEquals(FetchUserUseCaseSync.Status.NETWORK_ERROR, result.status)
  }

  @Test
  fun fetchUserSync_noCacheAndGeneralError_userNullReturned() {
    noCache()
    generalError()

    val result = SUT.fetchUserSync(USER_ID)

    assertNull(result.user)
  }

  @Test
  fun fetchUserSync_noCacheAndNetworkError_userNotCached() {
    noCache()
    networkError()

    val result = SUT.fetchUserSync(USER_ID)

    verify(usersCacheMock, never()).cacheUser(any(User::class.java))
  }

  @Test
  fun fetchUserSync_noCacheAndNetworkError_userNullReturned() {
    noCache()
    networkError()

    val result = SUT.fetchUserSync(USER_ID)

    assertEquals(FetchUserUseCaseSync.Status.NETWORK_ERROR, result.status)
    assertNull(result.user)
  }

  // endregion no cache

  // region with cache

  @Test
  fun fetchUserSync_withCache_fetchUserHttpEndpointSyncMockNotCalled() {
    withCache()

    val result = SUT.fetchUserSync(USER_ID)

    verifyZeroInteractions(fetchUserHttpEndpointSyncMock)
  }

  @Test
  fun fetchUserSync_withCache_successReturned() {
    withCache()

    val result = SUT.fetchUserSync(USER_ID)
    assertEquals(USER, result.user)
  }

  @Test
  fun fetchUserSync_withCache_userReturned() {
    withCache()

    val result = SUT.fetchUserSync(USER_ID)
    assertEquals(USER, result.user)
  }

  // endregion with cache

  // region helper methods

  private fun success() {
    val endpointResult = FetchUserHttpEndpointSync.EndpointResult(
        FetchUserHttpEndpointSync.EndpointStatus.SUCCESS,
        USER_ID,
        USERNAME
    )
    `when`(fetchUserHttpEndpointSyncMock
        .fetchUserSync(any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun noCache() {
    `when`(usersCacheMock
        .getUser(any(String::class.java)))
        .thenReturn(null)
  }

  private fun withCache() {
    `when`(usersCacheMock
        .getUser(any(String::class.java)))
        .thenReturn(USER)
  }

  private fun authError() {
    val endpointResult = FetchUserHttpEndpointSync.EndpointResult(
        FetchUserHttpEndpointSync.EndpointStatus.AUTH_ERROR,
        "",
        ""
    )
    `when`(fetchUserHttpEndpointSyncMock
        .fetchUserSync(any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun generalError() {
    val endpointResult = FetchUserHttpEndpointSync.EndpointResult(
        FetchUserHttpEndpointSync.EndpointStatus.GENERAL_ERROR,
        "",
        ""
    )
    `when`(fetchUserHttpEndpointSyncMock
        .fetchUserSync(any(String::class.java)))
        .thenReturn(endpointResult)
  }

  private fun networkError() {
    `when`(fetchUserHttpEndpointSyncMock
        .fetchUserSync(any(String::class.java)))
        .thenThrow(NetworkErrorException())
  }

  // endregion helper methods

}