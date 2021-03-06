package com.handybook.handybook.core.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.view.ProBusyView;
import com.handybook.handybook.booking.util.BookingUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookingListItem extends FrameLayout {

    private Booking mBooking;

    @BindView(R.id.booking_item_image_icon)
    ImageView mImageIcon;
    @BindView(R.id.booking_item_title)
    TextView mTextBookingTitle;
    @BindView(R.id.booking_item_subtitle)
    TextView mTextBookingSubtitle;
    @BindView(R.id.booking_item_pro_busy_banner)
    ProBusyView mProBusyView;
    @BindView(R.id.booking_item_container)
    View mBookingItemContainer;

    private View.OnClickListener mOnClickListener;
    private View.OnClickListener mBannerOnClickListener;

    /**
     * used to determine how to display the subtitle
     */
    private final boolean mIsBookingHoursClarificationExperimentEnabled;

    /**
     * @param isBookingHoursClarificationExperimentEnabled this is passed here because don't want to
     *                                                     pass the entire config object or inject
     *                                                     config manager, and don't want to pass it
     *                                                     to bindToBooking() since it should be
     *                                                     constant throughout this item's lifetime
     */
    public BookingListItem(
            Context context,
            View.OnClickListener clickListener,
            @Nullable View.OnClickListener bannerOnClickListener,
            Booking booking,
            boolean isBookingHoursClarificationExperimentEnabled
    ) {
        super(context);
        mOnClickListener = clickListener;
        mBannerOnClickListener = bannerOnClickListener;
        mBooking = booking;
        mIsBookingHoursClarificationExperimentEnabled
                = isBookingHoursClarificationExperimentEnabled;
        init();
    }

    void init() {
        inflate(getContext(), R.layout.layout_booking_list_item, this);
        ButterKnife.bind(this);
        bindToBooking(mBooking);
        setProBusyBanner();
    }

    public void setClickListener(
            final OnClickListener onClickListener,
            @Nullable final OnClickListener bannerOnClickListener
    ) {
        mOnClickListener = onClickListener;
        mBannerOnClickListener = bannerOnClickListener;
    }

    public void bindToBooking(Booking booking) {
        mBooking = booking;
        if (mBooking == null) {
            return;
        }

        mImageIcon.setVisibility(View.VISIBLE);
        mImageIcon.setImageResource(BookingUtil.getIconForService(
                mBooking,
                BookingUtil.IconType.OUTLINE
        ));

        mTextBookingTitle.setText(BookingUtil.getTitle(mBooking));
        mTextBookingSubtitle.setText(BookingUtil.getSubtitle(
                mBooking,
                getContext(),
                mIsBookingHoursClarificationExperimentEnabled
        ));

        mBookingItemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v);
                }
            }
        });
    }

    private void setProBusyBanner() {
        if (mBooking != null &&
            mBooking.getProviderRequest() != null &&
            mBooking.getProviderRequest().getProvider() != null) {
            mProBusyView.setVisibility(VISIBLE);
            mProBusyView.setDisplay(
                    mBooking.getProviderRequest().getProvider().getImageUrl(),
                    mBooking.getProviderRequest().getProvider().getFirstNameAndLastInitial(),
                    mBannerOnClickListener
            );
        }
        else {
            mProBusyView.setVisibility(View.GONE);
        }
    }

    public Booking getBooking() {
        return mBooking;
    }
}
