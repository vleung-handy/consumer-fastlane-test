package com.handybook.handybook.core;

import android.support.v4.util.Pair;

import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.BookingFlowClearedEvent;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public final class BookingManager implements Observer
{
    private BookingRequest request;
    private BookingQuote quote;
    private BookingTransaction transaction;
    private BookingPostInfo postInfo;
    private final PrefsManager prefsManager;
    private final DataManager dataManager;
    private final Bus bus;

    @Inject
    BookingManager(final Bus bus, final PrefsManager prefsManager, final DataManager dataManager)
    {
        this.prefsManager = prefsManager;
        this.dataManager = dataManager;
        this.bus = bus;
        this.bus.register(this);
    }
    //Event listening sending, half way to updating our managers to work like nortal's managers and provide a layer for data access

    @Subscribe
    public void onRequestPreRescheduleInfo(HandyEvent.RequestPreRescheduleInfo event)
    {
        dataManager.getPreRescheduleInfo(event.bookingId, new DataManager.Callback<String>()
        {
            @Override
            public void onSuccess(String notice)
            {
                bus.post(new HandyEvent.ReceivePreRescheduleInfoSuccess(notice));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceivePreRescheduleInfoError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPreCancelationInfo(HandyEvent.RequestPreCancelationInfo event)
    {
        dataManager.getPreCancelationInfo(event.bookingId, new DataManager.Callback<Pair<String, List<String>>>()
        {
            @Override
            public void onSuccess(final Pair<String, List<String>> result)
            {
                bus.post(new HandyEvent.ReceivePreCancelationInfoSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceivePreCancelationInfoError(error));
            }
        });
    }


    @Subscribe
    public void onRequestUpdateBookingNoteToPro(HandyEvent.RequestUpdateBookingNoteToPro event)
    {
        dataManager.updateBookingNoteToPro(event.bookingId, event.descriptionTransaction, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
                bus.post(new HandyEvent.ReceiveUpdateBookingNoteToProSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveUpdateBookingNoteToProError(error));
            }
        });
    }

    @Subscribe
    public void onRequestUpdateBookingEntryInformation(HandyEvent.RequestUpdateBookingEntryInformation event)
    {
        dataManager.updateBookingEntryInformation(event.bookingId, event.entryInformationTransaction, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
                bus.post(new HandyEvent.ReceiveUpdateBookingEntryInformationSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveUpdateBookingEntryInformationError(error));
            }
        });
    }

    @Subscribe
    public void onRequestBookings(HandyEvent.RequestBookingsForUser event)
    {
        dataManager.getBookings(event.user, new DataManager.Callback<List<Booking>>()
        {
            @Override
            public void onSuccess(final List<Booking> result)
            {
                bus.post(new HandyEvent.ReceiveBookingsSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveBookingsError(error));
            }
        });
    }

    @Subscribe
    public void onRequestBookingDetails(HandyEvent.RequestBookingDetails event)
    {
        dataManager.getBooking(event.bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(final Booking result)
            {
                bus.post(new HandyEvent.ReceiveBookingDetailsSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveBookingDetailsError(error));
            }
        });
    }

























//Old Direct References


    public final BookingRequest getCurrentRequest()
    {
        if (request != null)
        {
            return request;
        }
        else
        {
            if ((request = BookingRequest.fromJson(prefsManager.getString(PrefsKey.BOOKING_REQUEST))) != null)
            {
                request.addObserver(this);
            }
            return request;
        }
    }

    public final void setCurrentRequest(final BookingRequest newRequest)
    {
        if (request != null)
        {
            request.deleteObserver(this);
        }

        if (newRequest == null)
        {
            request = null;
            prefsManager.setString(PrefsKey.BOOKING_REQUEST, null);
            return;
        }

        request = newRequest;
        request.addObserver(this);

        prefsManager.setString(PrefsKey.BOOKING_REQUEST, request.toJson());
    }

    public final BookingQuote getCurrentQuote()
    {
        if (quote != null)
        {
            return quote;
        }
        else
        {
            if ((quote = BookingQuote.fromJson(prefsManager.getString(PrefsKey.BOOKING_QUOTE))) != null)
            {
                quote.addObserver(this);
            }
            return quote;
        }
    }

    public final void setCurrentQuote(final BookingQuote newQuote)
    {
        if (quote != null)
        {
            quote.deleteObserver(this);
        }

        if (newQuote == null)
        {
            quote = null;
            prefsManager.setString(PrefsKey.BOOKING_QUOTE, null);
            return;
        }

        quote = newQuote;
        quote.addObserver(this);
        prefsManager.setString(PrefsKey.BOOKING_QUOTE, quote.toJson());
    }

    public final BookingTransaction getCurrentTransaction()
    {
        if (transaction != null)
        {
            return transaction;
        }
        else
        {
            if ((transaction = BookingTransaction
                    .fromJson(prefsManager.getString(PrefsKey.BOOKING_TRANSACTION))) != null)
            {
                transaction.addObserver(this);
            }
            return transaction;
        }
    }

    public final void setCurrentTransaction(final BookingTransaction newTransaction)
    {
        if (transaction != null)
        {
            transaction.deleteObserver(this);
        }

        if (newTransaction == null)
        {
            transaction = null;
            prefsManager.setString(PrefsKey.BOOKING_TRANSACTION, null);
            return;
        }

        transaction = newTransaction;
        transaction.addObserver(this);
        prefsManager.setString(PrefsKey.BOOKING_TRANSACTION, transaction.toJson());
    }

    public final BookingPostInfo getCurrentPostInfo()
    {
        if (postInfo != null)
        {
            return postInfo;
        }
        else
        {
            if ((postInfo = BookingPostInfo
                    .fromJson(prefsManager.getString(PrefsKey.BOOKING_POST))) != null)
            {
                postInfo.addObserver(this);
            }
            return postInfo;
        }
    }

    public final void setCurrentPostInfo(final BookingPostInfo newInfo)
    {
        if (postInfo != null)
        {
            postInfo.deleteObserver(this);
        }

        if (newInfo == null)
        {
            postInfo = null;
            prefsManager.setString(PrefsKey.BOOKING_POST, null);
            return;
        }

        postInfo = newInfo;
        postInfo.addObserver(this);
        prefsManager.setString(PrefsKey.BOOKING_POST, postInfo.toJson());
    }

    public final void setPromoTabCoupon(final String code)
    {
        prefsManager.setString(PrefsKey.BOOKING_PROMO_TAB_COUPON, code);
    }

    public final String getPromoTabCoupon()
    {
        return prefsManager.getString(PrefsKey.BOOKING_PROMO_TAB_COUPON);
    }

    @Override
    public void update(final Observable observable, final Object data)
    {
        if (observable instanceof BookingRequest)
        {
            setCurrentRequest((BookingRequest) observable);
        }
        if (observable instanceof BookingQuote)
        {
            setCurrentQuote((BookingQuote) observable);
        }

        if (observable instanceof BookingTransaction)
        {
            setCurrentTransaction((BookingTransaction) observable);
        }

        if (observable instanceof BookingPostInfo)
        {
            setCurrentPostInfo((BookingPostInfo) observable);
        }
    }

    public void clear()
    {
        setCurrentRequest(null);
        setCurrentQuote(null);
        setCurrentTransaction(null);
        setCurrentPostInfo(null);
        prefsManager.setString(PrefsKey.STATE_BOOKING_CLEANING_EXTRAS_SELECTION, null);
        bus.post(new BookingFlowClearedEvent());
    }

    public void clearAll()
    {
        prefsManager.setString(PrefsKey.BOOKING_PROMO_TAB_COUPON, null);
        clear();
    }

    @Subscribe
    public final void environmentUpdated(final EnvironmentUpdatedEvent event)
    {
        if (event.getEnvironment() != event.getPrevEnvironment())
        {
            clearAll();
        }
    }

    @Subscribe
    public final void userAuthUpdated(final UserLoggedInEvent event)
    {
        if (!event.isLoggedIn())
        {
            clearAll();
        }
    }
}
