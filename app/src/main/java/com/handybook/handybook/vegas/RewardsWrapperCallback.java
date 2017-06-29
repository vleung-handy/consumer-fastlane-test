package com.handybook.handybook.vegas;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.TypedHandyRetrofitCallback;
import com.handybook.handybook.vegas.model.RewardsWrapper;

public class RewardsWrapperCallback
        extends TypedHandyRetrofitCallback<RewardsWrapper> {

    RewardsWrapperCallback(final DataManager.Callback callback) {
        super(callback);
    }
}
