package com.handybook.handybook.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.event.HandyEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Tells user that his app is too old to be used displaying a button leading to play store.
 */
public class BlockingUpdateFragment extends InjectedFragment
{
    public static final String URL_FORMAT_MARKET = "market://details?id=%1s";
    public static final String URL_FORMAT_PLAY_WEB = "https://play.google.com/store/apps/details?id=%1s";

    public BlockingUpdateFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View root = inflater.inflate(R.layout.fragment_blocking_update, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        bus.post(new HandyEvent.BlockingScreenDisplayed());
    }


    @OnClick(R.id.b_modal_blocking_button)
    public void launchPlayStore(final Button submitButton)
    {
        final String appPackageName = getActivity().getPackageName();
        try
        {
            startActivity(
                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(String.format(URL_FORMAT_MARKET, appPackageName))
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
            //This will fail if user doesn't have play store app, hence the link to web below
        } catch (android.content.ActivityNotFoundException anfe)
        {
            startActivity(

                    new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(String.format(URL_FORMAT_PLAY_WEB, appPackageName))
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
        } finally
        {
            bus.post(new HandyEvent.BlockingScreenButtonPressed());
        }
    }

}
