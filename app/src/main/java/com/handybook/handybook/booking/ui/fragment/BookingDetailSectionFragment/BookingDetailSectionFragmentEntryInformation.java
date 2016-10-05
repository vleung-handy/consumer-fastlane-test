package com.handybook.handybook.booking.ui.fragment.BookingDetailSectionFragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.bookingedit.ui.activity.BookingEditEntryInformationActivity;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.EntryMethodOption;
import com.handybook.handybook.booking.model.InputFormDefinition;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.core.User;

public class BookingDetailSectionFragmentEntryInformation extends BookingDetailSectionFragment
{

    public static final String TAG= "BookingDetailSectionFragmentEntryInformation";

    @Override
    protected int getEntryTitleTextResourceId(Booking booking)
    {
        return R.string.entry_info;
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

    @Override
    public void updateDisplay(Booking booking, User user)
    {
        super.updateDisplay(booking, user);
        final EntryMethodOption entryMethodOption = booking.getEntryMethodOption();
        if (entryMethodOption != null)
        {
            String bodyText = "";
            boolean shouldTitleBeBold = false;
            if (entryMethodOption.getInputFormDefinition() != null
                    && entryMethodOption.getInputFormDefinition().getFieldDefinitions() != null)
            {
                for (InputFormDefinition.InputFormField inputFormField :
                        entryMethodOption.getInputFormDefinition().getFieldDefinitions())
                {
                    if (!TextUtils.isEmpty(inputFormField.getValue()))
                    {
                        bodyText = bodyText + "<br>"
                                + inputFormField.getTitle() + ": "
                                + inputFormField.getValue();
                        shouldTitleBeBold = true;
                        //title should be bold if any additional instructions will be displayed
                    }
                }
            }
            String titleText;
            if (shouldTitleBeBold)
            {
                titleText = "<b>" + entryMethodOption.getTitleText() + "</b>";
            }
            else
            {
                titleText = entryMethodOption.getTitleText();
            }
            getSectionView().getEntryText().setText(Html.fromHtml(titleText + bodyText));
        }
        else
        {
            getSectionView().getEntryText().setText(R.string.no_information);
        }
    }

    protected void onActionClick()
    {
        final Intent intent = new Intent(getActivity(), BookingEditEntryInformationActivity.class);
        intent.putExtra(BundleKeys.BOOKING, this.booking);
        getParentFragment().startActivityForResult(intent, ActivityResult.BOOKING_UPDATED);
    }
}
