package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditPreferencesActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingInstruction;
import com.handybook.handybook.booking.ui.view.BookingDetailSectionImageItemView;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;

import java.util.List;

import butterknife.Bind;

public class BookingDetailSectionFragmentPreferences extends BookingDetailSectionFragment
{
    @Bind(R.id.preferences_section)
    public LinearLayout preferencesSection;

    @StringRes
    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {

        if (hasInstructions())
        {
            return R.string.cleaning_routine;
        }

        return R.string.job_details;
    }

    @LayoutRes
    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_preferences;
    }

    @Override
    protected void updateActionTextView(
            @NonNull final Booking booking, @NonNull final TextView actionTextView
    )
    {
        if (booking.isPast())
        {
            actionTextView.setVisibility(View.GONE);
            return;
        }
        actionTextView.setVisibility(View.VISIBLE);
        actionTextView.setText(R.string.edit);
        actionTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                onActionClick();
            }
        });
    }

    /**
     * This populates the instructions on the top part (if any), and the notes on the bottom (if any)
     * or hides everything if there is nothing to be displayed.
     *
     * @param booking
     * @param user
     */
    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);

        boolean showSection = false;

        if (hasRequestedInstructions())
        {
            showSection = true;
            populatePreferencesInSection();
        }

        if (!TextUtils.isEmpty(booking.getProNote()))
        {
            showSection = true;
            addNoteToSection(booking.getProNote());
        }

        if (showSection)
        {
            getSectionView().getEntryText().setVisibility(View.VISIBLE);
        }
        else
        {
            getSectionView().getEntryText().setVisibility(View.GONE);
        }
    }

    /**
     * Loops through the list of instructions (checklist items) and add the view to the section only
     * if that preference is "requested"
     * Assumes there are instructions to be displayed.
     */
    private void populatePreferencesInSection()
    {
        final List<BookingInstruction> bookingInstructions =
                booking.getInstructions().getBookingInstructions();
        preferencesSection.removeAllViews();
        for (int i = 0; i < bookingInstructions.size(); i++)
        {
            final BookingInstruction preference = bookingInstructions.get(i);

            if (preference.getIsRequested())
            {
                BookingDetailSectionImageItemView itemView =
                        (BookingDetailSectionImageItemView) getActivity().getLayoutInflater()
                                .inflate(R.layout.layout_section_image_item, null);

                itemView.updateDisplay(
                        preference.getImageResource(),
                        View.VISIBLE,
                        preference.getTitle(),
                        BookingDetailSectionImageItemView.TextStyle.BOLD,
                        preference.getDescription()
                );

                preferencesSection.addView(itemView);
            }
        }
    }

    private boolean hasInstructions()
    {
        return booking != null && booking.getInstructions() != null
                && booking.getInstructions().getBookingInstructions() != null
                && !booking.getInstructions().getBookingInstructions().isEmpty();
    }

    /**
     * returns true if there is at least one instruction marked as "requested"
     *
     * @return
     */
    private boolean hasRequestedInstructions()
    {
        if (hasInstructions())
        {

            for (BookingInstruction item : booking.getInstructions().getBookingInstructions())
            {
                if (item.getIsRequested())
                {
                    return true;
                }
            }

            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * Adds the notes to pro to the bottom of the section, with a divider if there are preferences set.
     *
     * @param note
     */
    private void addNoteToSection(String note)
    {

        if (hasRequestedInstructions())
        {
            getActivity().getLayoutInflater().inflate(R.layout.layout_section_separator_view_short, preferencesSection);
        }

        BookingDetailSectionImageItemView itemView = (BookingDetailSectionImageItemView)
                getActivity().getLayoutInflater().inflate(R.layout.layout_section_image_item, null);
        itemView.updateDisplay(R.drawable.ic_instruction_note, View.INVISIBLE, getString(R.string.note_to_pro),
                BookingDetailSectionImageItemView.TextStyle.ITALICS, note);

        preferencesSection.addView(itemView);
    }

    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditPreferencesActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
