package com.handybook.handybook.core.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.manager.ReviewAppManager;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.ExternalApplicationLauncher;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.ReviewAppPromptLog;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * holds a simple banner with a title and two buttons that leads user to review the app
 *
 * this is a fragment to follow this app's convention of not
 * allowing manager and bus injections inside custom views
 *
 * add this fragment to any container and it will handle everything
 * including the visibility of the banner view. although there is some
 * computational efficiency due to having to add/create the fragment no matter what,
 * this should make it very easy when we want to include this banner in multiple places
 */
public class ReviewAppBannerFragment extends InjectedFragment {

    public static final String TAG = ReviewAppBannerFragment.class.getName();

    @Bind(R.id.review_app_banner_title)
    TextView mTitle;

    @Bind(R.id.review_app_banner_button_negative)
    Button mNegativeButton;

    @Bind(R.id.review_app_banner_button_positive)
    Button mPositiveButton;

    @Inject
    ReviewAppManager mReviewAppManager;

    @Override
    public View onCreateView(
            final LayoutInflater inflater,
            @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.review_app_banner_view, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void setViewVisible(boolean visible) {
        if (getView() == null) {
            return; //prevent IDE from complaining
        }
        if(visible)
        {
            /*
            when this prompt is set to visible we want the initial prompt state to show
             */
            showAppEnjoymentQuestionPrompt();
            getView().setVisibility(View.VISIBLE);
        }
        else
        {
            getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setViewVisible(mReviewAppManager.shouldDisplayReviewAppBanner());
        /*
        user resumes app after a while but app was not killed

        they might be eligible to see the banner again, OR
        if they navigate to a page or popup that already asks them to rate the app
        they may be ineligible to see this banner when they navigate back to this page
         */
    }

    /**
     * user gave a response that indicates they should not/will not review
     *
     * record the time so that we don't show this prompt again until at least X days later
     * and hide the prompt
     */
    private void onReviewDeclined() {
        mReviewAppManager.updateReviewAppBannerDeclinedTime();
        setViewVisible(false);
    }

    /**
     * prompt that asks if user is enjoying the app
     */
    private void showAppEnjoymentQuestionPrompt() {
        bus.post(new LogEvent.AddLogEvent(new ReviewAppPromptLog.Shown(
                ReviewAppPromptLog.PromptId.APP_ENJOYMENT_QUESTION
        )));

        mTitle.setText(R.string.review_app_banner_enjoyment_question_prompt_title);
        mNegativeButton.setText(R.string.review_app_banner_enjoyment_question_prompt_negative_button);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bus.post(new LogEvent.AddLogEvent(new ReviewAppPromptLog.AnswerSubmitted(
                        ReviewAppPromptLog.PromptId.APP_ENJOYMENT_QUESTION,
                        ReviewAppPromptLog.Answer.NO
                )));
                ReviewAppBannerFragment.this.onReviewDeclined();
            }
        });
        mPositiveButton.setText(R.string.review_app_banner_enjoyment_question_prompt_positive_button);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bus.post(new LogEvent.AddLogEvent(new ReviewAppPromptLog.AnswerSubmitted(
                        ReviewAppPromptLog.PromptId.APP_ENJOYMENT_QUESTION,
                        ReviewAppPromptLog.Answer.YES
                )));
                showReviewAppDirectivePrompt();
            }
        });
    }

    /**
     * prompt that directs user to review the app in the play store
     */
    private void showReviewAppDirectivePrompt() {
        bus.post(new LogEvent.AddLogEvent(new ReviewAppPromptLog.Shown(
                ReviewAppPromptLog.PromptId.REVIEW_APP_IN_STORE_DIRECTIVE
        )));

        mTitle.setText(R.string.review_app_banner_review_directive_prompt_title);
        mNegativeButton.setText(R.string.review_app_banner_review_directive_prompt_negative_button);
        mNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                bus.post(new LogEvent.AddLogEvent(new ReviewAppPromptLog.AnswerSubmitted(
                        ReviewAppPromptLog.PromptId.REVIEW_APP_IN_STORE_DIRECTIVE,
                        ReviewAppPromptLog.Answer.NO
                )));
                ReviewAppBannerFragment.this.onReviewDeclined();
            }
        });
        mPositiveButton.setText(R.string.review_app_banner_review_directive_prompt_positive_button);
        mPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ReviewAppBannerFragment.this.onReviewAppButtonClicked();
            }
        });
    }

    /**
     * launch the play store app listing and
     * mark the app as rated so that we never show this again
     */
    private void onReviewAppButtonClicked() {
        bus.post(new LogEvent.AddLogEvent(new ReviewAppPromptLog.AnswerSubmitted(
                ReviewAppPromptLog.PromptId.REVIEW_APP_IN_STORE_DIRECTIVE,
                ReviewAppPromptLog.Answer.YES
        )));
        ExternalApplicationLauncher.launchPlayStoreAppListing(getContext());
        mReviewAppManager.markAppAsRated();
        setViewVisible(false);
    }
}
