package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.BaseObservable;
import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import java.util.HashMap;

public class FetchQuestionDetailsUseCase extends BaseObservable<FetchQuestionDetailsUseCase.Listener> {

    public static final long TIMEOUT_MILLISECONDS = 60 * 1000;

    public interface Listener {
        void onQuestionDetailsFetched(QuestionDetails questionDetails);
        void onQuestionDetailsFetchFailed();
    }

    private final FetchQuestionDetailsEndpoint mFetchQuestionDetailsEndpoint;
    private final TimeProvider mTimeProvider;
    private HashMap<String, QuestionDetailsCache> cacheList = new HashMap<>();

    public FetchQuestionDetailsUseCase(FetchQuestionDetailsEndpoint fetchQuestionDetailsEndpoint,
                                       TimeProvider timeProvider) {
        mFetchQuestionDetailsEndpoint = fetchQuestionDetailsEndpoint;
        mTimeProvider = timeProvider;
    }

    public void fetchQuestionDetailsAndNotify(String questionId) {
        QuestionDetailsCache cache = cacheList.get(questionId);
        if (cache != null && mTimeProvider.getCurrentTimestamp() - cache.getSavedWhen() < TIMEOUT_MILLISECONDS) {
            notifySuccess(cache.getQuestionDetails());
        } else {
            cacheList.remove(questionId);
            mFetchQuestionDetailsEndpoint.fetchQuestionDetails(questionId, new FetchQuestionDetailsEndpoint.Listener() {
                @Override
                public void onQuestionDetailsFetched(QuestionSchema question) {
                    QuestionDetails questionDetails = new QuestionDetails(
                            question.getId(),
                            question.getTitle(),
                            question.getBody()
                    );
                    cacheList.put(
                            questionDetails.getId(), new QuestionDetailsCache(mTimeProvider.getCurrentTimestamp(), questionDetails)
                    );
                    notifySuccess(questionDetails);
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
