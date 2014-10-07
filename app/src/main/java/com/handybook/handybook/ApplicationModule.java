package com.handybook.handybook;

import dagger.Module;
import dagger.Provides;

@Module(injects = {ServiceCategoriesActivity.class, ServiceCategoriesFragment.class,
        LoginActivityFragment.class})
final class ApplicationModule {
    @Provides DataManager provideDataManager() {
        return new BaseDataManager();
    }
}
