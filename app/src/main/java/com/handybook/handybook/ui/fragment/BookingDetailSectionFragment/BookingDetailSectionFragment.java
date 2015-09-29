package com.handybook.handybook.ui.fragment.BookingDetailSectionFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.constant.BookingActionButtonType;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.User;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.widget.BookingActionButton;
import com.handybook.handybook.ui.widget.BookingDetailSectionView;
import com.handybook.handybook.util.Utils;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public abstract class BookingDetailSectionFragment extends InjectedFragment
{
    protected Booking booking;

    @Bind(R.id.booking_detail_section_view)
    protected BookingDetailSectionView view;

    protected View.OnClickListener actionClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(final View v)
        {
            onActionClick();
        }
    };

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.booking = getArguments().getParcelable(BundleKeys.BOOKING);
    }

    @Override
    protected void disableInputs()
    {
        super.disableInputs();
        view.entryActionText.setClickable(false);
        setActionButtonsEnabled(false);
    }

    @Override
    protected final void enableInputs()
    {
        super.enableInputs();
        view.entryActionText.setClickable(true);
        setActionButtonsEnabled(true);
    }

    @Override
    public final View onCreateView(
            final LayoutInflater inflater,
            final ViewGroup container,
            final Bundle savedInstanceState
    )
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(getFragmentResourceId(), container, false);
        ButterKnife.bind(this, view);
        updateDisplay(this.booking, userManager.getCurrentUser());
        setupClickListeners(this.booking);
        return view;
    }

    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section;
    }

    protected void updateDisplay(Booking booking, User user)
    {
        view.entryTitle.setText(getEntryTitleTextResourceId(booking));
        view.entryActionText.setText(getEntryActionTextResourceId(booking));
        if (!hasEnabledAction())
        {
            view.entryActionText.setVisibility(View.GONE);
        }
        setupBookingActionButtons(booking);
    }

    protected void setupClickListeners(Booking booking)
    {
        //TODO: Probably some additional constraints on this for certain edit actions?
        if (!booking.isPast())
        {
            view.entryActionText.setOnClickListener(actionClicked);
        }
    }

    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.blank_string;
    }

    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.blank_string;
    }

    protected boolean hasEnabledAction()
    {
        return false;
    }

    //TODO: Might put all this booking action button stuff into a child class?, it's a big chunk of
    // functionality
    //Booking action buttons
    //This code is a copy paste from booking detail fragment, migrate the code away from booking
    // detail fragment into a sub fragment
    protected void setupBookingActionButtons(Booking booking)
    {
        clearBookingActionButtons();
        List<String> actionButtonTypes = getActionButtonTypeList(booking);
        ViewGroup actionButtonLayout = getBookingActionButtonLayout();
        if (actionButtonTypes.isEmpty())
        {
            actionButtonLayout.setVisibility(View.GONE);
        } else
        {
            actionButtonLayout.setVisibility(View.VISIBLE);
            for (String actionButtonType : actionButtonTypes)
            {
                BookingActionButtonType bookingABT = Utils.getBookingActionButtonType(actionButtonType);
                if (bookingABT == null)
                {
                    continue;
                }
                ViewGroup buttonParentLayout = getParentForActionButtonType(actionButtonType);
                if (buttonParentLayout == null)
                {
                    continue;
                }
                int newChildIndex = buttonParentLayout.getChildCount();//old count +1
                ViewGroup rootViewGroup = (ViewGroup) getActivity().getLayoutInflater()
                        .inflate(bookingABT.getLayoutTemplateId(), buttonParentLayout);
                BookingActionButton bookingActionButton = (BookingActionButton) rootViewGroup
                        .getChildAt(newChildIndex);
                View.OnClickListener onClickListener = getOnClickListenerForAction(actionButtonType);
                bookingActionButton.init(actionButtonType, onClickListener);
            }
        }
    }

    protected void clearBookingActionButtons()
    {
        view.actionButtonsLayout.removeAllViews();
    }

    //Nothing by default
    protected List<String> getActionButtonTypeList(Booking booking)
    {
        return new ArrayList<>();
    }

    protected ViewGroup getBookingActionButtonLayout()
    {
        return view.actionButtonsLayout;
    }

    protected ViewGroup getParentForActionButtonType(String actionButtonType)
    {
        //default is directly into parent, some sub classes will further sub divide this section
        return view.actionButtonsLayout;
    }

    //nothing by default
    protected View.OnClickListener getOnClickListenerForAction(String actionButtonType)
    {
        return null;
    }

    @Subscribe
    public void onSetBookingActionControlsEnabled(
            HandyEvent.SetBookingDetailSectionFragmentActionControlsEnabled event
    )
    {
        if (event.enabled)
        {
            enableInputs();
        } else
        {
            disableInputs();
        }
    }

    protected void onActionClick()
    {
    }

    protected void setActionButtonsEnabled(boolean enabled)
    {
        List<String> actionButtonTypes = getActionButtonTypeList(this.booking);
        if (actionButtonTypes.isEmpty())
        {
            return;
        }
        for (String actionButtonType : actionButtonTypes)
        {
            BookingActionButtonType bookingActionButtonType = Utils.getBookingActionButtonType(actionButtonType);
            if (bookingActionButtonType == null)
            {
                continue;
            }
            ViewGroup buttonParentLayout = getParentForActionButtonType(actionButtonType);
            if (buttonParentLayout == null)
            {
                continue;
            }
            for (int i = 0; i < buttonParentLayout.getChildCount(); i++)
            {
                BookingActionButton actionButton = (BookingActionButton) buttonParentLayout.getChildAt(i);
                if (actionButton == null)
                {
                    continue;
                }
                actionButton.setEnabled(enabled);
            }
        }
    }
}
