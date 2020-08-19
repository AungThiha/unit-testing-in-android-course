package com.techyourchance.testdrivendevelopment.exercise7

import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync

interface FetchReputationUseCaseSync {
  enum class Status {
    SUCCESS, FAILURE, NETWORK_ERROR
  }
  class UseCaseResult(val status: Status, val reputation: Int)

  fun fetchReputationSync(): UseCaseResult
}

class FetchReputationUseCaseSyncImpl(
    private val reputationHttpEndpointSync: GetReputationHttpEndpointSync
): FetchReputationUseCaseSync {
  override fun fetchReputationSync(): FetchReputationUseCaseSync.UseCaseResult {
    val result = reputationHttpEndpointSync.reputationSync
    return when (result.status) {
      GetReputationHttpEndpointSync.EndpointStatus.SUCCESS -> {
        FetchReputationUseCaseSync.UseCaseResult(
            FetchReputationUseCaseSync.Status.SUCCESS, result.reputation
        )
      }
      GetReputationHttpEndpointSync.EndpointStatus.GENERAL_ERROR -> {
        FetchReputationUseCaseSync.UseCaseResult(
            FetchReputationUseCaseSync.Status.FAILURE, 0
        )
      }
      GetReputationHttpEndpointSync.EndpointStatus.NETWORK_ERROR -> {
        FetchReputationUseCaseSync.UseCaseResult(
            FetchReputationUseCaseSync.Status.NETWORK_ERROR, 0
        )
      }
      else -> {
        return FetchReputationUseCaseSync.UseCaseResult(
            FetchReputationUseCaseSync.Status.FAILURE, 0
        )
      }
    }
  }
}