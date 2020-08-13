package com.techyourchance.testdrivendevelopment.exercise6

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException
import com.techyourchance.testdrivendevelopment.exercise6.users.User
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache

class FetchUserUseCaseSyncImpl(
    private val fetchUserHttpEndpointSync: FetchUserHttpEndpointSync,
    private val usersCache: UsersCache
) : FetchUserUseCaseSync {

  override fun fetchUserSync(userId: String): FetchUserUseCaseSync.UseCaseResult {
    val cachedUser = usersCache.getUser(userId)
    return if (cachedUser == null) {
      fetchFromHttp(userId)
    } else {
      FetchUserUseCaseSync.UseCaseResult(
          FetchUserUseCaseSync.Status.SUCCESS,
          cachedUser
      )
    }
  }

  private fun fetchFromHttp(userId: String): FetchUserUseCaseSync.UseCaseResult = try {
    val result = fetchUserHttpEndpointSync.fetchUserSync(userId)
    if (result.status == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS) {
      val user = User(result.userId, result.username)
      usersCache.cacheUser(user)
      FetchUserUseCaseSync.UseCaseResult(
          FetchUserUseCaseSync.Status.SUCCESS,
          user
      )
    } else {
      FetchUserUseCaseSync.UseCaseResult(
          FetchUserUseCaseSync.Status.FAILURE,
          null
      )
    }
  } catch (e: NetworkErrorException) {
    FetchUserUseCaseSync.UseCaseResult(
        FetchUserUseCaseSync.Status.NETWORK_ERROR,
        null
    )
  }

}