package com.handybook.handybook.core.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.handybook.handybook.R;
import com.handybook.handybook.library.ui.fragment.InjectedFragment;
import com.handybook.handybook.library.util.ExternalApplicationLauncher;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Tells user that his app is too old to be used displaying a button leading to play store.
 */
public class BlockingUpdateFragment extends InjectedFragment {

    public BlockingUpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        final View root = inflater.inflate(R.layout.fragment_blocking_update, container, false);
        ButterKnife.bind(this, root);
        return root;
    }

    @OnClick(R.id.b_modal_blocking_button)
    public void launchPlayStore(final Button submitButton) {
        ExternalApplicationLauncher.launchPlayStoreAppListing(getContext());
    }

}
