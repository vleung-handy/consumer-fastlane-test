package com.handybook.handybook.vegas;

import com.handybook.handybook.vegas.ui.PreGameFragment;
import com.handybook.handybook.vegas.ui.RatingFlowGameFragment;
import com.handybook.handybook.vegas.ui.VegasDevActivity;

import dagger.Module;

@Module(
        library = true,
        complete = false,
        injects = {
                VegasDevActivity.class,
                RatingFlowGameFragment.class,
                PreGameFragment.class
        })
public class VegasModule {
}
