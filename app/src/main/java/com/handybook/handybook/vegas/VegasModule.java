package com.handybook.handybook.vegas;

import com.handybook.handybook.vegas.ui.PreGameFragment;
import com.handybook.handybook.vegas.ui.ScratchOffGameFragment;
import com.handybook.handybook.vegas.ui.VegasActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                VegasActivity.class,
                ScratchOffGameFragment.class,
                PreGameFragment.class
        })
public class VegasModule {
}
