package com.techyourchance.unittesting.questions

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.techyourchance.unittesting.common.time.TimeProvider
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint
import com.techyourchance.unittesting.networking.questions.QuestionSchema
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify

class FetchQuestionDetailsUseCaseTest {

    companion object {
        private const val QUESTION_ID = "question_id"
        private const val QUESTION_TITLE = "title"
        private const val QUESTION_BODY = "body"
        private val QUESTION_SCHEME = QuestionSchema(QUESTION_TITLE, QUESTION_ID, QUESTION_BODY)
        private val QUESTION_DETAILS = QuestionDetails(QUESTION_ID, QUESTION_TITLE, QUESTION_BODY)
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

    // region helps

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

    // endregion helpers

}