package com.techyourchance.testdoublesfundamentals.exercise4

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync
import com.techyourchance.testdoublesfundamentals.exercise4.users.User
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FetchUserProfileUseCaseSyncTest {

  private lateinit var fetchUserProfileUseCaseSync: FetchUserProfileUseCaseSync
  private lateinit var userProfileHttpEndpointSyncTd: UserProfileHttpEndpointSyncTd
  private lateinit var usersCacheTd: UsersCacheTd

  private val USER_ID = "userId"
  private val FULL_NAME ="fullname"
  private val IMAGE_URL = "imageUrl"

  @Before
  fun setup() {
    userProfileHttpEndpointSyncTd = UserProfileHttpEndpointSyncTd()
    usersCacheTd = UsersCacheTd()
    fetchUserProfileUseCaseSync = FetchUserProfileUseCaseSync(
        userProfileHttpEndpointSyncTd,
        usersCacheTd
    )
  }

  @Test
  fun fetchUserProfileSync_success_userIdPassedToEndPoint() {
    fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    assertEquals(USER_ID, userProfileHttpEndpointSyncTd.userId)
  }

  @Test
  fun fetchUserProfileSync_success_userCached() {
    fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    val user = usersCacheTd.getUser(USER_ID)
    assertNotNull(user)
    assertEquals(USER_ID, user!!.userId)
    assertEquals(FULL_NAME, user.fullName)
    assertEquals(IMAGE_URL, user.imageUrl)
  }

  @Test
  fun fetchUserProfileSync_authError_userNotCached() {
    userProfileHttpEndpointSyncTd.resultStatus =
        UserProfileHttpEndpointSync.EndpointResultStatus.AUTH_ERROR
    fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    val user = usersCacheTd.getUser(USER_ID)
    assertNull(user)
  }

  @Test
  fun fetchUserProfileSync_serverError_userNotCached() {
    userProfileHttpEndpointSyncTd.resultStatus =
        UserProfileHttpEndpointSync.EndpointResultStatus.SERVER_ERROR
    fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    val user = usersCacheTd.getUser(USER_ID)
    assertNull(user)
  }

  @Test
  fun fetchUserProfileSync_generalError_userNotCached() {
    userProfileHttpEndpointSyncTd.resultStatus =
        UserProfileHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR
    fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    val user = usersCacheTd.getUser(USER_ID)
    assertNull(user)
  }

  @Test
  fun fetchUserProfileSync_networkError_userNotCached() {
    userProfileHttpEndpointSyncTd.resultStatus = null
    fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    val user = usersCacheTd.getUser(USER_ID)
    assertNull(user)
  }

  @Test
  fun fetchUserProfileSync_success_successReturned() {
    val result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    assertEquals(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS, result)
  }

  @Test
  fun fetchUserProfileSync_authError_failureReturned() {
    userProfileHttpEndpointSyncTd.resultStatus =
        UserProfileHttpEndpointSync.EndpointResultStatus.AUTH_ERROR
    val result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    assertEquals(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE, result)
  }

  @Test
  fun fetchUserProfileSync_serverError_failureReturned() {
    userProfileHttpEndpointSyncTd.resultStatus =
        UserProfileHttpEndpointSync.EndpointResultStatus.SERVER_ERROR
    val result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    assertEquals(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE, result)
  }

  @Test
  fun fetchUserProfileSync_generalError_failureReturned() {
    userProfileHttpEndpointSyncTd.resultStatus =
        UserProfileHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR
    val result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    assertEquals(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE, result)
  }

  @Test
  fun fetchUserProfileSync_networkError_networkErrorReturned() {
    userProfileHttpEndpointSyncTd.resultStatus = null
    val result = fetchUserProfileUseCaseSync.fetchUserProfileSync(USER_ID)
    assertEquals(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR, result)
  }

  ///////////////////////////////////////////
  ////// Helper Class
  //////////////////////////////////////////

  inner class UserProfileHttpEndpointSyncTd: UserProfileHttpEndpointSync {

    var userId: String = ""
    var resultStatus: UserProfileHttpEndpointSync.EndpointResultStatus? =
        UserProfileHttpEndpointSync.EndpointResultStatus.SUCCESS

    override fun getUserProfile(userId: String): UserProfileHttpEndpointSync.EndpointResult {
      this.userId = userId
      return when (resultStatus) {
        null -> {
          throw NetworkErrorException()
        }
        UserProfileHttpEndpointSync.EndpointResultStatus.SUCCESS -> {
          UserProfileHttpEndpointSync.EndpointResult(
              UserProfileHttpEndpointSync.EndpointResultStatus.SUCCESS,
              userId,
              FULL_NAME,
              IMAGE_URL
          )
        }
        else -> {
          UserProfileHttpEndpointSync.EndpointResult(
              resultStatus,
              "",
              "",
              ""
          )
        }
      }
    }

  }

  class UsersCacheTd: UsersCache {
    var user: User? = null
    override fun cacheUser(user: User) {
      this.user = user
    }

    override fun getUser(userId: String): User? {
      return user
    }
  }
}