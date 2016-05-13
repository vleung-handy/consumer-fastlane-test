package com.handybook.handybook.helpcenter;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.helpcenter.helpcontact.manager.HelpContactManager;
import com.handybook.handybook.helpcenter.helpcontact.ui.activity.HelpContactActivity;
import com.handybook.handybook.helpcenter.helpcontact.ui.fragment.HelpContactFragment;
import com.handybook.handybook.helpcenter.manager.HelpManager;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpNativeActivity;
import com.handybook.handybook.helpcenter.ui.activity.HelpWebViewActivity;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpNativeFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                HelpActivity.class,
                HelpFragment.class,
                HelpWebViewActivity.class,
                HelpWebViewFragment.class,
                HelpNativeActivity.class,
                HelpNativeFragment.class,
                HelpContactFragment.class,
                HelpContactActivity.class,
        })
public final class HelpModule
{
    @Provides
    @Singleton
    final HelpManager provideHelpManager(
            final Bus bus,
            final DataManager dataManager,
            final UserManager userManager
    )
    {
        return new HelpManager(bus, dataManager, userManager);
    }

    @Provides
    @Singleton
    final HelpContactManager provideHelpContactManager(
            final Bus bus,
            final DataManager dataManager
    )
    {
        return new HelpContactManager(bus, dataManager);
    }
}