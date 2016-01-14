package com.handybook.handybook.booking.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.BookingFlowClearedEvent;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

//TODO: Add caching like we do for portal, navigating back and forth from my bookings page is painfully slow right now
public class BookingManager implements Observer
{
    private final PrefsManager prefsManager;
    private final DataManager dataManager;
    private final Bus bus;
    private BookingRequest request;
    private BookingQuote quote;
    private BookingTransaction transaction;
    private BookingPostInfo postInfo;

    @Inject
    public BookingManager(final Bus bus, final PrefsManager prefsManager, final DataManager dataManager)
    {
        this.prefsManager = prefsManager;
        this.dataManager = dataManager;
        this.bus = bus;
        this.bus.register(this);
    }

    // Event listening + sending, half way to updating our managers to work like nortal's managers
    // and provide a layer for data access

    @Subscribe
    public void onRequestPreBookingPromo(BookingEvent.RequestPreBookingPromo event)
    {
        dataManager.getPreBookingPromo(event.getPromoCode(), new DataManager.Callback<PromoCode>()
        {
            @Override
            public void onSuccess(final PromoCode response)
            {
                //this is the logic in the direct callback in PromosFragment
                if (response.getType() == PromoCode.Type.COUPON)
                {
                    /*
                    need to set this in the manager because the fragments of the
                    subscribers might be dead when this response comes back
                     */
                    setPromoTabCoupon(response.getCode());
                }
                bus.post(new BookingEvent.ReceivePreBookingPromoSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceivePreBookingPromoError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPreRescheduleInfo(BookingEvent.RequestPreRescheduleInfo event)
    {
        dataManager.getPreRescheduleInfo(event.bookingId, new DataManager.Callback<String>()
        {
            @Override
            public void onSuccess(String notice)
            {
                bus.post(new BookingEvent.ReceivePreRescheduleInfoSuccess(notice));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceivePreRescheduleInfoError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPreCancelationInfo(BookingEvent.RequestPreCancelationInfo event)
    {
        dataManager.getPreCancelationInfo(event.bookingId, new DataManager.Callback<Pair<String,
                List<String>>>()
        {
            @Override
            public void onSuccess(final Pair<String, List<String>> result)
            {
                bus.post(new BookingEvent.ReceivePreCancelationInfoSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceivePreCancelationInfoError(error));
            }
        });
    }


    @Subscribe
    public void onRequestUpdateBookingNoteToPro(BookingEditEvent.RequestUpdateBookingNoteToPro event)
    {
        dataManager.updateBookingNoteToPro(event.bookingId, event.descriptionTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        bus.post(new BookingEditEvent.ReceiveUpdateBookingNoteToProSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new BookingEditEvent.ReceiveUpdateBookingNoteToProError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestBookingCardViewModels(
            @NonNull final HandyEvent.RequestEvent.BookingCardViewModelsEvent event)
    {
        if (null != event.getOnlyBookingValue())
        {
            dataManager.getBookings(
                    event.getUser(),
                    event.getOnlyBookingValue(),
                    new DataManager.Callback<UserBookingsWrapper>()
                    {
                        @Override
                        public void onSuccess(final UserBookingsWrapper result)
                        {
                            final List<Booking> bookings = result.getBookings();
                            Collections.sort(bookings, Booking.COMPARATOR_DATE);
                            // Mark bookingCardViewModels accordingly and emit it.
                            BookingCardViewModel.List models = getBookingCardViewModelListFromResult(
                                    event.getOnlyBookingValue(), bookings);
                            bus.post(new HandyEvent.ResponseEvent.BookingCardViewModels(models));
                        }

                        @Override
                        public void onError(DataManager.DataManagerError error)
                        {
                            bus.post(new HandyEvent.ResponseEvent.BookingCardViewModelsError(error));
                        }
                    });
        }
    }

    private BookingCardViewModel.List getBookingCardViewModelListFromResult(
            String onlyBookingValue, List<Booking> bookings)
    {
        BookingCardViewModel.List models = BookingCardViewModel.List.empty();
        switch (onlyBookingValue)
        {
            case Booking.List.VALUE_ONLY_BOOKINGS_PAST:
                models = BookingCardViewModel.List
                        .from(bookings, BookingCardViewModel.List.TYPE_PAST);
                break;
            case Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING:
                models = BookingCardViewModel.List
                        .from(bookings, BookingCardViewModel.List.TYPE_UPCOMING);
                break;
            default:
                Crashlytics.logException(
                        new RuntimeException("Unrecognized booking list type: " + onlyBookingValue));
        }
        return models;
    }

    @Subscribe
    public void onRequestBookingDetails(BookingEvent.RequestBookingDetails event)
    {
        dataManager.getBooking(event.bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(final Booking result)
            {
                bus.post(new BookingEvent.ReceiveBookingDetailsSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceiveBookingDetailsError(error));
            }
        });
    }

    @Subscribe
    public void onRequestRateBooking(BookingEvent.RateBookingEvent event)
    {
        dataManager.ratePro(
                event.getBookingId(),
                event.getFinalRating(),
                event.getTipAmountCents(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        bus.post(new BookingEvent.ReceiveRateBookingSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new BookingEvent.ReceiveRateBookingError(error));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestTipPro(BookingEvent.RequestTipPro event)
    {
        dataManager.tipPro(event.bookingId, event.tipAmount, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
                bus.post(new BookingEvent.ReceiveTipProSuccess());
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceiveTipProError());
            }
        });
    }

//Old Direct References, to eventually be handled in the events way

    public BookingRequest getCurrentRequest()
    {
        if (request != null)
        {
            return request;
        }
        else
        {
            if ((request = BookingRequest
                    .fromJson(prefsManager.getString(PrefsKey.BOOKING_REQUEST))) != null)
            {
                request.addObserver(this);
            }
            return request;
        }
    }

    public void setCurrentRequest(final BookingRequest newRequest)
    {
        if (request != null)
        {
            request.deleteObserver(this);
        }

        if (newRequest == null)
        {
            request = null;
            prefsManager.removeValue(PrefsKey.BOOKING_REQUEST);
            return;
        }

        request = newRequest;
        request.addObserver(this);

        prefsManager.setString(PrefsKey.BOOKING_REQUEST, request.toJson());
    }

    public BookingQuote getCurrentQuote()
    {
        if (quote != null)
        {
            return quote;
        }
        else
        {
            if ((quote = BookingQuote
                    .fromJson(prefsManager.getString(PrefsKey.BOOKING_QUOTE))) != null)
            {
                quote.addObserver(this);
            }
            return quote;
        }
    }

    public void setCurrentQuote(final BookingQuote newQuote)
    {
        if (quote != null)
        {
            quote.deleteObserver(this);
        }

        if (newQuote == null)
        {
            quote = null;
            prefsManager.removeValue(PrefsKey.BOOKING_QUOTE);
            return;
        }

        quote = newQuote;
        quote.addObserver(this);
        prefsManager.setString(PrefsKey.BOOKING_QUOTE, quote.toJson());
    }

    public BookingTransaction getCurrentTransaction()
    {
        if (transaction != null)
        {
            return transaction;
        }
        else
        {
            final String transactionJson = prefsManager.getString(PrefsKey.BOOKING_TRANSACTION);
            Crashlytics.log("Transaction JSON is " + transactionJson);
            if (transactionJson == null)
            {
                return null;
            }
            transaction = BookingTransaction.fromJson(transactionJson);
            if (transaction != null)
            {
                transaction.addObserver(this);
            }
            else
            {
                Crashlytics.log("Transaction object is null");
            }
            return transaction;
        }
    }

    public void setCurrentTransaction(final BookingTransaction newTransaction)
    {
        if (transaction != null)
        {
            transaction.deleteObserver(this);
        }

        if (newTransaction == null)
        {
            transaction = null;
            prefsManager.removeValue(PrefsKey.BOOKING_TRANSACTION);
            return;
        }

        transaction = newTransaction;
        transaction.addObserver(this);
        prefsManager.setString(PrefsKey.BOOKING_TRANSACTION, transaction.toJson());
    }

    public BookingPostInfo getCurrentPostInfo()
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

    public void setCurrentPostInfo(final BookingPostInfo newInfo)
    {
        if (postInfo != null)
        {
            postInfo.deleteObserver(this);
        }

        if (newInfo == null)
        {
            postInfo = null;
            prefsManager.removeValue(PrefsKey.BOOKING_POST);
            return;
        }

        postInfo = newInfo;
        postInfo.addObserver(this);
        prefsManager.setString(PrefsKey.BOOKING_POST, postInfo.toJson());
    }

    public void setPromoTabCoupon(final String code)
    {
        prefsManager.setString(PrefsKey.BOOKING_PROMO_TAB_COUPON, code);
    }

    @Nullable
    public String getPromoTabCoupon()
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
        prefsManager.removeValue(PrefsKey.STATE_BOOKING_CLEANING_EXTRAS_SELECTION);
        bus.post(new BookingFlowClearedEvent());
    }

    public void clearAll()
    {
        prefsManager.removeValue(PrefsKey.BOOKING_PROMO_TAB_COUPON);
        clear();
    }

    @Subscribe
    public void environmentUpdated(final EnvironmentUpdatedEvent event)
    {
        if (!event.getEnvironment().equals(event.getPrevEnvironment()))
        {
            clearAll();
        }
    }

    @Subscribe
    public void userAuthUpdated(final UserLoggedInEvent event)
    {
        if (!event.isLoggedIn())
        {
            clearAll();
        }
    }

    @Subscribe
    public void onRequestSendCancelRecurringBookingEmail(
            final BookingEvent.RequestSendCancelRecurringBookingEmail event)
    {
        dataManager.sendCancelRecurringBookingEmail(event.bookingRecurringId, new DataManager
                .Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper response)
            {
                bus.post(new BookingEvent.ReceiveSendCancelRecurringBookingEmailSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceiveSendCancelRecurringBookingEmailError(error));

            }
        });
    }

    /**
     * TODO: no endpoint to only return the recurring bookings, must fetch part of the user
     * bookings payload for now
     * TODO: would be nice to have caching
     * @param event
     */
    @Subscribe
    public final void onRequestRecurringBookings(
            final BookingEvent.RequestRecurringBookingsForUser event)
    {

        dataManager.getBookings(event.user, new DataManager.Callback<UserBookingsWrapper>()
        {
            @Override
            public void onSuccess(final UserBookingsWrapper result)
            {
                //TODO: need to sort the recurring bookings?
                bus.post(new BookingEvent.ReceiveRecurringBookingsSuccess(result.getRecurringBookings()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new BookingEvent.ReceiveRecurringBookingsError(error));
            }
        });
    }
}
