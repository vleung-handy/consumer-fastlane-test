package com.handybook.handybook.proprofiles.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.library.util.TextUtils;
import com.handybook.handybook.proprofiles.model.ProProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * lists information about pro, such as their years of experience
 * and whether they are background-checked
 */
public class ProProfileAboutView extends FrameLayout {

    @BindView(R.id.pro_profile_stats_items_container)
    LinearLayout mStatsItemsContainer;

    public ProProfileAboutView(@NonNull final Context context) {
        super(context);
        init();
    }

    public ProProfileAboutView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs
    ) {
        super(context, attrs);
        init();
    }

    public ProProfileAboutView(
            @NonNull final Context context,
            @Nullable final AttributeSet attrs,
            @AttrRes final int defStyleAttr
    ) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.layout_pro_profile_about, this);
        ButterKnife.bind(this);
    }

    public void updateWithModel(@NonNull ProProfile proProfile) {
        mStatsItemsContainer.removeAllViews();

        //use provider stats model to populate the layout
        ProProfile.ProviderStats providerStats = proProfile.getProviderStats();

        if (proProfile.getProviderRegionsServed() != null
            && proProfile.getProviderRegionsServed().length > 0) {
            String locationsServed = android.text.TextUtils.join(
                    ", ",
                    proProfile.getProviderRegionsServed()
            );
            mStatsItemsContainer.addView(createProStatsItem(
                    getResources().getString(
                            R.string.pro_profile_about_page_locations_served_formatted,
                            locationsServed
                    ),
                    R.drawable.ic_location_pin
            ));
        }

        if (!TextUtils.isBlank(providerStats.getDurationProExperienceFormatted())) {
            String formattedExperience = providerStats.getDurationProExperienceFormatted();
            mStatsItemsContainer.addView(createProStatsItem(
                    formattedExperience,
                    R.drawable.ic_checkbox
            ));
        }

        if (!TextUtils.isBlank(providerStats.getDurationExperienceHandyFormatted())) {
            String formattedExperience = providerStats.getDurationExperienceHandyFormatted();
            mStatsItemsContainer.addView(createProStatsItem(
                    formattedExperience,
                    R.drawable.ic_experience_badge
            ));
        }
        if (providerStats.getBackgroundChecked() != null && providerStats.getBackgroundChecked()) {
            mStatsItemsContainer.addView(createProStatsItem(
                    getResources().getString(R.string.pro_profile_about_page_background_checked),
                    R.drawable.ic_background_check
            ));
        }
    }

    private TextView createProStatsItem(@NonNull String textString, int drawableResourceId) {
        TextView textView = new TextView(getContext(), null, R.style.TextView_ProProfileAboutItem);
        textView.setText(textString);
        Drawable iconDrawable = ContextCompat.getDrawable(getContext(), drawableResourceId);

        textView.setCompoundDrawablesWithIntrinsicBounds(
                iconDrawable,
                null,
                null,
                null
        );

        textView.setCompoundDrawablePadding(
                getResources().getDimensionPixelOffset(R.dimen.pro_profile_about_page_icon_padding));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );

        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        layoutParams.bottomMargin
                = getResources().getDimensionPixelSize(R.dimen.default_margin_half);
        textView.setLayoutParams(layoutParams);
        return textView;

    }
}
