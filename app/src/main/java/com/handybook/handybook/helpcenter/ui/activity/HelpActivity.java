package com.handybook.handybook.helpcenter.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.google.common.base.Strings;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.core.constant.BundleKeys;
import com.handybook.handybook.core.ui.activity.SingleFragmentActivity;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;

public class HelpActivity extends SingleFragmentActivity {

    private static final String LINK_TYPE_ARTICLES = "/articles/";
    private static final String LINK_TYPE_SECTIONS = "/sections/";


    public enum DeepLink {
        CANCEL(296, 217659877),
        PRO_LATE(450, 214917058),
        ADJUST_HOURS(498, 215563597),
        PRO_ISSUES(0, 203531628, LINK_TYPE_SECTIONS),
        PRO_TEAM(0, 220258807);

        private final int mNodeId;
        private final int mId;
        private String mLinkType;

        DeepLink(final int nodeId, final int articleId) {
            mNodeId = nodeId;
            mId = articleId;
            mLinkType = LINK_TYPE_ARTICLES;     //this is the default, since most things use that.
        }

        DeepLink(final int nodeId, final int articleId, final String linkType) {
            mNodeId = nodeId;
            mId = articleId;
            mLinkType = linkType;
        }

        public Intent getIntent(final Context context) {
            final Intent intent = new Intent(context, HelpActivity.class);
            intent.putExtra(BundleKeys.HELP_ID, Integer.toString(mId));
            intent.putExtra(BundleKeys.HELP_NODE_ID, Integer.toString(mNodeId));
            intent.putExtra(BundleKeys.HELP_LINK_TYPE, mLinkType);
            return intent;
        }
    }

    @Override
    protected Fragment createFragment() {
        Bundle args = getIntent().getExtras();
        if (args != null && !args.isEmpty()) {
            if (!Strings.isNullOrEmpty(args.getString(BundleKeys.HELP_ID)) &&
                !Strings.isNullOrEmpty(args.getString(BundleKeys.HELP_LINK_TYPE))) {
                return HelpWebViewFragment.newInstance(getIntent().getExtras());
            }
            else { return launchHelpFragmentWithOptionalArgsIfEnabled(); }
        }
        else {
            return launchHelpFragmentWithOptionalArgsIfEnabled();
        }
    }

    private Fragment launchHelpFragmentWithOptionalArgsIfEnabled() {
        Configuration config = mConfigurationManager.getPersistentConfiguration();

        String helpCenterUrl = config.getHelpCenterUrl();
        if (config.isNativeHelpCenterEnabled()) {
            return HelpFragment.newInstance(helpCenterUrl);
        }
        else {
            Bundle args = new Bundle();
            if (!Strings.isNullOrEmpty(helpCenterUrl)) {
                args.putString(BundleKeys.HELP_CENTER_URL, helpCenterUrl);
            }
            return HelpWebViewFragment.newInstance(args);
        }
    }
}
