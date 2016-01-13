package com.handybook.handybook.booking.bookingedit.manager;

import com.handybook.handybook.booking.BookingEvent;
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
    public void onRequestEditBookingAddress(BookingEvent.RequestEditBookingAddress event)
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
                        mBus.post(new BookingEvent.ReceiveEditBookingAddressSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveEditBookingAddressError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditHoursInfoViewModel(BookingEvent.RequestEditHoursInfoViewModel event)
    {
        mDataManager.getEditHoursInfo(event.bookingId,
                new DataManager.Callback<BookingEditHoursInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditHoursInfoResponse response)
                    {
                        BookingEditHoursViewModel bookingEditHoursViewModel =
                                BookingEditHoursViewModel.from(response);
                        mBus.post(new BookingEvent.ReceiveEditHoursInfoViewModelSuccess(
                                bookingEditHoursViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveEditHoursInfoViewModelError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingNoteToPro(BookingEvent.RequestUpdateBookingNoteToPro event)
    {
        mDataManager.updateBookingNoteToPro(event.bookingId, event.descriptionTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEvent.ReceiveUpdateBookingNoteToProSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveUpdateBookingNoteToProError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingEntryInformation(
            BookingEvent.RequestUpdateBookingEntryInformation event)
    {
        mDataManager.updateBookingEntryInformation(event.bookingId, event.entryInformationTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new BookingEvent.ReceiveUpdateBookingEntryInformationSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveUpdateBookingEntryInformationError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingFrequency(BookingEvent.RequestEditBookingFrequency event)
    {
        mDataManager.updateBookingFrequency(event.bookingId, event.bookingEditFrequencyRequest,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        mBus.post(new BookingEvent.ReceiveEditBookingFrequencySuccess());

                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveEditBookingFrequencyError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditFrequencyViewModel(BookingEvent.RequestGetEditFrequencyViewModel event)
    {
        mDataManager.getBookingPricesForFrequencies(event.bookingId,
                new DataManager.Callback<BookingEditFrequencyInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditFrequencyInfoResponse response)
                    {
                        BookingEditFrequencyViewModel bookingEditFrequencyViewModel =
                                BookingEditFrequencyViewModel.from(response);
                        mBus.post(new BookingEvent.ReceiveGetEditFrequencyViewModelSuccess(
                                bookingEditFrequencyViewModel));

                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveGetEditFrequencyViewModelError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditBookingHours(final BookingEvent.RequestEditHours event)
    {
        mDataManager.editBookingHours(
                event.bookingId,
                event.bookingEditHoursRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        mBus.post(new BookingEvent.ReceiveEditHoursSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveEditHoursError(error));

                    }
                });
    }

    @Subscribe
    public void onRequestEditBookingExtras(final BookingEvent.RequestEditBookingExtras event)
    {
        mDataManager.editBookingExtras(
                event.bookingId,
                event.bookingEditExtrasRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        mBus.post(new BookingEvent.ReceiveEditExtrasSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveEditExtrasError(error));

                    }
                });
    }
    
    @Subscribe
    public void onRequestEditBookingExtrasViewModel(
            final BookingEvent.RequestEditBookingExtrasViewModel event)
    {
        mDataManager.getEditBookingExtrasInfo(event.bookingId,
                new DataManager.Callback<BookingEditExtrasInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditExtrasInfoResponse response)
                    {
                        BookingEditExtrasViewModel editBookingExtrasViewModel =
                                BookingEditExtrasViewModel.from(response);
                        mBus.post(new BookingEvent.ReceiveEditBookingExtrasViewModelSuccess(
                                editBookingExtrasViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new BookingEvent.ReceiveEditBookingExtrasViewModelError(error));

                    }
                });
    }
}
