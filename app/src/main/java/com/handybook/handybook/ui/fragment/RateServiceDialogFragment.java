package com.handybook.handybook.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handybook.handybook.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RateServiceDialogFragment extends DialogFragment {
    static final String EXTRA_RATING = "com.handy.handy.EXTRA_RATING";
    private static final String STATE_RATING = "RATING";

    private ArrayList<ImageView> stars = new ArrayList<>();
    private int rating;

    @InjectView(R.id.service_icon) ImageView serviceIcon;
    @InjectView(R.id.title_text) TextView titleText;
    @InjectView(R.id.message_text) TextView messageText;
    @InjectView(R.id.submit_button) Button submitButton;
    @InjectView(R.id.ratings_layout) LinearLayout ratingsLayout;
    @InjectView(R.id.star_1) ImageView star1;
    @InjectView(R.id.star_2) ImageView star2;
    @InjectView(R.id.star_3) ImageView star3;
    @InjectView(R.id.star_4) ImageView star4;
    @InjectView(R.id.star_5) ImageView star5;

    public static RateServiceDialogFragment newInstance(final int rating) {
        final RateServiceDialogFragment rateServiceDialogFragment = new RateServiceDialogFragment();
        final Bundle bundle = new Bundle();

        bundle.putInt(EXTRA_RATING, rating);
        rateServiceDialogFragment .setArguments(bundle);
        return rateServiceDialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        this.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        return new Dialog(getActivity());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        getDialog().setCancelable(false);
        getDialog().setCanceledOnTouchOutside(false);

        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(final DialogInterface dialog, final int keyCode,
                                 final KeyEvent event) {
                return true;
            }
        });

        final View view = inflater.inflate(R.layout.dialog_rate_service, container, true);
        ButterKnife.inject(this, view);

        if (savedInstanceState != null) {
            rating = savedInstanceState.getInt(STATE_RATING , -1);
        }
        else {
            final Bundle args = getArguments();
            if (args != null) rating = args.getInt(EXTRA_RATING, -1);
            else rating = -1;
        }

        initStars();
        setRating(rating);

        serviceIcon.setColorFilter(getResources().getColor(R.color.handy_green),
                PorterDuff.Mode.SRC_ATOP);

        titleText.setText(getResources().getString(R.string.how_was_service));
        messageText.setText(getResources().getString(R.string.please_rate_pro));

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_RATING, rating);
    }

    public int getCurrentRating() {
        return rating;
    }

    private void initStars() {
        stars.add(star1);
        stars.add(star2);
        stars.add(star3);
        stars.add(star4);
        stars.add(star5);

        // init all stars to empty
        for (final ImageView star : stars) {
            star.setColorFilter(getResources().getColor(R.color.light_grey),
                    PorterDuff.Mode.SRC_ATOP);
        }

        // fill stars when dragging across them
        ratingsLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                for(int i = 0; i < ratingsLayout.getChildCount(); i++) {
                    final RelativeLayout layout = (RelativeLayout)ratingsLayout.getChildAt(i);
                    final Rect outRect = new Rect(layout.getLeft(), layout.getTop(),
                            layout.getRight(), layout.getBottom());

                    if (outRect.contains((int)event.getX(), (int)event.getY())) {
                        final int starsIndex = stars.indexOf((ImageView)layout.getChildAt(0));
                        setRating(starsIndex);
                        break;
                    }
                }

                return true;
            }
        });
    }

    private void setRating(final int rating) {
        this.rating = rating;

        for (int j = 0; j < stars.size(); j++) {
            final ImageView star = stars.get(j);

            if (j <= rating) star.clearColorFilter();
            else star.setColorFilter(getResources().getColor(R.color.light_grey),
                    PorterDuff.Mode.SRC_ATOP);
        }

        if (rating > 0) submitButton.setVisibility(View.VISIBLE);
    }
}
