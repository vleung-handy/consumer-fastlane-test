package com.handybook.handybook.ui.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.handybook.handybook.R;
import com.handybook.handybook.ui.activity.MenuDrawerActivity;
import com.handybook.handybook.util.Utils;

public final class CTAButton extends Button
{
   private CTAButtonNavigationData navigationData;

    public CTAButton(Context context) {
        super(context);
        init(context, null);
    }

    public CTAButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public CTAButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, null);
    }

    public CTAButton(Context context, View parent) {
        super(context);
        init(context, parent);
    }

    void init(final Context context, final View parent) {
        this.setOnClickListener(new OnClickListener() { @Override public void onClick(final View v) { onCtaButtonClick(v); }});
    }

    public void setNavigationData(CTAButtonNavigationData navData)
    {
        this.navigationData = navData;
    }

    private void onCtaButtonClick(final View v)
    {
        //if we have a valid in app navigation id, go there
        //otherwise use the backup link
            //todo: check if the app nav id is valid / supported on this platform
        if(this.navigationData.appNavigationId != null
           && !this.navigationData.appNavigationId.isEmpty())
        {
            System.out.println("Going to app nav id : " + this.navigationData.appNavigationId);
        }
        else if(this.navigationData.backupWebURL != null
           && !this.navigationData.backupWebURL.isEmpty())
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.navigationData.backupWebURL));
            getContext().startActivity(browserIntent);
        }
    }
}