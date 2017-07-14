package com.handybook.handybook.proteam.manager;

import android.support.annotation.NonNull;

import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.data.HandyRetrofitCallback;
import com.handybook.handybook.core.data.HandyRetrofitService;
import com.handybook.handybook.proteam.event.ProTeamEvent;
import com.handybook.handybook.proteam.model.BookingProTeam;
import com.handybook.handybook.proteam.model.ProTeam;
import com.handybook.handybook.proteam.model.ProTeamEditWrapper;
import com.handybook.handybook.proteam.model.ProTeamWrapper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import javax.inject.Inject;

public class ProTeamManager {

    private static final String DEFAULT_USER_ID = "0";
    private final EventBus mBus;
    private final HandyRetrofitService mService;
    private final DataManager mDataManager;
    private final UserManager mUserManager;

    /**
     * the last time that the user's pro team was edited on this client.
     * will serve as a hint for whether a new request for pro team should be made
     */
    private long mProTeamLastEditedTimestampMs = 0;

    @Inject
    public ProTeamManager(
            final EventBus bus,
            HandyRetrofitService service,
            DataManager dataManager,
            UserManager userManager
    ) {
        mBus = bus;
        mBus.register(this);
        mService = service;
        mDataManager = dataManager;
        mUserManager = userManager;
    }

    @Subscribe
    public void onRequestProTeam(final ProTeamEvent.RequestProTeam event) {
        final DataManager.Callback<ProTeamWrapper> cb = new DataManager.Callback<ProTeamWrapper>() {
            @Override
            public void onSuccess(final ProTeamWrapper proTeamWrapper) {
                mBus.post(new ProTeamEvent.ReceiveProTeamSuccess(
                        proTeamWrapper.getProTeam(),
                        proTeamWrapper.getProReferral(),
                        proTeamWrapper.getProTeamHelpCenterUrl()
                ));
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new ProTeamEvent.ReceiveProTeamError(error));
            }
        };
        requestProTeam(cb);
    }

    public void requestProTeam(final DataManager.Callback<ProTeamWrapper> cb) {
        mService.requestProTeam(
                getUserIdString(),
                new HandyRetrofitCallback(cb) {
                    @Override
                    protected void success(final JSONObject response) {
                        cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
                    }
                }
        );
    }

    private String getUserIdString() {
        final User currentUser = mUserManager.getCurrentUser();
        if (currentUser == null) {
            return DEFAULT_USER_ID;
        }
        final String id = currentUser.getId();
        return id == null ? DEFAULT_USER_ID : id;
    }

    /**
     * @return true if this given pro team response's timestamp is before a
     * pro team edit was made on this app instance
     */
    public boolean isProTeamResponseDefinitelyOutdated(long proTeamResponseTimestampMs) {
        return mProTeamLastEditedTimestampMs > proTeamResponseTimestampMs;
    }

    public void editProTeam(
            @NonNull final String userId,
            @NonNull final ProTeamEditWrapper proTeamEditWrapper,
            @NonNull final DataManager.Callback<ProTeamWrapper> cb
    ) {
        mService.editProTeam(
                userId,
                proTeamEditWrapper,
                new HandyRetrofitCallback(cb) {
                    @Override
                    protected void success(final JSONObject response) {
                        mProTeamLastEditedTimestampMs = System.currentTimeMillis();
                        cb.onSuccess(ProTeamWrapper.fromJson(response.toString()));
                    }
                }
        );
    }

    //todo later: make those who use this method use editProTeam() directly instead
    @Subscribe
    public void onRequestEditProTeam(final ProTeamEvent.RequestProTeamEdit event) {
        final DataManager.Callback<ProTeamWrapper> cb = new DataManager.Callback<ProTeamWrapper>() {
            @Override
            public void onSuccess(final ProTeamWrapper response) {
                mBus.post(new ProTeamEvent.ReceiveProTeamEditSuccess(response.getProTeam()));
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new ProTeamEvent.ReceiveProTeamEditError(error));
            }
        };
        editProTeam(getUserIdString(), event.getProTeamEditWrapper(), cb);
    }

    public void requestBookingProTeam(
            @NonNull final String bookingId,
            final DataManager.Callback<ProTeam.ProTeamCategory> finalCallback
    ) {

        final DataManager.Callback<ProTeamWrapper> cb = new DataManager.Callback<ProTeamWrapper>() {
            @Override
            public void onSuccess(final ProTeamWrapper proTeamWrapper) {
                requestBookingProTeam(proTeamWrapper.getProTeam(), bookingId, finalCallback);
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new ProTeamEvent.ReceiveBookingProTeamError(error));
            }
        };
        mDataManager.requestProTeam(getUserIdString(), cb);
    }

    private void requestBookingProTeam(
            @NonNull final ProTeam proTeam,
            @NonNull String bookingId,
            final DataManager.Callback<ProTeam.ProTeamCategory> finalCallback
    ) {
        final DataManager.Callback<BookingProTeam> cb = new DataManager.Callback<BookingProTeam>() {
            @Override
            public void onSuccess(final BookingProTeam response) {
                // We filter the existing pro team based on the returning result
                ProTeam.ProTeamCategory proTeamCategory = proTeam.getAllCategories();
                proTeamCategory.filterFavorPros(response.getProTeamPros());
                finalCallback.onSuccess(proTeamCategory);
                mBus.post(new ProTeamEvent.ReceiveBookingProTeamSuccess(proTeamCategory));
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new ProTeamEvent.ReceiveBookingProTeamError(error));
            }
        };
        mDataManager.requestProTeamViaBooking(bookingId, cb);
    }
}
