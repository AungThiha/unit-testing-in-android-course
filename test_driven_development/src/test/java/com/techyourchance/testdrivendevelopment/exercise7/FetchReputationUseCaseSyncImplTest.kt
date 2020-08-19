package com.techyourchance.testdrivendevelopment.exercise7

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync
import org.junit.Assert.*

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mock
import org.mockito.Mockito.*


@RunWith(MockitoJUnitRunner::class)
class FetchReputationUseCaseSyncImplTest {

  var reputation = 1

  @Mock
  lateinit var getReputationHttpEndpointSync: GetReputationHttpEndpointSync

  lateinit var SUT: FetchReputationUseCaseSyncImpl

  @Before
  fun setup() {
    SUT = FetchReputationUseCaseSyncImpl(getReputationHttpEndpointSync)
  }

  @Test
  fun fetchReputationSync_successAndReputation1_reputationReturned() {
    reputation = 1
    success()

    val result = SUT.fetchReputationSync()

    verify(getReputationHttpEndpointSync).reputationSync
    assertEquals(FetchReputationUseCaseSync.Status.SUCCESS, result.status)
    assertEquals(reputation, result.reputation)
  }

  @Test
  fun fetchReputationSync_successAndReputation4_reputationReturned() {
    reputation = 4
    success()

    val result = SUT.fetchReputationSync()

    verify(getReputationHttpEndpointSync).reputationSync
    assertEquals(FetchReputationUseCaseSync.Status.SUCCESS, result.status)
    assertEquals(reputation, result.reputation)
  }

  @Test
  fun fetchReputationSync_generalErrorAndReputation2_failureReturned() {
    reputation = 2
    generalError()

    val result = SUT.fetchReputationSync()

    assertEquals(FetchReputationUseCaseSync.Status.FAILURE, result.status)
    assertEquals(0, result.reputation)
  }

  @Test
  fun fetchReputationSync_generalErrorAndReputation10_failureReturned() {
    reputation = 10
    generalError()

    val result = SUT.fetchReputationSync()

    assertEquals(FetchReputationUseCaseSync.Status.FAILURE, result.status)
    assertEquals(0, result.reputation)
  }

  @Test
  fun fetchReputationSync_networkErrorAndReputation20_networkReturned() {
    reputation = 20
    networkError()

    val result = SUT.fetchReputationSync()

    assertEquals(FetchReputationUseCaseSync.Status.NETWORK_ERROR, result.status)
    assertEquals(0, result.reputation)
  }

  @Test
  fun fetchReputationSync_networkErrorAndReputation10_networkReturned() {
    reputation = 10
    networkError()

    val result = SUT.fetchReputationSync()

    assertEquals(FetchReputationUseCaseSync.Status.NETWORK_ERROR, result.status)
    assertEquals(0, result.reputation)
  }

  private fun success() {
    val endpointResult = GetReputationHttpEndpointSync.EndpointResult(
        GetReputationHttpEndpointSync.EndpointStatus.SUCCESS,
        reputation
    )
    `when`(getReputationHttpEndpointSync.reputationSync)
        .thenReturn(endpointResult)
  }

  private fun generalError() {
    val endpointResult = GetReputationHttpEndpointSync.EndpointResult(
        GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR,
        reputation
    )
    `when`(getReputationHttpEndpointSync.reputationSync)
        .thenReturn(endpointResult)
  }

  private fun networkError() {
    val endpointResult = GetReputationHttpEndpointSync.EndpointResult(
        GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR,
        reputation
    )
    `when`(getReputationHttpEndpointSync.reputationSync)
        .thenReturn(endpointResult)
  }

}