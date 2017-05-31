package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.view.View;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditHoursActivity;
import com.handybook.handybook.booking.constant.BookingAction;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.ui.fragment.BookingDetailFragment;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionBookingActionsView;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.constant.ActivityResult;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.logger.handylogger.LogEvent;
import com.handybook.handybook.logger.handylogger.model.booking.BookingDetailsLog;
import com.handybook.handybook.proteam.manager.ProTeamManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class BookingDetailSectionFragmentBookingActions
        extends BookingDetailSectionFragment<BookingDetailSectionBookingActionsView> {

    @Inject
    ProTeamManager mProTeamManager;

    @Override
    protected int getFragmentResourceId() {
        return R.layout.fragment_booking_detail_section_booking_actions;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideProgressSpinner();
    }

    @Override
    public void updateDisplay(Booking booking, User user) {
        super.updateDisplay(booking, user);
        getSectionView().getEntryText().setVisibility(View.GONE);
        getSectionView().getEntryActionText().setVisibility(View.GONE);
        getSectionView().getEntryTitle().setVisibility(View.GONE);
    }

    //Setup the contact booking action buttons
    @Override
    protected List<String> getActionButtonTypeList(Booking booking) {
        List<String> actionButtonTypes = new ArrayList<>();
        if (!booking.isPast()) {
            actionButtonTypes.add(BookingAction.ACTION_RESCHEDULE);
            if (booking.canEditHours()) {
                actionButtonTypes.add(BookingAction.ACTION_EDIT_HOURS);
            }
            if (booking.isRecurring()) {
                actionButtonTypes.add(BookingAction.ACTION_SKIP);
            }
            else {
                actionButtonTypes.add(BookingAction.ACTION_CANCEL);
            }
        }
        return actionButtonTypes;
    }

    @Override
    protected View.OnClickListener getOnClickListenerForAction(String actionButtonType) {
        switch (actionButtonType) {
            case BookingAction.ACTION_SKIP:
                return cancelClicked; // This is not a typo, had to change the label to skip :|
            case BookingAction.ACTION_CANCEL:
                return cancelClicked;
            case BookingAction.ACTION_RESCHEDULE:
                return rescheduleClicked;
            case BookingAction.ACTION_EDIT_HOURS:
                return editHoursClicked;
        }
        return null;
    }

    private View.OnClickListener cancelClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            BookingDetailFragment parentFragment = (BookingDetailFragment) getParentFragment();

            if (parentFragment != null &&
                mConfigurationManager.getPersistentConfiguration().isShowRescheduleFlowOnCancel()) {
                //interrupt the cancellation process by asking whether the user wants to reschedule instead.
                parentFragment.setRescheduleType(BookingDetailFragment.RescheduleType.FROM_CANCELATION);
                bus.post(new BookingEvent.RequestPreRescheduleInfo(booking.getId()));
            }
            else {
                //if there were no configuration suggesting rescheduling, then proceed with normal cancellation
                bus.post(new BookingEvent.RequestBookingCancellationData(booking.getId()));
            }
        }
    };

    private View.OnClickListener rescheduleClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            //log that this was clicked.
            bus.post(new LogEvent.AddLogEvent(new BookingDetailsLog.RescheduleBookingSelectedLog(
                    booking.getProvider(),
                    booking.getId(),
                    booking.getRecurringId()
            )));

            if (getParentFragment() != null) {
                showBlockingProgressSpinner();
                ((BookingDetailFragment) getParentFragment()).setRescheduleType(
                        BookingDetailFragment.RescheduleType.NORMAL);
                ((BookingDetailFragment) getParentFragment()).onRescheduleClicked();
            }
        }
    };

    private View.OnClickListener editHoursClicked = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final Intent intent = new Intent(getActivity(), BookingEditHoursActivity.class);
            intent.putExtra(BundleKeys.BOOKING, booking);
            getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
        }
    };
}
