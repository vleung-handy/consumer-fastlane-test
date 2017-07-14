package com.handybook.handybook.helpcenter;

import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.helpcenter.helpcontact.manager.HelpContactManager;
import com.handybook.handybook.helpcenter.manager.HelpManager;
import com.handybook.handybook.helpcenter.ui.activity.HelpActivity;
import com.handybook.handybook.helpcenter.ui.fragment.HelpFragment;
import com.handybook.handybook.helpcenter.ui.fragment.HelpWebViewFragment;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                HelpActivity.class,
                HelpWebViewFragment.class,
                HelpFragment.class
        })
public final class HelpModule {

    @Provides
    @Singleton
    final HelpManager provideHelpManager(
            final EventBus bus,
            final DataManager dataManager,
            final UserManager userManager
    ) {
        return new HelpManager(bus, dataManager, userManager);
    }

    @Provides
    @Singleton
    final HelpContactManager provideHelpContactManager(
            final EventBus bus,
            final DataManager dataManager
    ) {
        return new HelpContactManager(bus, dataManager);
    }
}
