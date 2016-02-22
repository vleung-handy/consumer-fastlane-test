package com.handybook.handybook.helpcenter.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.handybook.handybook.constant.BundleKeys;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;

public class HelpActivity extends MenuDrawerActivity
{
    public enum DeepLink
    {
        CANCEL(296, 215562927), PRO_LATE(450, 214917058), ADJUST_HOURS(498, 215563597);

        private final int mNodeId;
        private final int mArticleId;

        DeepLink(final int nodeId, final int articleId)
        {
            mNodeId = nodeId;
            mArticleId = articleId;
        }

        public Intent getIntent(final Context context)
        {
            final Intent intent = new Intent(context, HelpActivity.class);
            intent.putExtra(BundleKeys.HELP_ARTICLE_ID, Integer.toString(mArticleId));
            intent.putExtra(BundleKeys.HELP_NODE_ID, Integer.toString(mNodeId));
            return intent;
        }
    }

    @Override
    protected Fragment createFragment()
    {
        return HelpFragment.newInstance(getIntent().getExtras());
    }

    @Override
    protected String getNavItemTitle()
    {
        return null;
    }
}
