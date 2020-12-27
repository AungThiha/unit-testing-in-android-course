package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.BaseObservable;
import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

public class FetchQuestionDetailsUseCase extends BaseObservable<FetchQuestionDetailsUseCase.Listener> {

    public static final long TIMEOUT_MILLISECONDS = 60 * 1000;

    public interface Listener {
        void onQuestionDetailsFetched(QuestionDetails questionDetails);
        void onQuestionDetailsFetchFailed();
    }

    private final FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpoint;
    private final TimeProvider mTimeProvider;
    private long lastSavedTime = 0;
    private QuestionDetails lastFetchedQuestionDetails = null;

    public FetchQuestionDetailsUseCase(FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint,
                                       TimeProvider timeProvider) {
        mFetchQuestionDetailsEndpoint = fetchQuestionDetailsEndpoint;
        mTimeProvider = timeProvider;
    }

    public void fetchQuestionDetailsAndNotify(String questionId) {
        if (lastFetchedQuestionDetails != null && mTimeProvider.getCurrentTimestamp() - lastSavedTime < TIMEOUT_MILLISECONDS) {
            notifySuccess(lastFetchedQuestionDetails);
        } else {
            lastFetchedQuestionDetails = null;
            mFetchQuestionDetailsEndpoint.fetchQuestionDetails(questionId, new FetchQuestionDetailsEndpoint.Listener() {
                @Override
                public void onQuestionDetailsFetched(QuestionSchema question) {
                    lastFetchedQuestionDetails = new QuestionDetails(
                            question.getId(),
                            question.getTitle(),
                            question.getBody()
                    );
                    lastSavedTime = mTimeProvider.getCurrentTimestamp();
                    notifySuccess(lastFetchedQuestionDetails);
                }

                @Override
                public void onQuestionDetailsFetchFailed() {
                    notifyFailure();
                }
            });
        }
    }

    private void notifyFailure() {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetchFailed();
        }
    }

    private void notifySuccess(QuestionDetails questionDetails) {
        for (Listener listener : getListeners()) {
            listener.onQuestionDetailsFetched(questionDetails);
        }
    }
}
