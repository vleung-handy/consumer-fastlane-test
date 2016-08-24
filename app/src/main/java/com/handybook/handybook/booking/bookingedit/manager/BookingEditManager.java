package com.handybook.handybook.booking.bookingedit.manager;

import com.handybook.handybook.booking.bookingedit.BookingEditEvent;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditHoursViewModel;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.data.DataManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class BookingEditManager
{
    private final DataManager mDataManager;
    private final Bus mBus;

    @Inject
    public BookingEditManager(final Bus bus, final DataManager dataManager)
    {
        mDataManager = dataManager;
        mBus = bus;
        mBus.register(this);
    }

    @Subscribe
    public void onRequestEditBookingAddress(BookingEditEvent.RequestEditBookingAddress event)
    {
        mDataManager.editBookingAddress(event.bookingId,
                event.bookingEditAddressRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        //the response is useless because server only returns success:true
                        //or error:true. in the latter case, retrofit callback will invoke onError()
                        //even if the http code is 200
                        mBus.post(new BookingEditEvent.ReceiveEditBookingAddressSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditBookingAddressError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditHoursInfoViewModel(BookingEditEvent.RequestEditHoursInfoViewModel event)
    {
        mDataManager.getEditHoursInfo(event.bookingId,
                new DataManager.Callback<BookingEditHoursInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditHoursInfoResponse response)
                    {
                        BookingEditHoursViewModel bookingEditHoursViewModel =
                                BookingEditHoursViewModel.from(response);
                        mBus.post(new BookingEditEvent.ReceiveEditHoursInfoViewModelSuccess(
                                bookingEditHoursViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditHoursInfoViewModelError(error));
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
    public void onRequestUpdateBookingEntryInformation(
            BookingEditEvent.RequestUpdateBookingEntryInformation event)
    {
        mDataManager.updateBookingEntryInformation(event.bookingId, event.entryInformationTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveUpdateBookingEntryInformationSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveUpdateBookingEntryInformationError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingFrequency(BookingEditEvent.RequestEditBookingFrequency event)
    {
        mDataManager.updateBookingFrequency(event.bookingId, event.bookingEditFrequencyRequest,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditBookingFrequencySuccess());

                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditBookingFrequencyError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateRecurringFrequency(BookingEditEvent.RequestUpdateRecurringFrequency event)
    {
        mDataManager.updateRecurringFrequency(event.recurringId, event.bookingEditFrequencyRequest,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveUpdateRecurringFrequencySuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveUpdateRecurringFrequencyError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditFrequencyViewModel(BookingEditEvent.RequestGetEditFrequencyViewModel event)
    {
        mDataManager.getBookingPricesForFrequencies(event.bookingId,
                new DataManager.Callback<BookingEditFrequencyInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditFrequencyInfoResponse response)
                    {
                        BookingEditFrequencyViewModel bookingEditFrequencyViewModel =
                                BookingEditFrequencyViewModel.from(response);
                        mBus.post(new BookingEditEvent.ReceiveGetEditFrequencyViewModelSuccess(
                                bookingEditFrequencyViewModel));

                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveGetEditFrequencyViewModelError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestRecurringFrequencyViewModel(BookingEditEvent.RequestRecurringFrequencyViewModel event)
    {
        mDataManager.getRecurringFrequency(event.recurringId,
                new DataManager.Callback<BookingEditFrequencyInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditFrequencyInfoResponse response)
                    {
                        BookingEditFrequencyViewModel bookingEditFrequencyViewModel =
                                BookingEditFrequencyViewModel.from(response);
                        mBus.post(new BookingEditEvent.ReceiveGetEditFrequencyViewModelSuccess(
                                bookingEditFrequencyViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveGetEditFrequencyViewModelError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditBookingHours(final BookingEditEvent.RequestEditHours event)
    {
        mDataManager.editBookingHours(
                event.bookingId,
                event.bookingEditHoursRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditHoursSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditHoursError(error));

                    }
                });
    }

    @Subscribe
    public void onRequestEditBookingExtras(final BookingEditEvent.RequestEditBookingExtras event)
    {
        mDataManager.editBookingExtras(
                event.bookingId,
                event.bookingEditExtrasRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditExtrasSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditExtrasError(error));

                    }
                });
    }
    
    @Subscribe
    public void onRequestEditBookingExtrasViewModel(
            final BookingEditEvent.RequestEditBookingExtrasViewModel event)
    {
        mDataManager.getEditBookingExtrasInfo(event.bookingId,
                new DataManager.Callback<BookingEditExtrasInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditExtrasInfoResponse response)
                    {
                        BookingEditExtrasViewModel editBookingExtrasViewModel =
                                BookingEditExtrasViewModel.from(response);
                        mBus.post(new BookingEditEvent.ReceiveEditBookingExtrasViewModelSuccess(
                                editBookingExtrasViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEditEvent.ReceiveEditBookingExtrasViewModelError(error));

                    }
                });
    }
}
