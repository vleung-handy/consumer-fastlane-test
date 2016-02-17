package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditPreferencesActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.ChecklistItem;
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

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.preferences;
    }

    @Override
    protected int getEntryActionTextResourceId(Booking booking)
    {
        return R.string.edit;
    }

    @Override
    protected int getFragmentResourceId()
    {
        return R.layout.fragment_booking_detail_section_preferences;
    }

    @Override
    protected boolean hasEnabledAction(Booking booking)
    {
        return true;
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

        if (hasInstructions())
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
     * Loops through the list of instructions (checklist items) and add the view to the section.
     * Assumes there are instructions to be displayed.
     */
    private void populatePreferencesInSection()
    {
        final List<ChecklistItem> checklistItems = booking.getInstructions().getChecklist();
        preferencesSection.removeAllViews();
        for (int i = 0; i < checklistItems.size(); i++)
        {
            final ChecklistItem preference = checklistItems.get(i);
            BookingDetailSectionImageItemView itemView = (BookingDetailSectionImageItemView)
                    getActivity().getLayoutInflater().inflate(R.layout.layout_section_image_item, null);

            itemView.updateDisplay(preference.getImageResource(), View.VISIBLE, preference.getTitle(),
                    BookingDetailSectionImageItemView.TextStyle.BOLD, preference.getDescription());

            preferencesSection.addView(itemView);
        }
    }

    /**
     * returns true if there is at least one instruction marked as "requested"
     *
     * @return
     */
    private boolean hasInstructions()
    {
        if (booking != null && booking.getInstructions() != null
                && booking.getInstructions().getChecklist() != null
                && !booking.getInstructions().getChecklist().isEmpty())
        {

            for (ChecklistItem item : booking.getInstructions().getChecklist())
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

        if (hasInstructions())
        {
            getActivity().getLayoutInflater().inflate(R.layout.layout_section_separator_view_short, preferencesSection);
        }

        BookingDetailSectionImageItemView itemView = (BookingDetailSectionImageItemView)
                getActivity().getLayoutInflater().inflate(R.layout.layout_section_image_item, null);
        itemView.updateDisplay(R.drawable.ic_instruction_note, View.INVISIBLE, getString(R.string.note_to_pro),
                BookingDetailSectionImageItemView.TextStyle.ITALICS, note);

        preferencesSection.addView(itemView);
    }

    @Override
    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditPreferencesActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
