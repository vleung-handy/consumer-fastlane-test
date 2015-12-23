package com.handybook.handybook.booking.bookingedit.manager;

import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditExtrasViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditFrequencyViewModel;
import com.handybook.handybook.booking.bookingedit.viewmodel.BookingEditHoursViewModel;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;
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
    public void onRequestEditBookingAddress(HandyEvent.RequestEditBookingAddress event)
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
                        mBus.post(new HandyEvent.ReceiveEditBookingAddressSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveEditBookingAddressError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditHoursInfoViewModel(HandyEvent.RequestEditHoursInfoViewModel event)
    {
        mDataManager.getEditHoursInfo(event.bookingId,
                new DataManager.Callback<BookingEditHoursInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditHoursInfoResponse response)
                    {
                        BookingEditHoursViewModel bookingEditHoursViewModel =
                                BookingEditHoursViewModel.from(response);
                        mBus.post(new HandyEvent.ReceiveEditHoursInfoViewModelSuccess(
                                bookingEditHoursViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveEditHoursInfoViewModelError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingNoteToPro(HandyEvent.RequestUpdateBookingNoteToPro event)
    {
        mDataManager.updateBookingNoteToPro(event.bookingId, event.descriptionTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdateBookingNoteToProSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdateBookingNoteToProError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingEntryInformation(
            HandyEvent.RequestUpdateBookingEntryInformation event)
    {
        mDataManager.updateBookingEntryInformation(event.bookingId, event.entryInformationTransaction,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(final Void response)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdateBookingEntryInformationSuccess());
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveUpdateBookingEntryInformationError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestUpdateBookingFrequency(HandyEvent.RequestEditBookingFrequency event)
    {
        mDataManager.updateBookingFrequency(event.bookingId, event.bookingEditFrequencyRequest,
                new DataManager.Callback<Void>()
                {
                    @Override
                    public void onSuccess(Void response)
                    {
                        mBus.post(new HandyEvent.ReceiveEditBookingFrequencySuccess());

                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveEditBookingFrequencyError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditFrequencyViewModel(HandyEvent.RequestGetEditFrequencyViewModel event)
    {
        mDataManager.getBookingPricesForFrequencies(event.bookingId,
                new DataManager.Callback<BookingEditFrequencyInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditFrequencyInfoResponse response)
                    {
                        BookingEditFrequencyViewModel bookingEditFrequencyViewModel =
                                BookingEditFrequencyViewModel.from(response);
                        mBus.post(new HandyEvent.ReceiveGetEditFrequencyViewModelSuccess(
                                bookingEditFrequencyViewModel));

                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveGetEditFrequencyViewModelError(error));
                    }
                });
    }

    @Subscribe
    public void onRequestEditBookingHours(final HandyEvent.RequestEditHours event)
    {
        mDataManager.editBookingHours(
                event.bookingId,
                event.bookingEditHoursRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        mBus.post(new HandyEvent.ReceiveEditHoursSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveEditHoursError(error));

                    }
                });
    }

    @Subscribe
    public void onRequestEditBookingExtras(final HandyEvent.RequestEditBookingExtras event)
    {
        mDataManager.editBookingExtras(
                event.bookingId,
                event.bookingEditExtrasRequest,
                new DataManager.Callback<SuccessWrapper>()
                {
                    @Override
                    public void onSuccess(SuccessWrapper response)
                    {
                        mBus.post(new HandyEvent.ReceiveEditExtrasSuccess(response));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveEditExtrasError(error));

                    }
                });
    }
    
    @Subscribe
    public void onRequestEditBookingExtrasViewModel(
            final HandyEvent.RequestEditBookingExtrasViewModel event)
    {
        mDataManager.getEditBookingExtrasInfo(event.bookingId,
                new DataManager.Callback<BookingEditExtrasInfoResponse>()
                {
                    @Override
                    public void onSuccess(BookingEditExtrasInfoResponse response)
                    {
                        BookingEditExtrasViewModel editBookingExtrasViewModel =
                                BookingEditExtrasViewModel.from(response);
                        mBus.post(new HandyEvent.ReceiveEditBookingExtrasViewModelSuccess(
                                editBookingExtrasViewModel));
                    }

                    @Override
                    public void onError(DataManager.DataManagerError error)
                    {
                        mBus.post(new HandyEvent.ReceiveEditBookingExtrasViewModelError(error));

                    }
                });
    }
}
