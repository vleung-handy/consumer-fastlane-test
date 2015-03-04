package com.handybook.handybook.ui.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.handybook.handybook.R;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.util.TextUtils;

import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LaundryInfoDialogFragment extends BaseDialogFragment {
    static final String EXTRA_BOOKING = "com.handy.handy.EXTRA_BOOKING";

    private Booking booking;

    @Inject DataManager dataManager;

    @InjectView(R.id.submit_button) Button submitButton;
    @InjectView(R.id.price_link) TextView priceLink;
    @InjectView(R.id.close_img) ImageView closeImage;

    public static LaundryInfoDialogFragment newInstance(final Booking booking) {
        final LaundryInfoDialogFragment laundryDropOffDialogFragment
                = new LaundryInfoDialogFragment();

        final Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_BOOKING, booking);

        laundryDropOffDialogFragment.setArguments(bundle);
        return laundryDropOffDialogFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();
        booking = args.getParcelable(EXTRA_BOOKING);

        this.canDismiss = true;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        final View view = inflater.inflate(R.layout.dialog_laundry_info, container, true);
        ButterKnife.inject(this, view);

        Linkify.addLinks(priceLink, Pattern.compile(getResources().getString(R.string.see_pricing))
                , "http://help.handy.com/customer/portal/articles/1809526-dry-cleaning-and-laundry-on-demand?text=");

        TextUtils.stripUnderlines(priceLink);

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

                AddLaundryDialogFragment.newInstance(booking)
                        .show(getActivity().getSupportFragmentManager(), "AddLaundryDialogFragment");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("APP_LAUNDRY_INFO_SHOWN", true);
        edit.apply();
    }
}
