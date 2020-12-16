package com.techyourchance.unittesting.questions

import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint
import com.techyourchance.unittesting.networking.questions.QuestionSchema
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.*


class FetchQuestionDetailsUseCaseTest {

    companion object {
        private const val QUESTION_ID = "id"
        private const val QUESTION_TITLE = "title"
        private const val QUESTION_BODY = "body"
    }

    lateinit var SUT: FetchQuestionDetailsUseCase
    private lateinit var endPointTd: EndPointTd

    @Mock
    lateinit var listener1: FetchQuestionDetailsUseCase.Listener

    @Mock
    lateinit var listener2: FetchQuestionDetailsUseCase.Listener

    @Captor
    lateinit var questionDetailsCaptor: ArgumentCaptor<QuestionDetails>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        endPointTd = EndPointTd()
        SUT = FetchQuestionDetailsUseCase(endPointTd)
    }

    @Test
    fun fetchQuestionDetailsAndNotify_success_listenerNotifiedWithCorrectData() {
        success()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        Mockito.verify(listener1).onQuestionDetailsFetched(questionDetailsCaptor.capture())
        Mockito.verify(listener2).onQuestionDetailsFetched(questionDetailsCaptor.capture())
        val questionDetailsList = questionDetailsCaptor.allValues

        assertEquals(createQuestionDetails(), questionDetailsList[0])
        assertEquals(createQuestionDetails(), questionDetailsList[1])
    }

    @Test
    fun fetchQuestionDetailsAndNotify_failure_listenerOnFailedCalled() {
        failure()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)

        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID)

        Mockito.verify(listener1).onQuestionDetailsFetchFailed()
        Mockito.verify(listener2).onQuestionDetailsFetchFailed()
    }

    private fun success() {
        // nothing to do. just a placeholder
    }

    private fun failure() {
        endPointTd.failure = true
    }

    private fun createQuestionDetails() = QuestionDetails(
            QUESTION_ID, QUESTION_TITLE, QUESTION_BODY
    )

    // region helper class
    private class EndPointTd : FetchQuestionDetailsEndpoint(null) {

        var failure: Boolean = false

        override fun fetchQuestionDetails(questionId: String, listener: Listener) {
            if (failure) {
                listener.onQuestionDetailsFetchFailed()
            } else {
                listener.onQuestionDetailsFetched(QuestionSchema(QUESTION_TITLE, QUESTION_ID, QUESTION_BODY))
            }
        }
    }
    // endregion helper class

}