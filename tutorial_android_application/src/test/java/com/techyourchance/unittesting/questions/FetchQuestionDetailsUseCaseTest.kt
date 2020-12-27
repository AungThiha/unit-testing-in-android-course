package com.techyourchance.unittesting.questions

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.techyourchance.unittesting.common.time.TimeProvider
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint
import com.techyourchance.unittesting.networking.questions.QuestionSchema
import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase.TIMEOUT_MILLISECONDS
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class FetchQuestionDetailsUseCaseTest {

    companion object {
        private const val QUESTION_ID = "question_id"
        private const val QUESTION_TITLE = "title"
        private const val QUESTION_BODY = "body"
        private val QUESTION_SCHEME = QuestionSchema(QUESTION_TITLE, QUESTION_ID, QUESTION_BODY)
        private val QUESTION_DETAILS = QuestionDetails(QUESTION_ID, QUESTION_TITLE, QUESTION_BODY)

        private const val QUESTION_ID_2 = "question_id2"
        private const val QUESTION_TITLE_2 = "title2"
        private const val QUESTION_BODY_2 = "body2"
        private val QUESTION_SCHEME_2 = QuestionSchema(QUESTION_TITLE_2, QUESTION_ID_2, QUESTION_BODY_2)
        private val QUESTION_DETAILS_2 = QuestionDetails(QUESTION_ID_2, QUESTION_TITLE_2, QUESTION_BODY_2)
    }

    private val fetchQuestionDetailsEndpoint = mock<FetchQuestionDetailsEndpoint>()
    private val timeProvider = mock<TimeProvider>()
    private val listener1 = mock<FetchQuestionDetailsUseCase.Listener>()
    private val listener2 = mock<FetchQuestionDetailsUseCase.Listener>()

    private lateinit var SUT: FetchQuestionDetailsUseCase

    @Before
    fun setUp() {
        SUT = FetchQuestionDetailsUseCase(
                fetchQuestionDetailsEndpoint,
                timeProvider
        )
    }

    @Test
    fun fetchQuestionDetailsAndNotify_correctArgumentsPassed() {
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        verify(fetchQuestionDetailsEndpoint).fetchQuestionDetails(eq(QUESTION_ID), any())
    }

    @Test
    fun success_fetchQuestionDetailsAndNotify_questionsNotified() {
        success()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        verify(listener1).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2).onQuestionDetailsFetched(QUESTION_DETAILS)
    }

    @Test
    fun failure_fetchQuestionDetailsAndNotify_fetchFailed() {
        failure()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        verify(listener1).onQuestionDetailsFetchFailed()
        verify(listener2).onQuestionDetailsFetchFailed()
    }

    @Test
    fun previousValuesWithinTimeout_fetchQuestionDetailsAndNotify_sameValuesReturned() {
        success()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)
        `when`(timeProvider.currentTimestamp).thenReturn(100)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS - 1)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(fetchQuestionDetailsEndpoint, times(1)).fetchQuestionDetails(eq(QUESTION_ID), any())
    }

    @Test
    fun previousValuesEqualsTimeout_fetchQuestionDetailsAndNotify_sameValuesReturned() {
        success()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)
        `when`(timeProvider.currentTimestamp).thenReturn(100)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(fetchQuestionDetailsEndpoint, times(2)).fetchQuestionDetails(eq(QUESTION_ID), any())
    }

    @Test
    fun previousValuesMoreThanTimeout_fetchQuestionDetailsAndNotify_sameValuesReturned() {
        success()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)
        `when`(timeProvider.currentTimestamp).thenReturn(100)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS + 1)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(fetchQuestionDetailsEndpoint, times(2)).fetchQuestionDetails(eq(QUESTION_ID), any())
    }

    @Test
    fun previousValuesDifferentIdWithinTimeout_fetchQuestionDetailsAndNotify_sameValuesReturned() {
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)
        success()
        success2()
        `when`(timeProvider.currentTimestamp).thenReturn(100)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS - 1)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS - 1 + TIMEOUT_MILLISECONDS - 1)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2)

        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(fetchQuestionDetailsEndpoint).fetchQuestionDetails(eq(QUESTION_ID), any())
        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_2)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_2)
        verify(fetchQuestionDetailsEndpoint).fetchQuestionDetails(eq(QUESTION_ID_2), any())
    }

    @Test
    fun previousValuesDifferentIdEqualsTimeout_fetchQuestionDetailsAndNotify_sameValuesReturned() {
        success()
        success2()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)
        `when`(timeProvider.currentTimestamp).thenReturn(100)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS + TIMEOUT_MILLISECONDS)


        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2)

        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(fetchQuestionDetailsEndpoint, times(2)).fetchQuestionDetails(eq(QUESTION_ID), any())
        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_2)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_2)
        verify(fetchQuestionDetailsEndpoint, times(2)).fetchQuestionDetails(eq(QUESTION_ID_2), any())
    }

    @Test
    fun previousValuesDifferentIdMoreThanTimeout_fetchQuestionDetailsAndNotify_sameValuesReturned() {
        success()
        success2()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)
        `when`(timeProvider.currentTimestamp).thenReturn(100)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS + 1)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2)
        `when`(timeProvider.currentTimestamp).thenReturn(100 + TIMEOUT_MILLISECONDS + 1 + TIMEOUT_MILLISECONDS + 1)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID_2)

        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS)
        verify(fetchQuestionDetailsEndpoint, times(2)).fetchQuestionDetails(eq(QUESTION_ID), any())
        verify(listener1, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_2)
        verify(listener2, times(2)).onQuestionDetailsFetched(QUESTION_DETAILS_2)
        verify(fetchQuestionDetailsEndpoint, times(2)).fetchQuestionDetails(eq(QUESTION_ID_2), any())
    }

    // region helpers

    private fun failure() {
        doAnswer {
            val listener = it.arguments[1] as FetchQuestionDetailsEndpoint.Listener
            listener.onQuestionDetailsFetchFailed()
        }.`when`(fetchQuestionDetailsEndpoint).fetchQuestionDetails(eq(QUESTION_ID), any())
    }

    private fun success() {
        doAnswer {
            val listener = it.arguments[1] as FetchQuestionDetailsEndpoint.Listener
            listener.onQuestionDetailsFetched(QUESTION_SCHEME)
        }.`when`(fetchQuestionDetailsEndpoint).fetchQuestionDetails(eq(QUESTION_ID), any())
    }

    private fun success2() {
        doAnswer {
            val listener = it.arguments[1] as FetchQuestionDetailsEndpoint.Listener
            listener.onQuestionDetailsFetched(QUESTION_SCHEME_2)
        }.`when`(fetchQuestionDetailsEndpoint).fetchQuestionDetails(eq(QUESTION_ID_2), any())
    }

    // endregion helpers

}