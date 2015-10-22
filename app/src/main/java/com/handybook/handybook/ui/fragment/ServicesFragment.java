package com.handybook.handybook.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BookingManager;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.ui.widget.ServiceView;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class ServicesFragment extends BookingFlowFragment
{
    static final String EXTRA_SERVICE = "com.handy.handy.EXTRA_SERVICE";
    static final String EXTRA_NAV_HEIGHT = "com.handy.handy.EXTRA_NAV_HEIGHT";

    private Service mService;
    private ArrayList<Service> mServices;

    @Inject
    BookingManager bookingManager;
    @Inject
    UserManager userManager;

    @Bind(R.id.list)
    ViewGroup mList;
    @Bind(R.id.header)
    View mHeader;
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.subtitle)
    TextView mSubtitle;

    public static ServicesFragment newInstance(final Service service, final int navHeight)
    {
        final ServicesFragment fragment = new ServicesFragment();
        final Bundle args = new Bundle();
        args.putParcelable(EXTRA_SERVICE, service);
        args.putInt(EXTRA_NAV_HEIGHT, navHeight);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mService = getArguments().getParcelable(EXTRA_SERVICE);
        mServices = new ArrayList<>(mService.getServices());
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_services, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public final void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        try
        {
            String serviceCategoryMachineName = mService.getUniq().toUpperCase();
            ServiceCategoryAttributes attributes = ServiceCategoryAttributes.valueOf(serviceCategoryMachineName);
            mHeader.setBackgroundColor(getResources().getColor(attributes.getColor()));
            mIcon.setImageResource(attributes.getIcon());
            mTitle.setText(attributes.getTitle());
            mSubtitle.setText(attributes.getSlogan());
        } catch (IllegalArgumentException e)
        {
            Crashlytics.logException(new RuntimeException("Cannot display service: " + mService.getUniq()));
        }
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        for (final Service service : mServices)
        {
            ServiceView serviceView = new ServiceView(getActivity());
            if (serviceView.init(service))
            {
                serviceView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        startBookingFlow(service.getId(), service.getUniq());
                    }
                });
                mList.addView(serviceView);
            }
        }

        // remove bottom border of last element
        mList.getChildAt(mList.getChildCount() - 1).findViewById(R.id.container).setBackgroundResource(0);
    }

    private enum ServiceCategoryAttributes
    {
        HANDYMAN(R.string.handyman, R.string.handyman_slogan_long, R.drawable.ic_handyman_fill, R.color.handy_service_handyman),
        PLUMBING(R.string.plumber, R.string.plumber_slogan_long, R.drawable.ic_plumber_fill, R.color.handy_service_plumber),
        ELECTRICIAN(R.string.electrician, R.string.electrician_slogan_long, R.drawable.ic_electrician_fill, R.color.handy_service_electrician),;

        private final int mTitle;
        private final int mSlogan;
        private final int mIcon;
        private final int mColor;

        ServiceCategoryAttributes(int title, int slogan, int icon, int color)
        {
            mTitle = title;
            mSlogan = slogan;
            mIcon = icon;
            mColor = color;
        }

        public int getTitle()
        {
            return mTitle;
        }

        public int getIcon()
        {
            return mIcon;
        }

        public int getSlogan()
        {
            return mSlogan;
        }

        public int getColor()
        {
            return mColor;
        }

    }
}
