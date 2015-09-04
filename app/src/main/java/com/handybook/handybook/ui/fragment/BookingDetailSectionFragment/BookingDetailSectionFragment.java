package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.BookingDetailSectionView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class BookingDetailSectionFragment extends InjectedFragment
{
    protected Booking booking;

    @Optional
    @InjectView(R.id.booking_detail_section_view)
    protected BookingDetailSectionView view;

    protected int getFragmentResourceId(){ return R.layout.fragment_booking_detail_section; }

    public static BookingDetailSectionFragment newInstance(final Booking booking)
    {
        final BookingDetailSectionFragment fragment = new BookingDetailSectionFragment();
        final Bundle args = new Bundle();
        args.putParcelable(BundleKeys.BOOKING, booking);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.booking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(getFragmentResourceId(), container, false);

        ButterKnife.inject(this, view);

        updateDisplay(this.booking, userManager.getCurrentUser());

        setupClickListeners(this.booking);

        return view;
    }

    //anything universal?
    protected void updateDisplay(Booking booking, User user)
    {
    }


    protected void setupClickListeners(Booking booking)
    {
        //TODO: Probably some additional constraints on this for certain edit actions
        if (!booking.isPast())
        {
          view.entryActionText.setOnClickListener(actionClicked);
        }
    }

    protected void onActionClick()
    {
        System.out.println("ZZZZ test do thing is a go!");
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        view.entryActionText.setClickable(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        view.entryActionText.setClickable(true);
    }

//    @Override
//    public final void onActivityResult(final int requestCode,
//                                       final int resultCode,
//                                       final Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//
////        if (resultCode == BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE)
////        {
////           Date newDate = new Date(data.getLongExtra(BundleKeys.RESCHEDULE_NEW_DATE, 0));
////           bookingDetailView.updateDateTimeInfoText(booking, newDate);
////           setUpdatedBookingResult();
////        }
////        else if (resultCode == BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED)
////        {
////            setCanceledBookingResult();
////            getActivity().finish();
////        }
//    }

//    @Override
//    public final void onSaveInstanceState(final Bundle outState)
//    {
//        super.onSaveInstanceState(outState);
//        //outState.putBoolean(STATE_UPDATED_BOOKING, updatedBooking);
//    }

//    @Subscribe
//    public void onReceivePreRescheduleInfoSuccess(HandyEvent.ReceivePreRescheduleInfoSuccess event)
//    {
//        enableInputs();
//        progressDialog.dismiss();
//
//        final Intent intent = new Intent(getActivity(), BookingDateActivity.class);
//        intent.putExtra(BundleKeys.RESCHEDULE_BOOKING, this.booking);
//        intent.putExtra(BundleKeys.RESCHEDULE_NOTICE, event.notice);
//        startActivityForResult(intent, BookingDateActivity.RESULT_RESCHEDULE_NEW_DATE);
//    }
//
//    @Subscribe
//    public void onReceivePreRescheduleInfoError(HandyEvent.ReceivePreRescheduleInfoError event)
//    {
//        enableInputs();
//        progressDialog.dismiss();
//        dataManagerErrorHandler.handleError(getActivity(), event.error);
//    }
//
//    private View.OnClickListener rescheduleClicked = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(final View v)
//        {
//            disableInputs();
//            progressDialog.show();
//            bus.post(new HandyEvent.RequestPreRescheduleInfo(booking.getId()));
//        }
//    };
//
//    @Subscribe
//    public void onReceivePreCancelationInfoSuccess(HandyEvent.ReceivePreCancelationInfoSuccess event)
//    {
//        Pair<String, List<String>> result = event.result;
//
//        enableInputs();
//        progressDialog.dismiss();
//
//        final Intent intent = new Intent(getActivity(), BookingCancelOptionsActivity.class);
//        intent.putExtra(BundleKeys.OPTIONS, new ArrayList<>(result.second));
//        intent.putExtra(BundleKeys.NOTICE, result.first);
//        intent.putExtra(BundleKeys.BOOKING, booking);
//        startActivityForResult(intent, BookingCancelOptionsActivity.RESULT_BOOKING_CANCELED);
//    }

//    @Subscribe
//    public void onReceivePreCancelationInfoError(HandyEvent.ReceivePreCancelationInfoError event)
//    {
//        enableInputs();
//        progressDialog.dismiss();
//        dataManagerErrorHandler.handleError(getActivity(), event.error);
//    }


//    private View.OnClickListener cancelClicked = new View.OnClickListener()
//    {
//        @Override
//        public void onClick(final View v)
//        {
//            disableInputs();
//            progressDialog.show();
//            bus.post(new HandyEvent.RequestPreCancelationInfo(booking.getId()));
//        }
//    };

    protected View.OnClickListener actionClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
//            disableInputs();
//            progressDialog.show();
//            bus.post(new HandyEvent.RequestPreCancelationInfo(booking.getId()));

            System.out.println("ZZZZ Clicked on action, do the thing");
            onActionClick();
        }
    };



//    private final void setUpdatedBookingResult()
//    {
//        updatedBooking = true;
//
//        final Intent intent = new Intent();
//        intent.putExtra(BundleKeys.BOOKING, booking);
//        getActivity().setResult(BookingDetailActivity.RESULT_BOOKING_UPDATED, intent);
//    }
//
//    private final void setCanceledBookingResult()
//    {
//        final Intent intent = new Intent();
//        intent.putExtra(BundleKeys.CANCELLED_BOOKING, booking);
//        getActivity().setResult(BookingDetailActivity.RESULT_BOOKING_CANCELED, intent);
//    }

}
