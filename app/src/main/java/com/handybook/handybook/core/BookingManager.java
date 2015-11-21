package com.handybook.handybook.core;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.BookingFlowClearedEvent;
import com.handybook.handybook.event.EnvironmentUpdatedEvent;
import com.handybook.handybook.event.HandyEvent;
import com.handybook.handybook.event.UserLoggedInEvent;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.model.response.BookingEditExtrasInfoResponse;
import com.handybook.handybook.model.response.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.model.response.BookingEditHoursInfoResponse;
import com.handybook.handybook.viewmodel.BookingCardViewModel;
import com.handybook.handybook.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.viewmodel.BookingEditHoursViewModel;
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
    BookingManager(final Bus bus, final PrefsManager prefsManager, final DataManager dataManager)
    {
        this.prefsManager = prefsManager;
        this.dataManager = dataManager;
        this.bus = bus;
        this.bus.register(this);
    }

    // Event listening + sending, half way to updating our managers to work like nortal's managers
    // and provide a layer for data access

    @Subscribe
    public void onRequestEditHoursInfoViewModel(HandyEvent.RequestEditHoursInfoViewModel event)
    {
        dataManager.getEditHoursInfo(event.bookingId,
                new DataManager.Callback<BookingEditHoursInfoResponse>()
        {
            @Override
            public void onSuccess(BookingEditHoursInfoResponse response)
            {
                BookingEditHoursViewModel bookingEditHoursViewModel =
                        BookingEditHoursViewModel.from(response);
                bus.post(new HandyEvent.ReceiveEditHoursInfoViewModelSuccess(
                        bookingEditHoursViewModel));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveEditHoursInfoViewModelError(error));
            }
        });
    }

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
        dataManager.getPreCancelationInfo(event.bookingId, new DataManager.Callback<Pair<String,
                List<String>>>()
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
        dataManager.updateBookingNoteToPro(event.bookingId, event.descriptionTransaction,
                new DataManager.Callback<Void>()
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
    public void onRequestUpdateBookingEntryInformation(
            HandyEvent.RequestUpdateBookingEntryInformation event)
    {
        dataManager.updateBookingEntryInformation(event.bookingId, event.entryInformationTransaction,
                new DataManager.Callback<Void>()
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
    public void onRequestUpdateBookingFrequency(HandyEvent.RequestEditBookingFrequency event)
    {
        dataManager.updateBookingFrequency(event.bookingId, event.bookingEditFrequencyRequest,
                new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(Void response)
            {
                bus.post(new HandyEvent.ReceiveEditBookingFrequencySuccess());

            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveEditBookingFrequencyError(error));
            }
        });
    }

    @Subscribe
    public void onRequestEditFrequencyViewModel(HandyEvent.RequestGetEditFrequencyViewModel event)
    {
        dataManager.getBookingPricesForFrequencies(event.bookingId,
                new DataManager.Callback<BookingEditFrequencyInfoResponse>()
        {
            @Override
            public void onSuccess(BookingEditFrequencyInfoResponse response)
            {
                BookingEditFrequencyViewModel bookingEditFrequencyViewModel =
                        BookingEditFrequencyViewModel.from(response);
                bus.post(new HandyEvent.ReceiveGetEditFrequencyViewModelSuccess(
                        bookingEditFrequencyViewModel));

            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveGetEditFrequencyViewModelError(error));
            }
        });
    }

    @Subscribe
    public void onRequestBookings(HandyEvent.RequestBookingsForUser event)
    {
        dataManager.getBookings(event.user, new DataManager.Callback<UserBookingsWrapper>()
        {
            @Override
            public void onSuccess(final UserBookingsWrapper result)
            {
                bus.post(new HandyEvent.ReceiveBookingsSuccess(result.getBookings()));
            }

            @Override
            public void onError(DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveBookingsError(error));
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
                            BookingCardViewModel.List models = new BookingCardViewModel.List();
                            switch (event.getOnlyBookingValue())
                            {
                                case Booking.List.VALUE_ONLY_BOOKINGS_PAST:
                                    models = BookingCardViewModel.List
                                            .from(bookings, BookingCardViewModel.List.TYPE_PAST);
                                    break;
                                case Booking.List.VALUE_ONLY_BOOKINGS_UPCOMING:
                                    models = BookingCardViewModel.List
                                            .from(bookings, BookingCardViewModel.List.TYPE_UPCOMING);
                                    models.setType(BookingCardViewModel.List.TYPE_UPCOMING);
                                    break;
                                default:
                                    Crashlytics.log("event.getOnlyBookingValue() hit default :(");
                            }
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

    @Subscribe
    public void onRequestRateBooking(HandyEvent.RateBookingEvent event)
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
                        bus.post(new HandyEvent.ReceiveRateBookingSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ReceiveRateBookingError(error));
                    }
                }
        );
    }

    @Subscribe
    public void onRequestTipPro(HandyEvent.RequestTipPro event)
    {
        dataManager.tipPro(event.bookingId, event.tipAmount, new DataManager.Callback<Void>()
        {
            @Override
            public void onSuccess(final Void response)
            {
                bus.post(new HandyEvent.ReceiveTipProSuccess());
            }

            @Override
            public void onError(final DataManager.DataManagerError error)
            {
                bus.post(new HandyEvent.ReceiveTipProError());
            }
        });
    }

//Old Direct References, to eventually be handled in the events way

    public final BookingRequest getCurrentRequest()
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

    public final BookingQuote getCurrentQuote()
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

    public final void setCurrentQuote(final BookingQuote newQuote)
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
            prefsManager.removeValue(PrefsKey.BOOKING_TRANSACTION);
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
            prefsManager.removeValue(PrefsKey.BOOKING_POST);
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
    public final void environmentUpdated(final EnvironmentUpdatedEvent event)
    {
        if (!event.getEnvironment().equals(event.getPrevEnvironment()))
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

    @Subscribe
    public final void onRequestEditBookingHours(final HandyEvent.RequestEditHours event)
    {
        dataManager.editBookingHours(
                event.bookingId,
                event.bookingEditHoursRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        bus.post(new HandyEvent.ReceiveEditHoursSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ReceiveEditHoursError(error));

                    }
                });
    }

    @Subscribe
    public final void onRequestEditBookingExtras(final HandyEvent.RequestEditBookingExtras event)
    {
        dataManager.editBookingExtras(
                event.bookingId,
                event.bookingEditExtrasRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        bus.post(new HandyEvent.ReceiveEditExtrasSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ReceiveEditExtrasError(error));

                    }
                });
    }

    @Subscribe
    public final void onRequestEditBookingExtrasViewModel(
            final HandyEvent.RequestEditBookingExtrasViewModel event)
    {
        dataManager.getEditBookingExtrasInfo(event.bookingId,
                new DataManager.Callback<BookingEditExtrasInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditExtrasInfoResponse response)
                    {
                        BookingEditExtrasViewModel editBookingExtrasViewModel =
                                BookingEditExtrasViewModel.from(response);
                        bus.post(new HandyEvent.ReceiveEditBookingExtrasViewModelSuccess(
                                editBookingExtrasViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        bus.post(new HandyEvent.ReceiveEditBookingExtrasViewModelError(error));

                    }
                });
    }
}
