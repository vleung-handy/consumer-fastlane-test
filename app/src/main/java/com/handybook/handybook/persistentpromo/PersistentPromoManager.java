package com.handybook.handybook.persistentpromo;

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
public class PersistentPromoManager
{
    private final DataManager mDataManager;
    private final Context mContext;

    @Inject
    public PersistentPromoManager(
            final Context context,
            final DataManager dataManager
    )
    {
        mContext = context;
        mDataManager = dataManager;
    }

    public void getPersistentPromo(@Nullable String postalCode,
                                   @NonNull DataManager.Callback<PersistentPromo> persistentPromoCallback)
    {
        //FIXME revert, test only
        mDataManager.getAvailablePersistentPromo("99500", persistentPromoCallback);
//        persistentPromoCallback.onSuccess(getTestPersistentPromo(mContext));
    }

    private PersistentPromo getTestPersistentPromo(Context context)
    {
        String json = null;
        try
        {
            json = IOUtils.loadJSONFromAsset(context, "test_persistent_promo.json");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new GsonBuilder().create().fromJson(json, PersistentPromo.class);
    }
}
