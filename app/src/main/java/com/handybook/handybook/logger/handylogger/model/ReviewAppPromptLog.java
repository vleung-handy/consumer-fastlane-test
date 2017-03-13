package com.handybook.handybook.logger.handylogger.model;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.handybook.handybook.logger.handylogger.model.ReviewAppPromptLog.Answer.NO;
import static com.handybook.handybook.logger.handylogger.model.ReviewAppPromptLog.Answer.YES;
import static com.handybook.handybook.logger.handylogger.model.ReviewAppPromptLog.PromptId.APP_ENJOYMENT_QUESTION;
import static com.handybook.handybook.logger.handylogger.model.ReviewAppPromptLog.PromptId.REVIEW_APP_IN_STORE_DIRECTIVE;

public abstract class ReviewAppPromptLog extends EventLog {

    private static final String EVENT_CONTEXT = "app_rating_prompt";


    public static class Answer {

        public static final String YES = "yes";
        public static final String NO = "no";
    }


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
                       YES,
                       NO
               })
    @interface AppRatingPromptAnswer {
    }


    public static class PromptId {

        public static final int APP_ENJOYMENT_QUESTION = 1;
        public static final int REVIEW_APP_IN_STORE_DIRECTIVE = 2;
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
                    APP_ENJOYMENT_QUESTION,
                    REVIEW_APP_IN_STORE_DIRECTIVE
            })
    @interface AppRatingPromptId {
    }


    @SerializedName("prompt_number")
    private final int mPromptId;

    public ReviewAppPromptLog(final String eventType, @AppRatingPromptId final int promptId) {
        super(eventType, EVENT_CONTEXT);
        mPromptId = promptId;
    }

    /**
     * rating is submitted
     */
    public static class Shown extends ReviewAppPromptLog {

        private static final String EVENT_TYPE = "prompt_shown";

        public Shown(
                @AppRatingPromptId int promptId
        ) {
            super(EVENT_TYPE, promptId);
        }
    }


    public static class AnswerSubmitted extends ReviewAppPromptLog {

        private static final String EVENT_TYPE = "answer_submitted";

        @SerializedName("answer_type")
        private final String mAnswer;

        public AnswerSubmitted(
                @AppRatingPromptId final int promptId,
                @AppRatingPromptAnswer final String answer
        ) {
            super(EVENT_TYPE, promptId);
            mAnswer = answer;
        }
    }
}
