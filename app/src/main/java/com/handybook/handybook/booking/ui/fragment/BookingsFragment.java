package com.handybook.handybook.booking.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handybook.handybook.R;
import com.handybook.handybook.analytics.MixpanelEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.ui.activity.ServiceCategoriesActivity;
import com.handybook.handybook.booking.ui.activity.ServicesActivity;
import com.handybook.handybook.booking.ui.view.ServiceOptionView;
import com.handybook.handybook.booking.ui.view.ServiceOptionsView;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.ui.fragment.InjectedFragment;
import com.handybook.handybook.ui.view.HandyTabLayout;
import com.handybook.handybook.ui.widget.MenuButton;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link InjectedFragment} subclass.
 * Use the {@link BookingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookingsFragment extends BookingFlowFragment
        implements ServiceOptionsView.OnClickListeners
{

    private static final String SHARED_ICON_ELEMENT_NAME = "icon";
    @Bind(R.id.menu_button_layout)
    ViewGroup mMenuButtonLayout;
    @Bind(R.id.pager)
    ViewPager mViewPager;
    @Bind(R.id.tab_layout)
    HandyTabLayout mTabLayout;
    @Bind(R.id.service_options)
    ServiceOptionsView mServiceOptionsView;
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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTabAdapter = new TabAdapter(getActivity(), getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_bookings, container, false);
        ButterKnife.bind(this, view);
        mViewPager.setAdapter(mTabAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(mTabAdapter);
        final MenuButton menuButton = new MenuButton(getActivity(), mMenuButtonLayout);
        menuButton.setColor(getResources().getColor(R.color.white));
        mMenuButtonLayout.addView(menuButton);
        loadServices();

        return view;
    }

    private void loadServices()
    {
        dataManager.getServices(new DataManager.CacheResponse<List<Service>>()
        {
            @Override
            public void onResponse(final List<Service> services)
            {
            }
        }, new DataManager.Callback<List<Service>>()
        {
            @Override
            public void onSuccess(final List<Service> services)
            {
                mServiceOptionsView.init(services, BookingsFragment.this);
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
            }
        });
    }

    @Subscribe
    public void onServicesButtonClicked(HandyEvent.ServicesButtonClicked event)
    {
        if (mServiceOptionsView.isReady())
        {
            mServiceOptionsView.show();
            bus.post(new MixpanelEvent.TrackAddBookingFabMenuShown());
        }
        else
        {
            startActivity(new Intent(getActivity(), ServiceCategoriesActivity.class));
        }
    }

    @Override
    public void onHideServiceOptions()
    {
        bus.post(new HandyEvent.CloseServicesButtonClicked());
    }

    @Override
    public void onServiceOptionClicked(final ServiceOptionView view, final Service service)
    {
        bus.post(new MixpanelEvent.TrackAddBookingFabServiceSelected(service.getId(), service.getUniq()));
        if (service.getServices().size() > 0)
        {
            final Intent intent = new Intent(getActivity(), ServicesActivity.class);
            intent.putExtra(ServicesActivity.EXTRA_SERVICE, service);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(), view.getIcon(), SHARED_ICON_ELEMENT_NAME
                );
                getActivity().startActivity(intent, options.toBundle());
            }
            else
            {
                startActivity(intent);
            }
        }
        else
        {
            startBookingFlow(service.getId(), service.getUniq());
        }
    }

    private static class TabAdapter extends FragmentPagerAdapter
    {
        private ArrayList<BookingListFragment> fragments = new ArrayList<>();
        private ArrayList<String> titles = new ArrayList<>();

        public TabAdapter(Context context, FragmentManager fm)
        {
            super(fm);

            titles.add(context.getResources().getString(R.string.upcoming));
            fragments.add(
                    BookingListFragment.newInstance(BookingCardViewModel.List.TYPE_UPCOMING)
            );
            titles.add(context.getResources().getString(R.string.past));
            fragments.add(
                    BookingListFragment.newInstance(BookingCardViewModel.List.TYPE_PAST)
            );
        }

        @Override
        public int getCount()
        {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return titles.get(position);
        }

        @Override
        public BookingListFragment getItem(int position)
        {
            return fragments.get(position);
        }
    }
}
