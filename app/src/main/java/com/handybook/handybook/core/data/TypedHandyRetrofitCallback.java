package com.handybook.handybook.core.data;

import com.crashlytics.android.Crashlytics;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.handybook.handybook.account.model.RecurringPlanWrapper;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingGeoStatus;
import com.handybook.handybook.booking.model.BookingOptionsWrapper;
import com.handybook.handybook.booking.model.BookingProRequestResponse;
import com.handybook.handybook.booking.model.BookingRequestablePros;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.helpcenter.model.HelpNodeWrapper;
import com.handybook.handybook.core.model.response.HelpCenterResponse;
import com.handybook.handybook.core.model.response.UserExistsResponse;
import com.handybook.handybook.configuration.model.Configuration;
import com.handybook.handybook.notifications.model.HandyNotification;
import com.handybook.handybook.promos.splash.SplashPromo;
import com.handybook.handybook.promos.persistent.PersistentPromo;
import com.handybook.handybook.referral.model.RedemptionDetailsResponse;
import com.handybook.handybook.referral.model.ReferralResponse;

import org.json.JSONObject;

public abstract class TypedHandyRetrofitCallback<T> extends HandyRetrofitCallback
{
    protected static final Gson gsonBuilder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
    protected T returnData;

    TypedHandyRetrofitCallback(DataManager.Callback callback)
    {
        super(callback);
    }

    @Override
    public void success(final JSONObject response)
    {
        try
        {
            TypeToken<T> typeToken = new TypeToken<T>(getClass())
            {
            };
            returnData = gsonBuilder.fromJson(response.toString(), typeToken.getType());
        }
        catch (JsonSyntaxException e)
        {
            Crashlytics.logException(e);
        }

        if (callback != null)
        {
            callback.onSuccess(returnData);
        }
    }
}


//We need to trick the compiler into holding onto the generic type so we don't lose it to erasure
class ZipValidationRetroFitCallback extends TypedHandyRetrofitCallback<ZipValidationResponse>
{
    ZipValidationRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingMilestonesCallback extends TypedHandyRetrofitCallback<JobStatus>
{
    BookingMilestonesCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingOptionsWrapperHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingOptionsWrapper>
{
    BookingOptionsWrapperHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class HelpNodeWrapperResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<HelpNodeWrapper>
{
    HelpNodeWrapperResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingRequestableProsResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingRequestablePros>
{
    BookingRequestableProsResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingProRequestResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingProRequestResponse>
{
    BookingProRequestResponseHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class UserBookingsWrapperHandyRetroFitCallback extends TypedHandyRetrofitCallback<UserBookingsWrapper>
{
    UserBookingsWrapperHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingPricesForFrequenciesHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingEditFrequencyInfoResponse>
{
    BookingPricesForFrequenciesHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class EditExtrasInfoHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingEditExtrasInfoResponse>
{
    EditExtrasInfoHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class EditHoursInfoHandyRetroFitCallback extends TypedHandyRetrofitCallback<BookingEditHoursInfoResponse>
{
    EditHoursInfoHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}

class EntryMethodsInfoHandyRetroFitCallback extends TypedHandyRetrofitCallback<EntryMethodsInfo>
{
    EntryMethodsInfoHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class RecurringPlanHandyRetroFitCallback extends TypedHandyRetrofitCallback<RecurringPlanWrapper>
{
    RecurringPlanHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class SuccessHandyRetroFitCallback extends TypedHandyRetrofitCallback<SuccessWrapper>
{
    SuccessHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }
}


class UserExistsHandyRetrofitCallback extends TypedHandyRetrofitCallback<UserExistsResponse>
{

    UserExistsHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class AvailableSplashPromoRetrofitCallback extends TypedHandyRetrofitCallback<SplashPromo>
{
    AvailableSplashPromoRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class AvailablePersistentPromoRetrofitCallback extends TypedHandyRetrofitCallback<PersistentPromo>
{
    AvailablePersistentPromoRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class HandyNotificationResultSetHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<HandyNotification.ResultSet>
{
    HandyNotificationResultSetHandyRetrofitCallback(final DataManager.Callback cb) {super(cb);}
}


class ReferralResponseHandyRetrofitCallback extends TypedHandyRetrofitCallback<ReferralResponse>
{
    ReferralResponseHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class RedemptionDetailsResponseHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<RedemptionDetailsResponse>
{
    RedemptionDetailsResponseHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class HelpCenterResponseHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<HelpCenterResponse>
{
    HelpCenterResponseHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}

class ConfigurationHandyRetrofitCallback extends TypedHandyRetrofitCallback<Configuration>
{
    ConfigurationHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class RecurringBookingsResponseHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<RecurringBookingsResponse>
{
    RecurringBookingsResponseHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingGeoStatusHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<BookingGeoStatus>
{
    BookingGeoStatusHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class BookingLocationStatusHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<Booking.LocationStatus>
{
    BookingLocationStatusHandyRetrofitCallback(final DataManager.Callback callback)
    {
        super(callback);
    }
}


class EmptyHandyRetroFitCallback extends TypedHandyRetrofitCallback<Void>
{
    EmptyHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }

}
