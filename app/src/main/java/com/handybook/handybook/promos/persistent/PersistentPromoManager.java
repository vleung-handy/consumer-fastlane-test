package com.handybook.handybook.promos.persistent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.GsonBuilder;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.library.util.IOUtils;

import javax.inject.Inject;

/**
 * note that this may acquire more responsibility later
 */
public class PersistentPromoManager {

    private final DataManager mDataManager;

    @Inject
    public PersistentPromoManager(final DataManager dataManager) {
        mDataManager = dataManager;
    }

    public void getPersistentPromo(
            @Nullable String postalCode,
            @NonNull DataManager.Callback<PersistentPromo> persistentPromoCallback
    ) {
        //TODO revert when we actually use this feature
        mDataManager.getAvailablePersistentPromo(postalCode, persistentPromoCallback);
        //        persistentPromoCallback.onSuccess(getTestPersistentPromo(mContext));
    }

    //TODO remove once we actually integrate this feature
    private PersistentPromo getTestPersistentPromo(Context context) {
        String json = null;
        try {
            json = IOUtils.loadJSONFromAsset(context, "testdata/test_persistent_promo.json");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new GsonBuilder().create().fromJson(json, PersistentPromo.class);
    }
}
