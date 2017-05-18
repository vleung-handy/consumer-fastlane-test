package com.handybook.handybook.proprofiles;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.proprofiles.ui.ProProfileActivity;
import com.handybook.handybook.proprofiles.ui.ProProfileFragment;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                ProProfileFragment.class,
                ProProfileActivity.class
                //todo turn some of the profile views into fragments and inject here
        })
public final class ProProfileModule {

    @Provides
    @Singleton
    final ProProfileManager provideProProfileManager(final DataManager dataManager) {
        return new ProProfileManager(dataManager);
    }
}
