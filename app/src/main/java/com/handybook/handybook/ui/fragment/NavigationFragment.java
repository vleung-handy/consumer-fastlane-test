package com.handybook.handybook.ui.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.R;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.ui.activity.BookingsActivity;
import com.handybook.handybook.ui.activity.HelpActivity;
import com.handybook.handybook.ui.activity.LoginActivity;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.ui.activity.ProfileActivity;
import com.handybook.handybook.ui.activity.PromosActivity;
import com.handybook.handybook.ui.activity.ServiceCategoriesActivity;
import com.simplealertdialog.SimpleAlertDialog;
import com.simplealertdialog.SimpleAlertDialogSupportFragment;
import com.squareup.otto.Subscribe;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public final class NavigationFragment extends InjectedFragment
        implements SimpleAlertDialog.OnClickListener
{
    static final String ARG_SELECTED_ITEM = "com.handybook.handybook.ARG_SELECTED_ITEM";
    static final int REQUEST_LOGOUT = 1;

    private final ArrayList<String> items = new ArrayList<>();
    private String mSelectedItem;
    private MenuDrawer mMenuDrawer;

    @Bind(R.id.env_button)
    Button envButton;
    @Bind(android.R.id.list)
    ListView listView;

    @Inject
    UserManager mUserManager;
    @Inject
    EnvironmentModifier mEnvironmentModifier;

    public static NavigationFragment newInstance(final String selectedItem)
    {
        final Bundle args = new Bundle();
        args.putString(ARG_SELECTED_ITEM, selectedItem);

        final NavigationFragment fragment = new NavigationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public final void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        final Bundle args;
        if ((args = getArguments()) != null)
        {
            mSelectedItem = args.getString(ARG_SELECTED_ITEM);
        }

        //need to make sure FB is initialized to be able to log the user out of facebook
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
    }

    @Override
    public final View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                                   final Bundle savedInstanceState)
    {
        final View view = getActivity().getLayoutInflater()
                .inflate(R.layout.fragment_navigation, container, false);

        ButterKnife.bind(this, view);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD))
        {
            envButton.setVisibility(View.GONE);
        }

        envButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Context context = NavigationFragment.this.getActivity();
                final EditText input = new EditText(context);
                input.setText(mEnvironmentModifier.getEnvironment());
                new AlertDialog.Builder(context)
                        .setTitle(R.string.set_environment)
                        .setView(input)
                        .setPositiveButton(R.string.set, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                mEnvironmentModifier.setEnvironment(input.getText().toString());
                            }
                        })
                        .setNegativeButton(R.string.cancel, null)
                        .create()
                        .show();
            }
        });

        return view;
    }

    @Override
    public final void onResume()
    {
        super.onResume();
        loadNavItems();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    private Integer getViewIdByItemName(String itemName)
    {
        //HACK: This is an ugly hack to move automation forward, we should not continue the pattern of using string names as identifiers
        //since it is currently keyed by a string we can't do it statically, yet another reason to move over to static ids
        Map<String, Integer> nameToResourceId = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        nameToResourceId.put(getString(R.string.home), R.id.nav_menu_home);
        nameToResourceId.put(getString(R.string.profile), R.id.nav_menu_profile);
        nameToResourceId.put(getString(R.string.my_bookings), R.id.nav_menu_my_bookings);
        nameToResourceId.put(getString(R.string.help), R.id.nav_menu_help);
        nameToResourceId.put(getString(R.string.promotions), R.id.nav_menu_promotions);
        nameToResourceId.put(getString(R.string.log_out), R.id.nav_menu_log_out);
        nameToResourceId.put(getString(R.string.log_in), R.id.nav_menu_log_in);

        if (nameToResourceId.containsKey(itemName))
        {
            return nameToResourceId.get(itemName);
        }

        Crashlytics.log("Could not find mapping to id for navigation menu entry : " + itemName);
        return -1;
    }

    @Override
    public final void onActivityCreated(final Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
        mMenuDrawer = activity.getMenuDrawer();
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_nav, items)
        {
            @Override
            public final View getView(final int position, final View convertView,
                                      final ViewGroup parent)
            {
                View view = convertView;
                if (view == null)
                {
                    final LayoutInflater inflater = (LayoutInflater) getContext()
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.list_item_nav, parent, false);
                }

                final String itemName = items.get(position).toUpperCase();
                final TextView item = (TextView) view.findViewById(R.id.nav_item);
                item.setText(itemName);

                if (itemName.equalsIgnoreCase(mSelectedItem))
                {
                    item.setTextColor(getResources().getColor(R.color.handy_blue));
                }
                else
                {
                    item.setTextColor(getResources().getColor(R.color.white));
                }

                //HACK: We should be using IDs not text names, this is a quick fix so Automation is not blocked
                int viewId = getViewIdByItemName(itemName);
                if (viewId > -1)
                {
                    view.setId(viewId);
                }

                return view;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id)
            {
                final TextView textView = (TextView) view.findViewById(R.id.nav_item);
                final String item = textView.getText().toString();
                final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();

                if (item.equalsIgnoreCase(getString(R.string.home))
                        && !getString(R.string.home).equalsIgnoreCase(mSelectedItem))
                {
                    activity.navigateToActivity(ServiceCategoriesActivity.class);
                }
                else if (item.equalsIgnoreCase(getString(R.string.profile))
                        && !getString(R.string.profile).equalsIgnoreCase(mSelectedItem))
                {
                    activity.navigateToActivity(ProfileActivity.class);
                }
                else if (item.equalsIgnoreCase(getString(R.string.my_bookings))
                        && !getString(R.string.my_bookings).equalsIgnoreCase(mSelectedItem))
                {
                    activity.navigateToActivity(BookingsActivity.class);
                }
                else if (item.equalsIgnoreCase(getString(R.string.help))
                        && !getString(R.string.help).equalsIgnoreCase(mSelectedItem))
                {
                    activity.navigateToActivity(HelpActivity.class);
                }
                else if (item.equalsIgnoreCase(getString(R.string.promotions))
                        && !getString(R.string.promotions).equalsIgnoreCase(mSelectedItem))
                {
                    activity.navigateToActivity(PromosActivity.class);
                }
                else if (item.equalsIgnoreCase(getString(R.string.log_in))
                        && !getString(R.string.log_in).equalsIgnoreCase(mSelectedItem))
                {
                    activity.navigateToActivity(LoginActivity.class);
                }
                else if (item.equalsIgnoreCase(getString(R.string.log_out)))
                {
                    new SimpleAlertDialogSupportFragment.Builder()
                            .setMessage(getString(R.string.want_to_log_out))
                            .setPositiveButton(R.string.log_out)
                            .setNegativeButton(android.R.string.cancel)
                            .setRequestCode(REQUEST_LOGOUT)
                            .setTargetFragment(NavigationFragment.this)
                            .create().show(getActivity().getSupportFragmentManager(), "dialog");
                }
                else
                {
                    mMenuDrawer.closeMenu();
                }
            }
        });
    }

    @Override
    public final void onDialogPositiveButtonClicked(final SimpleAlertDialog dialog,
                                                    final int requestCode, final View view)
    {
        if (requestCode == REQUEST_LOGOUT)
        {
            //TODO: we should invalidate the auth token also!
            mUserManager.setCurrentUser(null);

            //log out of Facebook also
            LoginManager.getInstance().logOut();
        }
    }

    @Override
    public final void onDialogNegativeButtonClicked(final SimpleAlertDialog dialog,
                                                    final int requestCode, final View view)
    {
    }

    @Subscribe
    public final void userAuthUpdated(final UserLoggedInEvent event)
    {
        loadNavItems();

        if (!event.isLoggedIn())
        {
            final MenuDrawerActivity activity = (MenuDrawerActivity) getActivity();
            activity.navigateToActivity(ServiceCategoriesActivity.class);
        }
    }

    @Subscribe
    public final void envUpdated(final EnvironmentUpdatedEvent event)
    {
        loadNavItems();
    }

    private void loadNavItems()
    {
        final boolean userLoggedIn = mUserManager.getCurrentUser() != null;

        items.clear();
        items.add(getString(R.string.home));

        if (userLoggedIn)
        {
            items.add(getString(R.string.profile));
            items.add(getString(R.string.my_bookings));
        }

        items.add(getString(R.string.help));

        items.add(getString(R.string.promotions));

        if (mUserManager.getCurrentUser() != null)
        {
            items.add(getString(R.string.log_out));
        }
        else
        {
            items.add(getString(R.string.log_in));
        }

        envButton.setText(String.format(getString(R.string.env_format), mEnvironmentModifier.getEnvironment(),
                BuildConfig.VERSION_NAME, Integer.valueOf(BuildConfig.VERSION_CODE).toString()));

        final BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
        if (adapter != null)
        {
            adapter.notifyDataSetChanged();
        }
    }
}
