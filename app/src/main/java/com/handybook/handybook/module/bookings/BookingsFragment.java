package com.handybook.handybook.module.bookings;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.view.ServiceCategoriesOverlayFragment;
import com.handybook.handybook.constant.ActivityResult;
import com.handybook.handybook.logger.mixpanel.MixpanelEvent;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.util.UiUtils;
import com.squareup.otto.Subscribe;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 */
public class BookingsFragment extends InjectedFragment
{
    public static final String mOverlayFragmentTag = ServiceCategoriesOverlayFragment.class.getSimpleName();

    @Bind(R.id.add_booking_button)
    FloatingActionButton mAddBookingButton;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    private List<Service> mServices;
    private TabAdapter mTabAdapter;

    public BookingsFragment()
    {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookingsFragmentTabbed.
     */
    public static BookingsFragment newInstance()
    {
        return new BookingsFragment();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ActivityResult.BOOKING_UPDATED
                || resultCode == ActivityResult.BOOKING_CANCELED)
        {
            if (mTabAdapter != null)
            {
                mTabAdapter.reloadFragments();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(this, view);

        setupToolbar(mToolbar, getString(R.string.my_bookings));

        if (getActivity() instanceof MenuDrawerActivity)
        {
            ((MenuDrawerActivity) getActivity()).setupHamburgerMenu(mToolbar);
        }

        mTabAdapter = new TabAdapter(getActivity(), getChildFragmentManager());
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);

        getActivity().getSupportFragmentManager()
                .addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener()
                {
                    @Override
                    public void onBackStackChanged()
                    {
                        int backStackEntryCount = getActivity().getSupportFragmentManager()
                                .getBackStackEntryCount();
                        if (backStackEntryCount == 0)
                        {
                            mAddBookingButton.show();
                        }
                        else
                        {
                            mAddBookingButton.hide();
                        }
                    }
                });

        return view;
    }

    @Subscribe
    public void onReceiveServicesSuccess(final BookingEvent.ReceiveServicesSuccess event)
    {
        mServices = event.getServices();
        if (mServices != null)
        {
            if (ViewCompat.isAttachedToWindow(mAddBookingButton))
            {
                UiUtils.revealView(mAddBookingButton);
            }
            else
            {
                mAddBookingButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.add_booking_button)
    public void onServicesButtonClicked()
    {
        bus.post(new MixpanelEvent.TrackAddBookingFabClicked());

        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(mOverlayFragmentTag) == null)
        {
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, 0, 0, R.anim.fade_out)
                    .add(R.id.fragment_container,
                            ServiceCategoriesOverlayFragment.newInstance(mServices),
                            mOverlayFragmentTag)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        //do not post this before onResume, because this fragment registers the bus onResume. If
        //post prior to onResume, this fragment won't be listening to the bus for results.
        bus.post(new BookingEvent.RequestServices());
    }
}
