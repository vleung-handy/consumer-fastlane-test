package com.handybook.handybook.core;

import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.data.HandyRetrofitEndpoint;
import com.handybook.handybook.data.HandyRetrofitService;
import com.handybook.handybook.manager.PrefsManager;

public class TestDataManager extends DataManager
{
    public TestDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final PrefsManager prefsManager)
    {
        super(service, endpoint, prefsManager);
    }

    @Override
    public void validateBookingZip(
            final int serviceId, final String zipCode, final String userId,
            final String promoCode, final Callback<ZipValidationResponse> cb
    )
    {
        cb.onSuccess(new ZipValidationResponse());
    }
}