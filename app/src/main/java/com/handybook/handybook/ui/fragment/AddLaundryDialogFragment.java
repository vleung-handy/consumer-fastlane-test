package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.handybook.handybook.R;
import com.handybook.handybook.data.DataManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class AddLaundryDialogFragment extends BaseDialogFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private int booking;

    @Inject DataManager dataManager;

    @InjectView(R.id.submit_button) Button submitButton;
    @InjectView(R.id.close_img) ImageView closeImage;

    public static AddLaundryDialogFragment newInstance(final int bookingId) {
        final AddLaundryDialogFragment addLaundryDialogFragment
                = new AddLaundryDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_BOOKING, bookingId);

        addLaundryDialogFragment.setArguments(bundle);
        return addLaundryDialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        booking = args.getInt(EXTRA_BOOKING);

        this.canDismiss = true;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_laundry_add, container, true);
        ButterKnife.inject(this, view);

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });

        return view;
    }
}
