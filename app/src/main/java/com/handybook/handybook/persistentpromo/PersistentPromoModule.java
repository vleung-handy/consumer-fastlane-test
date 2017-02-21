package com.handybook.handybook.persistentpromo;

import com.handybook.handybook.core.data.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        complete = false,
        injects = {
                PersistentPromoCoordinatorLayout.class
        })
public final class PersistentPromoModule {

    @Provides
    @Singleton
    final PersistentPromoManager providePersistentPromoManager(
            final DataManager dataManager
    ) {
        return new PersistentPromoManager(dataManager);
    }
}
