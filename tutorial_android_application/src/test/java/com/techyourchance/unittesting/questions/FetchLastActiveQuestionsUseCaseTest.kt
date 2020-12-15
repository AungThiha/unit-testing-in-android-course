package com.techyourchance.unittesting.questions

import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint
import com.techyourchance.unittesting.networking.questions.QuestionSchema
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.*
import java.util.*

class FetchLastActiveQuestionsUseCaseTest {

    lateinit var SUT: FetchLastActiveQuestionsUseCase
    private lateinit var endPointTd: EndPointTd

    @Mock
    lateinit var listener1: FetchLastActiveQuestionsUseCase.Listener

    @Mock
    lateinit var listener2: FetchLastActiveQuestionsUseCase.Listener

    @Captor
    lateinit var questionsCaptor: ArgumentCaptor<List<Question>>

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        endPointTd = EndPointTd()
        SUT = FetchLastActiveQuestionsUseCase(endPointTd)
    }

    @Test
    fun fetchLastActiveQuestionsAndNotify_success_listenerNotifiedWithCorrectData() {
        success()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)

        SUT.fetchLastActiveQuestionsAndNotify()

        Mockito.verify(listener1).onLastActiveQuestionsFetched(questionsCaptor.capture())
        Mockito.verify(listener2).onLastActiveQuestionsFetched(questionsCaptor.capture())
        val questions = questionsCaptor.allValues
        Assert.assertEquals(questions[0], createExpectedQuestions())
        Assert.assertEquals(questions[1], createExpectedQuestions())
    }

    @Test
    fun fetchLastActiveQuestionsAndNotify_failure_listenerNotifiedOfFailure() {
        failure()
        SUT.registerListener(listener1)
        SUT.registerListener(listener2)

        SUT.fetchLastActiveQuestionsAndNotify()

        Mockito.verify(listener1).onLastActiveQuestionsFetchFailed()
        Mockito.verify(listener2).onLastActiveQuestionsFetchFailed()
    }

    private fun createExpectedQuestions(): List<Question> {
        val questions: MutableList<Question> = LinkedList()
        questions.add(Question("id1", "title1"))
        questions.add(Question("id2", "title2"))
        return questions
    }

    private fun success() {
        // nothing to do here. just a placeholder
    }

    private fun failure() {
        endPointTd.failure = true
    }

    // region helper class
    private class EndPointTd : FetchLastActiveQuestionsEndpoint(null) {
        var failure: Boolean = false

        override fun fetchLastActiveQuestions(listener: Listener) {
            if (failure) {
                listener.onQuestionsFetchFailed()
            } else {
                val questionSchemas: MutableList<QuestionSchema> = LinkedList()
                questionSchemas.add(QuestionSchema("title1", "id1", "body1"))
                questionSchemas.add(QuestionSchema("title2", "id2", "body2"))
                listener.onQuestionsFetched(questionSchemas)
            }
        }
    } // endregion helper class
}