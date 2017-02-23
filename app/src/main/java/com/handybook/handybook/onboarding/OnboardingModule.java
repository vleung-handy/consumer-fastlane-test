package com.handybook.handybook.onboarding;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                OnboardActivity.class,
                OnboardFragment.class,
                OnboardV2Fragment.class,
                OnboardPageFragment.class,
                ServiceNotSupportedActivity.class,
        })
public final class OnboardingModule {

}
