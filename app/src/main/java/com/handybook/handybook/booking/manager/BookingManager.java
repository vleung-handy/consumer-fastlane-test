package com.handybook.handybook.booking.manager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.viewmodel.BookingCardViewModel;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BaseApplication;
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
    private final PrefsManager mPrefsManager;
    private final DataManager mDataManager;
    private final Bus mBus;
    private BookingRequest mBookingRequest;
    private BookingQuote mBookingQuote;
    private BookingTransaction mBookingTransaction;
    private BookingPostInfo mBookingPostInfo;
    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload;

    @Inject
    public BookingManager(final Bus bus, final PrefsManager prefsManager, final DataManager dataManager)
    {
        mPrefsManager = prefsManager;
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    // Event listening + sending, half way to updating our managers to work like nortal's managers
    // and provide a layer for data access

    @Subscribe
    public void onRequestPreBookingPromo(BookingEvent.RequestPreBookingPromo event)
    {
        mDataManager.getPreBookingPromo(event.getPromoCode(), new DataManager.Callback<PromoCode>()
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
                mBus.post(new BookingEvent.ReceivePreBookingPromoSuccess(response));
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceivePreBookingPromoError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPreRescheduleInfo(BookingEvent.RequestPreRescheduleInfo event)
    {
        mDataManager.getPreRescheduleInfo(event.bookingId, new DataManager.Callback<String>()
        {
            @Override
            public void onSuccess(String notice)
            {
                mBus.post(new BookingEvent.ReceivePreRescheduleInfoSuccess(notice));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceivePreRescheduleInfoError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPreCancelationInfo(BookingEvent.RequestPreCancelationInfo event)
    {
        mDataManager.getPreCancelationInfo(event.bookingId, new DataManager.Callback<Pair<String,
                List<String>>>()
        {
            @Override
            public void onSuccess(final Pair<String, List<String>> result)
            {
                mBus.post(new BookingEvent.ReceivePreCancelationInfoSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceivePreCancelationInfoError(error));
            }
        });
    }


    @Subscribe
    public void onRequestUpdateBookingNoteToPro(BookingEditEvent.RequestUpdateBookingNoteToPro event)
    {
        mDataManager.updateBookingNoteToPro(event.bookingId, event.descriptionTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveUpdateBookingNoteToProSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveUpdateBookingNoteToProError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestBookingCardViewModels(
            @NonNull final HandyEvent.RequestEvent.BookingCardViewModelsEvent event
    )
    {
        final long nanosNow = System.nanoTime();
        if (null != event.getOnlyBookingValue())
        {
            mDataManager.getBookings(
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
                                    bookings,
                                    event.getOnlyBookingValue());
                            BaseApplication.tracker().send(
                                    new HitBuilders.TimingBuilder()
                                            .setCategory("Api")
                                            .setValue((System.nanoTime() - nanosNow) / 1000000)
                                            .setVariable("Bookings")
                                            .setLabel(event.getOnlyBookingValue())
                                            .build()
                            );
                            mBus.post(new HandyEvent.ResponseEvent.BookingCardViewModels(models));
                        }

                        @Override
                        public void onError(DataManager.DataManagerError error)
                        {
                            mBus.post(new HandyEvent.ResponseEvent.BookingCardViewModelsError(error));
                        }
                    });
        }
    }

    private BookingCardViewModel.List getBookingCardViewModelListFromResult(
            @NonNull List<Booking> bookings,
            @Booking.List.OnlyBookingValues @NonNull String onlyBookingValue
    )
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
        mDataManager.getBooking(event.bookingId, new DataManager.Callback<Booking>()
        {
            @Override
            public void onSuccess(final Booking result)
            {
                mBus.post(new BookingEvent.ReceiveBookingDetailsSuccess(result));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceiveBookingDetailsError(error));
            }
        });
    }

    @Subscribe
    public void onRequestRateBooking(BookingEvent.RateBookingEvent event)
    {
        mDataManager.ratePro(
                event.getBookingId(),
                event.getFinalRating(),
                event.getTipAmountCents(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEvent.ReceiveRateBookingSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveRateBookingError(error));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestTipPro(BookingEvent.RequestTipPro event)
    {
        mDataManager.tipPro(event.bookingId, event.tipAmount, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
                mBus.post(new BookingEvent.ReceiveTipProSuccess());
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceiveTipProError());
            }
        });
    }

//Old Direct References, to eventually be handled in the events way

    public BookingRequest getCurrentRequest()
    {
        if (mBookingRequest != null)
        {
            return mBookingRequest;
        }
        else
        {
            if ((mBookingRequest = BookingRequest
                    .fromJson(mPrefsManager.getString(PrefsKey.BOOKING_REQUEST))) != null)
            {
                mBookingRequest.addObserver(this);
            }
            return mBookingRequest;
        }
    }

    public void setCurrentRequest(final BookingRequest newRequest)
    {
        if (mBookingRequest != null)
        {
            mBookingRequest.deleteObserver(this);
        }

        if (newRequest == null)
        {
            mBookingRequest = null;
            mPrefsManager.removeValue(PrefsKey.BOOKING_REQUEST);
            return;
        }

        mBookingRequest = newRequest;
        mBookingRequest.addObserver(this);

        mPrefsManager.setString(PrefsKey.BOOKING_REQUEST, mBookingRequest.toJson());
    }

    public BookingQuote getCurrentQuote()
    {
        if (mBookingQuote != null)
        {
            return mBookingQuote;
        }
        else
        {
            if ((mBookingQuote = BookingQuote
                    .fromJson(mPrefsManager.getString(PrefsKey.BOOKING_QUOTE))) != null)
            {
                mBookingQuote.addObserver(this);
            }
            return mBookingQuote;
        }
    }

    public void setCurrentQuote(final BookingQuote newQuote)
    {
        if (mBookingQuote != null)
        {
            mBookingQuote.deleteObserver(this);
        }

        if (newQuote == null)
        {
            mBookingQuote = null;
            mPrefsManager.removeValue(PrefsKey.BOOKING_QUOTE);
            return;
        }

        mBookingQuote = newQuote;
        mBookingQuote.addObserver(this);
        mPrefsManager.setString(PrefsKey.BOOKING_QUOTE, mBookingQuote.toJson());
    }

    public BookingTransaction getCurrentTransaction()
    {
        if (mBookingTransaction != null)
        {
            return mBookingTransaction;
        }
        else
        {
            final String transactionJson = mPrefsManager.getString(PrefsKey.BOOKING_TRANSACTION);
            Crashlytics.log("Transaction JSON is " + transactionJson);
            if (transactionJson == null)
            {
                return null;
            }
            mBookingTransaction = BookingTransaction.fromJson(transactionJson);
            if (mBookingTransaction != null)
            {
                mBookingTransaction.addObserver(this);
            }
            else
            {
                Crashlytics.log("Transaction object is null");
            }
            return mBookingTransaction;
        }
    }

    public void setCurrentTransaction(final BookingTransaction newTransaction)
    {
        if (mBookingTransaction != null)
        {
            mBookingTransaction.deleteObserver(this);
        }

        if (newTransaction == null)
        {
            Crashlytics.log("BookingManager: Setting current transaction to null!");
            mBookingTransaction = null;
            mPrefsManager.removeValue(PrefsKey.BOOKING_TRANSACTION);
            return;
        }

        mBookingTransaction = newTransaction;
        mBookingTransaction.addObserver(this);
        mPrefsManager.setString(PrefsKey.BOOKING_TRANSACTION, mBookingTransaction.toJson());
    }

    public BookingPostInfo getCurrentPostInfo()
    {
        if (mBookingPostInfo != null)
        {
            return mBookingPostInfo;
        }
        else
        {
            if ((mBookingPostInfo = BookingPostInfo
                    .fromJson(mPrefsManager.getString(PrefsKey.BOOKING_POST))) != null)
            {
                mBookingPostInfo.addObserver(this);
            }
            return mBookingPostInfo;
        }
    }

    public void setCurrentPostInfo(final BookingPostInfo newInfo)
    {
        if (mBookingPostInfo != null)
        {
            mBookingPostInfo.deleteObserver(this);
        }

        if (newInfo == null)
        {
            mBookingPostInfo = null;
            mPrefsManager.removeValue(PrefsKey.BOOKING_POST);
            return;
        }

        mBookingPostInfo = newInfo;
        mBookingPostInfo.addObserver(this);
        mPrefsManager.setString(PrefsKey.BOOKING_POST, mBookingPostInfo.toJson());
    }

    public FinalizeBookingRequestPayload getCurrentFinalizeBookingPayload()
    {
        if (mFinalizeBookingRequestPayload != null)
        {
            return mFinalizeBookingRequestPayload;
        }
        else
        {
            mFinalizeBookingRequestPayload = FinalizeBookingRequestPayload.fromJson(
                    mPrefsManager.getString(PrefsKey.BOOKING_FINALIZE_PAYLOAD)
            );
            if (mFinalizeBookingRequestPayload != null)
            {
                mFinalizeBookingRequestPayload.addObserver(this);
            }
            return mFinalizeBookingRequestPayload;
        }
    }

    public void setCurrentFinalizeBookingRequestPayload(final FinalizeBookingRequestPayload payload)
    {
        if (mFinalizeBookingRequestPayload != null)
        {
            mFinalizeBookingRequestPayload.deleteObserver(this);
        }
        if (payload == null)
        {
            mFinalizeBookingRequestPayload = null;
            mPrefsManager.removeValue(PrefsKey.BOOKING_FINALIZE_PAYLOAD);
        }
        else
        {
            mFinalizeBookingRequestPayload = payload;
            mPrefsManager.setString(PrefsKey.BOOKING_FINALIZE_PAYLOAD, mFinalizeBookingRequestPayload.toJson());
        }
    }

    public void setPromoTabCoupon(final String code)
    {
        mPrefsManager.setString(PrefsKey.BOOKING_PROMO_TAB_COUPON, code);
    }

    @Nullable
    public String getPromoTabCoupon()
    {
        return mPrefsManager.getString(PrefsKey.BOOKING_PROMO_TAB_COUPON);
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

        if (observable instanceof FinalizeBookingRequestPayload)
        {
            setCurrentFinalizeBookingRequestPayload((FinalizeBookingRequestPayload) observable);
        }
    }

    public void clear()
    {
        setCurrentRequest(null);
        setCurrentQuote(null);
        setCurrentTransaction(null);
        setCurrentPostInfo(null);
        setCurrentFinalizeBookingRequestPayload(null);
        mPrefsManager.removeValue(PrefsKey.STATE_BOOKING_CLEANING_EXTRAS_SELECTION);
        mBus.post(new BookingFlowClearedEvent());
    }

    public void clearAll()
    {
        mPrefsManager.removeValue(PrefsKey.BOOKING_PROMO_TAB_COUPON);
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
            final BookingEvent.RequestSendCancelRecurringBookingEmail event
    )
    {
        mDataManager.sendCancelRecurringBookingEmail(event.bookingRecurringId, new DataManager
                .Callback<SuccessWrapper>()
        {
            @Override
            public void onSuccess(SuccessWrapper response)
            {
                mBus.post(new BookingEvent.ReceiveSendCancelRecurringBookingEmailSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceiveSendCancelRecurringBookingEmailError(error));

            }
        });
    }


    /**
     * TODO: no endpoint to only return the recurring bookings, must fetch part of the user
     * bookings payload for now
     * TODO: would be nice to have caching
     *
     * @param event
     */
    @Subscribe
    public final void onRequestRecurringBookings(
            final BookingEvent.RequestRecurringBookings event
    )
    {
        mDataManager.getRecurringBookings(new DataManager.Callback<RecurringBookingsResponse>()
        {
            @Override
            public void onSuccess(final RecurringBookingsResponse response)
            {
                mBus.post(new BookingEvent.ReceiveRecurringBookingsSuccess(
                                response.getRecurringBookings())
                );
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.ReceiveRecurringBookingsError(error));
            }
        });
    }

    @Subscribe
    public final void onFinalizeBooking(
            final BookingEvent.RequestFinalizeBooking event
    )
    {
        mDataManager.finalizeBooking(
                event.getBookingId(),
                event.getPayload(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEvent.FinalizeBookingSuccess());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.FinalizeBookingError());
                    }
                });
    }

    @Subscribe
    public final void onUpdatePreferences(
            final BookingEditEvent.RequestEditPreferences event
    )
    {
        mDataManager.updatePreferences(
                event.getBookingId(),
                event.getPayload(),
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditPreferencesSuccess());
                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditPreferencesError());
                    }
                });
    }
}
