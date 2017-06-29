package com.handybook.handybook.vegas;

import com.handybook.handybook.vegas.ui.GameFragment;
import com.handybook.handybook.vegas.ui.PreGameFragment;
import com.handybook.handybook.vegas.ui.VegasActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                VegasActivity.class,
                GameFragment.class,
                PreGameFragment.class
        })
public class VegasModule {
}
