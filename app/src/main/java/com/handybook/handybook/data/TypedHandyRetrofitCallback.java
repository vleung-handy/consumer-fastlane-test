package com.handybook.handybook.data;

import com.crashlytics.android.Crashlytics;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingOptionsWrapper;
import com.handybook.handybook.booking.model.BookingProRequestResponse;
import com.handybook.handybook.booking.model.BookingRequestablePros;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.helpcenter.model.HelpNodeWrapper;
import com.handybook.handybook.model.response.UserExistsResponse;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;

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
class BookingHandyRetroFitCallback extends TypedHandyRetrofitCallback<Booking>
{
    BookingHandyRetroFitCallback(DataManager.Callback callback)
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


class ProviderResponseHandyRetroFitCallback extends TypedHandyRetrofitCallback<Booking.Provider>
{
    ProviderResponseHandyRetroFitCallback(DataManager.Callback callback)
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

class HandyNotificationResultSetHandyRetrofitCallback
        extends TypedHandyRetrofitCallback<HandyNotification.ResultSet>
{
    HandyNotificationResultSetHandyRetrofitCallback(final DataManager.Callback cb) {super(cb);}
}


class EmptyHandyRetroFitCallback extends TypedHandyRetrofitCallback<Void>
{
    EmptyHandyRetroFitCallback(DataManager.Callback callback)
    {
        super(callback);
    }

}
