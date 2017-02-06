package com.handybook.handybook.booking.manager;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCancellationData;
import com.handybook.handybook.booking.model.BookingOption;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.BookingFlowClearedEvent;
import com.handybook.handybook.core.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.core.event.UserLoggedInEvent;
import com.handybook.handybook.core.manager.SecurePreferencesManager;
import com.handybook.handybook.proteam.model.ProTeam;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

//TODO: Add caching like we do for portal, navigating back and forth from my bookings page is painfully slow right now
public class BookingManager implements Observer
{
    private final SecurePreferencesManager mSecurePreferencesManager;
    private final DataManager mDataManager;
    private final Bus mBus;
    private BookingRequest mBookingRequest;
    private BookingQuote mBookingQuote;
    private BookingTransaction mBookingTransaction;
    private BookingPostInfo mBookingPostInfo;
    private FinalizeBookingRequestPayload mFinalizeBookingRequestPayload;
    private ProTeam mCurrentProTeam;

    @Inject
    public BookingManager(
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager,
            final DataManager dataManager
    )
    {
        mSecurePreferencesManager = securePreferencesManager;
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
    public void onRequestPrerateProInfo(BookingEvent.RequestPrerateProInfo event)
    {
        mDataManager.requestPrerateProInfo(
                event.bookingId,
                new DataManager.Callback<PrerateProInfo>()
                {
                    @Override
                    public void onSuccess(PrerateProInfo object)
                    {
                        mBus.post(new BookingEvent.ReceivePrerateProInfoSuccess(object));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceivePrerateProInfoError(error));
                    }
                }
        );
    }

    @Subscribe
    public void onPostLowRatingFeedback(BookingEvent.PostLowRatingFeedback event)
    {
        mDataManager.postLowRatingFeedback(event.mFeedback, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
                mBus.post(new BookingEvent.PostLowRatingFeedbackSuccess());
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                mBus.post(new BookingEvent.PostLowRatingFeedbackError(error));
            }
        });
    }

    @Subscribe
    public void onRequestPreCancelationInfo(BookingEvent.RequestBookingCancellationData event)
    {
        mDataManager.getBookingCancellationData(
                event.bookingId,
                new DataManager.Callback<BookingCancellationData>()
                {
                    @Override
                    public void onSuccess(final BookingCancellationData result)
                    {
                        mBus.post(new BookingEvent.ReceiveBookingCancellationDataSuccess(result));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveBookingCancellationDataError(error));
                    }
                }
        );
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
                                                    mBus.post(new BookingEditEvent.ReceiveUpdateBookingNoteToProError(
                                                            error));
                                                }
                                            }
        );
    }

    @Subscribe
    public void onRequestBookings(final BookingEvent.RequestBookings event)
    {
        if (null != event.getOnlyBookingsValue())
        {
            mDataManager.getBookings(
                    null,
                    event.getOnlyBookingsValue(),
                    new DataManager.Callback<UserBookingsWrapper>()
                    {
                        @Override
                        public void onSuccess(final UserBookingsWrapper result)
                        {
                            mBus.post(new BookingEvent.ReceiveBookingsSuccess(
                                    result,
                                    event.getOnlyBookingsValue()
                            ));
                        }

                        @Override
                        public void onError(DataManager.DataManagerError error)
                        {
                            mBus.post(new BookingEvent.ReceiveBookingsError(error));
                        }
                    }
            );
        }
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
                event.getProviderMatchPreference(),
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
            String json = mSecurePreferencesManager.getString(PrefsKey.BOOKING_REQUEST);
            if (TextUtils.isEmpty(json))
            {
                Crashlytics.log("getCurrentRequest: booking request JSON is :" + json);
            }
            if ((mBookingRequest = BookingRequest.fromJson(json)) != null)
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
            Crashlytics.log("setCurrentRequest: setting booking request = null");
            mBookingRequest = null;
            mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_REQUEST);
            return;
        }

        mBookingRequest = newRequest;
        mBookingRequest.addObserver(this);

        mSecurePreferencesManager.setString(PrefsKey.BOOKING_REQUEST, mBookingRequest.toJson());
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
                    .fromJson(mSecurePreferencesManager.getString(PrefsKey.BOOKING_QUOTE))) != null)
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
            mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_QUOTE);
            return;
        }

        mBookingQuote = newQuote;
        mBookingQuote.addObserver(this);
        mSecurePreferencesManager.setString(PrefsKey.BOOKING_QUOTE, mBookingQuote.toJson());
    }

    public BookingTransaction getCurrentTransaction()
    {
        if (mBookingTransaction != null)
        {
            return mBookingTransaction;
        }
        else
        {
            final String transactionJson = mSecurePreferencesManager.getString(PrefsKey.BOOKING_TRANSACTION);
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
            mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_TRANSACTION);
            return;
        }

        mBookingTransaction = newTransaction;
        mBookingTransaction.addObserver(this);
        mSecurePreferencesManager.setString(
                PrefsKey.BOOKING_TRANSACTION,
                mBookingTransaction.toJson()
        );
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
                    .fromJson(mSecurePreferencesManager.getString(PrefsKey.BOOKING_POST))) != null)
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
            mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_POST);
            return;
        }

        mBookingPostInfo = newInfo;
        mBookingPostInfo.addObserver(this);
        mSecurePreferencesManager.setString(PrefsKey.BOOKING_POST, mBookingPostInfo.toJson());
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
                    mSecurePreferencesManager.getString(PrefsKey.BOOKING_FINALIZE_PAYLOAD)
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
            mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_FINALIZE_PAYLOAD);
        }
        else
        {
            mFinalizeBookingRequestPayload = payload;
            mSecurePreferencesManager.setString(
                    PrefsKey.BOOKING_FINALIZE_PAYLOAD,
                    mFinalizeBookingRequestPayload.toJson()
            );
        }
    }

    /**
     * @param transaction
     * @return Will return the extra hours based off the bookingtransaction and the corresponding
     * bookingQuote. If no extra hours will return 0
     */
    public float getExtraHours(BookingTransaction transaction)
    {
        if (transaction == null || mBookingQuote == null || TextUtils.isEmpty(transaction.getExtraCleaningText()) || mBookingQuote
                .getBookingOption() == null)
        { return 0; }

        float extraHours = 0;
        String bookingExtras = transaction.getExtraCleaningText();
        BookingOption bookingOption = mBookingQuote.getBookingOption();
        String[] options = bookingOption.getOptions();
        float[] optionsHours = bookingOption.getHoursInfo();

        for (int i = 0; i < options.length; i++)
        {
            String option = options[i];
            if (bookingExtras.contains(option))
            {
                extraHours += optionsHours[i];
            }
        }

        return extraHours;
    }

    public void setPromoTabCoupon(final String code)
    {
        mSecurePreferencesManager.setString(PrefsKey.BOOKING_PROMO_TAB_COUPON, code);
    }

    @Nullable
    public String getPromoTabCoupon()
    {
        return mSecurePreferencesManager.getString(PrefsKey.BOOKING_PROMO_TAB_COUPON);
    }

    @Nullable
    public ProTeam getCurrentProTeam()
    {
        if (mCurrentProTeam == null)
        {
            mCurrentProTeam = ProTeam.fromJson(mSecurePreferencesManager.getString(PrefsKey.BOOKING_PRO_TEAM));
        }
        return mCurrentProTeam;
    }

    public void setCurrentProTeam(@Nullable final ProTeam proTeam)
    {
        if (proTeam == null)
        {
            mCurrentProTeam = null;
            mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_PRO_TEAM);
            return;
        }
        mSecurePreferencesManager.setString(PrefsKey.BOOKING_PRO_TEAM, ProTeam.toJson(proTeam));
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
        Crashlytics.log("clear: Clearing booking request, quote, transaction, and everything else");

        setCurrentRequest(null);
        setCurrentQuote(null);
        setCurrentTransaction(null);
        setCurrentPostInfo(null);
        setCurrentFinalizeBookingRequestPayload(null);
        setCurrentProTeam(null);
        mSecurePreferencesManager.removeValue(PrefsKey.STATE_BOOKING_CLEANING_EXTRAS_SELECTION);
        mBus.post(new BookingFlowClearedEvent());
    }

    public void clearAll()
    {
        mSecurePreferencesManager.removeValue(PrefsKey.BOOKING_PROMO_TAB_COUPON);
        clear();
    }

    @Subscribe
    public void environmentUpdated(final EnvironmentUpdatedEvent event)
    {
        if (!event.getEnvironment().equals(event.getPrevEnvironment()))
        {
            Crashlytics.logException(new RuntimeException(
                    "environmentUpdated: environmentUpdated from: " + event.getPrevEnvironment() + "  to:" + event
                            .getEnvironment()));
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
     * TODO: no endpoint to only return the recurring bookings, must fetch part of the user bookings
     * payload for now TODO: would be nice to have caching
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
                }
        );
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
                }
        );
    }
}
