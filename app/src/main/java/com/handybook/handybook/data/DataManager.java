package com.handybook.handybook.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.account.model.RecurringPlanWrapper;
import com.handybook.handybook.booking.bookingedit.model.BookingEditEntryInformationRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.bookingedit.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.bookingedit.model.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.booking.bookingedit.model.EditAddressRequest;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCompleteTransaction;
import com.handybook.handybook.booking.model.BookingCoupon;
import com.handybook.handybook.booking.model.BookingGeoStatus;
import com.handybook.handybook.booking.model.BookingOptionsWrapper;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingProRequestResponse;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingRequestablePros;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.EntryMethodsInfo;
import com.handybook.handybook.booking.model.FinalizeBookingRequestPayload;
import com.handybook.handybook.booking.model.JobStatus;
import com.handybook.handybook.booking.model.LaundryDropInfo;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.RecurringBookingsResponse;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.booking.model.ZipValidationResponse;
import com.handybook.handybook.booking.rating.PrerateProInfo;
import com.handybook.handybook.booking.rating.RateImprovementFeedback;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BlockedWrapper;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.User;
import com.handybook.handybook.helpcenter.model.HelpNodeWrapper;
import com.handybook.handybook.logger.handylogger.model.EventLogResponse;
import com.handybook.handybook.manager.SecurePreferencesManager;
import com.handybook.handybook.model.request.CreateUserRequest;
import com.handybook.handybook.model.request.UpdateUserRequest;
import com.handybook.handybook.model.response.HelpCenterResponse;
import com.handybook.handybook.model.response.UserExistsResponse;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.module.notifications.feed.model.HandyNotification;
import com.handybook.handybook.module.notifications.splash.model.SplashPromo;
import com.handybook.handybook.module.proteam.model.ProviderMatchPreference;
import com.handybook.handybook.module.referral.model.RedemptionDetailsResponse;
import com.handybook.handybook.module.referral.model.ReferralResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import retrofit.mime.TypedInput;

public class DataManager
{
    private static final String TAG = DataManager.class.getName();

    private final HandyRetrofitService mService;
    private final HandyRetrofitEndpoint mEndpoint;
    private final SecurePreferencesManager mSecurePreferencesManager;

    @Inject
    public DataManager(
            final HandyRetrofitService service,
            final HandyRetrofitEndpoint endpoint,
            final SecurePreferencesManager securePreferencesManager
    )
    {
        mService = service;
        mEndpoint = endpoint;
        mSecurePreferencesManager = securePreferencesManager;
    }

    public String getBaseUrl()
    {
        return mEndpoint.getBaseUrl();
    }

    /**
     * If there is a cached version, return the cache, and updates the cache in the background. If
     * there was a cached version, then cache update success will not be broadcasted. This is to
     * prevent a client from having to deal with multiple broadcasts (cache, updates, etc).
     */
    public final void getServices(
            final CacheResponse<List<Service>> cache,
            final Callback<List<Service>> cb
    )
    {
        final List<Service> cachedServices = getCachedServices();

        if (cachedServices != null && cache != null)
        {
            //if there is a cached version, we notify right away.
            cache.onResponse(cachedServices);
        }

        final ArrayList<Service> servicesMenu = new ArrayList<>();
        final HashMap<String, Service> menuMap = new HashMap<>();

        mService.getServicesMenu(new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                final JSONArray array = response.optJSONArray("menu_structure");

                if (array == null && cachedServices == null)
                {
                    //we only notify of error if there is not already a cached version returned.
                    cb.onError(new DataManagerError(Type.SERVER));
                    return;
                }

                for (int i = 0; i <= array.length(); i++)
                {
                    final JSONObject obj = array.optJSONObject(i);

                    if (obj != null)
                    {
                        final String name = obj.isNull("name") ? null : obj.optString("name", null);
                        final int ignore = obj.optInt("ignore", 1);

                        if (name == null || ignore == 1)
                        {
                            continue;
                        }

                        final Service service = new Service();
                        service.setUniq(obj.isNull("uniq") ? null : obj.optString("uniq"));
                        service.setName(obj.isNull("name") ? null : obj.optString("name"));
                        service.setOrder(obj.optInt("order", 0));
                        servicesMenu.add(service);
                        menuMap.put(service.getUniq(), service);
                    }
                }

                final HashMap<Integer, ArrayList<Service>> servicesMap = new HashMap<>();

                mService.getServices(new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        final JSONArray array = response.optJSONArray("services_list");

                        if (array == null && cachedServices == null)
                        {
                            //we only notify of error if there is not already a cached version returned.
                            cb.onError(new DataManagerError(Type.SERVER));
                            return;
                        }

                        for (int i = 0; i <= array.length(); i++)
                        {
                            final JSONObject obj = array.optJSONObject(i);

                            if (obj != null && obj.optBoolean("no_show", true))
                            {
                                final Service service = new Service();
                                service.setId(obj.optInt("id"));

                                service.setUniq(obj.isNull("machine_name") ? null
                                                        : obj.optString("machine_name"));

                                service.setName(obj.isNull("name") ? null : obj.optString("name"));
                                service.setOrder(obj.optInt("order", 0));
                                service.setParentId(obj.optInt("parent", 0));

                                final Service menuService;
                                if ((menuService = menuMap.get(service.getUniq())) != null)
                                {
                                    menuService.setId(service.getId());
                                    continue;
                                }

                                ArrayList<Service> list;
                                if ((list = servicesMap.get(service.getParentId())) != null)
                                {
                                    list.add(service);
                                }
                                else
                                {
                                    list = new ArrayList<>();
                                    list.add(service);
                                    servicesMap.put(service.getParentId(), list);
                                }
                            }
                        }

                        for (final Service service : servicesMenu)
                        {
                            final List<Service> services;
                            if ((services = servicesMap.get(service.getId())) != null)
                            {
                                Collections.sort(services, new Comparator<Service>()
                                {
                                    @Override
                                    public int compare(final Service lhs, final Service rhs)
                                    {
                                        return lhs.getOrder() - rhs.getOrder();
                                    }
                                });
                                service.setServices(services);
                            }
                        }

                        Collections.sort(servicesMenu, new Comparator<Service>()
                        {
                            @Override
                            public int compare(final Service lhs, final Service rhs)
                            {
                                return lhs.getOrder() - rhs.getOrder();
                            }
                        });

                        //updates the cache with fresh version of services
                        mSecurePreferencesManager.setString(PrefsKey.CACHED_SERVICES, new Gson()
                                .toJsonTree(servicesMenu).getAsJsonArray().toString());

                        //we only notify of error if there is not already a cached version returned.
                        if (cachedServices == null)
                        {
                            cb.onSuccess(servicesMenu);
                        }
                    }
                });
            }
        });
    }

    @Nullable
    public List<Service> getCachedServices()
    {
        String cachedServicesJson = mSecurePreferencesManager.getString(PrefsKey.CACHED_SERVICES);
        List<Service> cachedServices = null;
        if (cachedServicesJson != null)
        {
            try
            {
                cachedServices = new Gson().fromJson(
                        cachedServicesJson,
                        new TypeToken<List<Service>>()
                        {
                        }.getType()
                );
            }
            catch (Exception e)
            {
                //if there is ever an error parsing this, fall out and let it create a new set
                Crashlytics.log(TAG + " error when deserializing JSON:" + cachedServicesJson);
                Crashlytics.logException(e);
            }

        }

        return cachedServices;
    }

    public void getAvailableSplashPromo(
            final String userId,
            final String[] displayedPromos,
            final String[] acceptedPromos,
            final Callback<SplashPromo> cb
    )
    {
        mService.getAvailableSplashPromo(userId, displayedPromos, acceptedPromos,
                                         new AvailableSplashPromoRetrofitCallback(cb)
        );
    }

    public void editBookingAddress(
            final int bookingId,
            final EditAddressRequest editAddressRequest,
            final Callback<SuccessWrapper> cb
    )
    {
        mService.editBookingAddress(
                bookingId,
                editAddressRequest,
                new SuccessHandyRetroFitCallback(cb)
        );
    }

    public void editBookingPlanAddress(
            final int planId,
            final EditAddressRequest editAddressRequest,
            final Callback<RecurringPlanWrapper> cb
    )
    {
        mService.editBookingPlanAddress(
                planId,
                editAddressRequest,
                new RecurringPlanHandyRetroFitCallback(cb)
        );
    }

    public void sendCancelRecurringBookingEmail(
            final int bookingRecurringId, final
    Callback<SuccessWrapper> cb
    )
    {
        mService.sendCancelRecurringBookingEmail(
                bookingRecurringId,
                "",
                new SuccessHandyRetroFitCallback(cb)
        );
    }

    public void getEditBookingExtrasInfo(
            final int bookingId,
            final Callback<BookingEditExtrasInfoResponse> cb
    )
    {
        mService.getEditExtrasInfo(bookingId, new EditExtrasInfoHandyRetroFitCallback(cb));
    }

    public void editBookingExtras(
            final int bookingId,
            final BookingEditExtrasRequest bookingEditExtrasRequest,
            final Callback<SuccessWrapper> cb
    )
    {
        mService.editServiceExtras(
                bookingId,
                bookingEditExtrasRequest,
                new SuccessHandyRetroFitCallback(cb)
        );
    }

    public void editBookingHours(
            final int bookingId,
            final BookingEditHoursRequest bookingEditHoursRequest,
            final Callback<SuccessWrapper> cb
    )
    {
        mService.editBookingHours(
                bookingId,
                bookingEditHoursRequest,
                new SuccessHandyRetroFitCallback(cb)
        );
    }

    public void getBlockedWrapper(
            final int versionCode,
            final CacheResponse<BlockedWrapper> blockedWrapperCacheResponse,
            final Callback<BlockedWrapper> blockedWrapperCallback
    )
    {
        mService.getBlockedWrapper(
                versionCode,
                new HandyRetrofitCallback(blockedWrapperCallback)
                {
                    @Override
                    protected void success(JSONObject response)
                    {
                        final BlockedWrapper blockedWrapper = BlockedWrapper
                                .fromJson(response.toString());
                        blockedWrapperCallback.onSuccess(blockedWrapper);
                    }
                }
        );
    }

    /**
     * Requests a HandyNotification.ResultSet from the server
     *
     * @param userId  id of the user to request notifications for
     * @param count   max size of the notification list (can be smaller)
     * @param sinceId <i>optional</i> Bottom delimiter, exclusive. Only notification after this id.
     * @param untilId <i>optional</i> Top delimiter, exclusive. Only get notification before this
     *                id
     * @param cb      the callback used for returning data
     * @see <a href="https://dev.twitter.com/rest/public/timelines">Twitter's implementation</a>
     * <p>
     */
    public void getNotifications(
            long userId,
            @Nullable final Long count,
            @Nullable final Long sinceId,
            @Nullable final Long untilId,
            @NonNull final Callback<HandyNotification.ResultSet> cb
    )
    {
        mService.getNotificationResultSet(
                userId,
                count,
                sinceId,
                untilId,
                new HandyNotificationResultSetHandyRetrofitCallback(cb)
        );
    }


    public void getQuoteOptions(
            final int serviceId, final String userId,
            final Callback<BookingOptionsWrapper> cb
    )
    {
        mService.getQuoteOptions(
                serviceId,
                userId,
                new BookingOptionsWrapperHandyRetroFitCallback(cb)
        );
    }

    public void createQuote(final BookingRequest bookingRequest, final Callback<BookingQuote> cb)
    {
        mService.createQuote(bookingRequest, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    public void updateQuoteDate(
            final int quoteId, final Date date,
            final Callback<BookingQuote> cb
    )
    {
        mService.updateQuoteDate(quoteId, date, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    public void applyPromo(
            final String promoCode, final int quoteId, final String userId,
            final String email, final Callback<BookingQuote> cb
    )
    {
        mService.applyPromo(promoCode, quoteId, userId, email,
                            new HandyRetrofitCallback(cb)
                            {
                                @Override
                                protected void success(final JSONObject response)
                                {
                                    cb.onSuccess(BookingQuote.fromJson(response.toString()));
                                }
                            }
        );
    }

    public void removePromo(final int quoteId, final Callback<BookingCoupon> cb)
    {
        mService.removePromo(quoteId, "", new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(BookingCoupon.fromJson(response.toString()));
            }
        });
    }

    public void createBooking(
            final BookingTransaction bookingTransaction,
            final Callback<BookingCompleteTransaction> cb
    )
    {
        mService.createBooking(
                bookingTransaction.getBookingId(),
                bookingTransaction,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(BookingCompleteTransaction.fromJson(response.toString()));
                    }
                }
        );
    }

    public void validateBookingZip(
            final int serviceId, final String zipCode, final String userId,
            final String promoCode, final Callback<ZipValidationResponse> cb
    )
    {
        mService.validateBookingZip(serviceId, zipCode, userId, promoCode,
                                    new ZipValidationRetroFitCallback(cb)
        );
    }

    public void getBookingMilestones(final String bookingId, final Callback<JobStatus> cb)
    {
        mService.getBookingMilestones(bookingId, new BookingMilestonesCallback(cb));
    }

    public final void getBookings(
            final User user,
            final Callback<UserBookingsWrapper> cb
    )
    {
        mService.getBookings(
                null,
                new UserBookingsWrapperHandyRetroFitCallback(cb)
        );
    }

    public final void getBookings(
            final User user,
            @NonNull @Booking.List.OnlyBookingValues final String onlyBookingValue,
            final Callback<UserBookingsWrapper> cb
    )
    {
        mService.getBookings(
                onlyBookingValue,
                new UserBookingsWrapperHandyRetroFitCallback(cb)
        );
    }

    public final void getBookingsForReschedule(
            final String providerId,
            @NonNull @Booking.List.OnlyBookingValues final String onlyBookingValue,
            final Callback<UserBookingsWrapper> cb
    )
    {
        mService.getBookingsForReschedule(
                onlyBookingValue,
                providerId,
                new UserBookingsWrapperHandyRetroFitCallback(cb)
        );
    }

    public final void getEntryMethodsInfo(
            final String bookingId,
            final Callback<EntryMethodsInfo> cb
    )
    {
        mService.getEntryMethodsInfo(
                bookingId,
                new EntryMethodsInfoHandyRetroFitCallback(cb)
        );
    }

    public final void getEditHoursInfo(
            final int bookingId,
            final Callback<BookingEditHoursInfoResponse> cb
    )
    {
        mService.getEditHoursInfo(
                bookingId,
                new EditHoursInfoHandyRetroFitCallback(cb)
        );
    }

    public void getBooking(final String bookingId, final Callback<Booking> cb)
    {
        mService.getBooking(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(Booking.fromJson(response.optJSONObject("booking").toString()));
            }
        });
    }

    public void getPreBookingPromo(final String promoCode, final Callback<PromoCode> cb)
    {
        mService.getPreBookingPromo(promoCode, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                if (response.has("coupon") && response.optInt("coupon") == 1)
                {
                    cb.onSuccess(new PromoCode(PromoCode.Type.COUPON, promoCode));
                }
                else if (response.has("voucher"))
                {
                    final JSONObject voucher = response.optJSONObject("voucher");

                    final PromoCode code = new PromoCode(
                            PromoCode.Type.VOUCHER,
                            voucher.optString("code")
                    );

                    code.setServiceId(voucher.optInt("service_id"));

                    //TODO remove this harcoding below after update to API response
                    code.setUniq("home_cleaning");
                    //code.setUniq(voucher.optString("machine_name"));

                    cb.onSuccess(code);
                }
                else
                {
                    cb.onError(new DataManagerError(Type.SERVER));
                }
            }
        });
    }

    public void requestPrerateProInfo(
            final String bookingId,
            final Callback<PrerateProInfo> cb
    )
    {
        mService.requestPrerateProInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(PrerateProInfo.fromJson(response.toString()));
            }
        });
    }

    public void postLowRatingFeedback(
            RateImprovementFeedback feedback,
            final Callback<Void> cb
    )
    {
        mService.postLowRatingFeedback(
                feedback.getBookingId(),
                feedback,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    public void addBookingPostInfo(
            final int bookingId, final BookingPostInfo postInfo,
            final Callback<Void> cb
    )
    {
        mService.addBookingPostInfo(bookingId, postInfo, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public void getPreRescheduleInfo(final String bookingId, final Callback<String> cb)
    {
        mService.getPreRescheduleInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(response.isNull("notice") ? null : response.optString("notice", null));
            }
        });
    }

    public void rescheduleBooking(
            final String bookingId,
            final String date,
            boolean rescheduleAll,
            final String userId,
            final String providerId,
            final Callback<Pair<String, BookingQuote>> cb
    )
    {

        //logic dictates, if there is a provider id, that means we're rescheduling from chat, and therefore
        //rescheduleAll = false, and chatRescheduleAgreement = true;
        boolean chatRescheduleAgreement = false;
        if (!TextUtils.isEmpty(providerId))
        {
            chatRescheduleAgreement = true;
            rescheduleAll = false;
        }

        mService.rescheduleBooking(
                bookingId,
                date,
                rescheduleAll ? 1 : 0,
                userId,
                providerId,
                chatRescheduleAgreement ? 1 : 0,
                                   new HandyRetrofitCallback(cb)
                                   {
                                       @Override
                                       protected void success(final JSONObject response)
                                       {
                                           final String message = parseAlertMessage(response);
                                           BookingQuote quote = null;

                                           if (response.optJSONArray("dynamic_options") != null)
                                           {
                                               quote = BookingQuote.fromJson(response.toString());
                                           }

                                           cb.onSuccess(new Pair<>(message, quote));
                                       }
                                   }
        );
    }

    public void getPreCancelationInfo(
            final String bookingId,
            final Callback<Pair<String, List<String>>> cb
    )
    {
        mService.getPreCancelationInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                final String notice = response.isNull("notice") ? null
                        : response.optString("notice", null);

                final JSONArray array = response.optJSONArray("options");
                final Gson gson = new Gson();
                final List<String> options = gson.fromJson(
                        array.toString(),
                        new TypeToken<List<String>>()
                        {
                        }.getType()
                );

                cb.onSuccess(new Pair<>(notice, options));
            }
        });
    }

    public void cancelBooking(
            final String bookingId, final int reasonCode, final String userId,
            final Callback<String> cb
    )
    {
        final HandyRetrofitCallback callback = new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                final String message = parseAlertMessage(response);
                cb.onSuccess(message);
            }
        };

        if (reasonCode >= 0)
        {
            mService.cancelBooking(bookingId, reasonCode, userId, callback);
        }
        else
        {
            mService.cancelBooking(bookingId, userId, callback);
        }
    }

    public void getLaundryScheduleInfo(
            final int bookingId,
            final Callback<LaundryDropInfo> cb
    )
    {
        mService.getLaundryScheduleInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(LaundryDropInfo.fromJson(response.toString()));
            }
        });
    }

    public void setLaundryDropOff(
            final int bookingId, final String date,
            final int hour, final int minute, final String type,
            final Callback<Void> cb
    )
    {
        mService.setLaundryDropOff(bookingId, date, hour, minute, type,
                                   new HandyRetrofitCallback(cb)
                                   {
                                       @Override
                                       protected void success(final JSONObject response)
                                       {
                                           cb.onSuccess(null);
                                       }
                                   }
        );
    }

    public void getAddLaundryInfo(
            final int bookingId,
            final Callback<Booking> cb
    )
    {
        mService.getAddLaundryInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                final Booking booking = Booking.fromJson(response.optJSONObject("booking")
                                                                 .toString());
                booking.setId(Integer.toString(bookingId));
                cb.onSuccess(booking);
            }
        });
    }

    public void addLaundry(final int bookingId, final Callback<Void> cb)
    {
        mService.addLaundry(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public void ratePro(
            final int bookingId,
            final int rating,
            final Integer tipAmount,
            final ProviderMatchPreference providerMatchPreference,
            final Callback<Void> cb
    )
    {
        mService.ratePro(
                bookingId,
                rating,
                tipAmount,
                ProviderMatchPreference.asString(providerMatchPreference),
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    public void tipPro(final int bookingId, final Integer tipAmount, final Callback<Void> cb)
    {
        mService.tipPro(bookingId, tipAmount, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public void submitProRatingDetails(
            final int bookingId, final String positiveFeedback,
            final Callback<Void> cb
    )
    {
        mService.submitProRatingDetails(
                bookingId,
                new HandyRetrofitService.RateProRequest(positiveFeedback),
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        if (response.optBoolean("success", false))
                        {
                            cb.onSuccess(null);
                        }
                        else
                        {
                            cb.onError(new DataManagerError(Type.SERVER));
                        }
                    }
                }
        );
    }

    public final void authUser(final String email, final String password, final Callback<User> cb)
    {
        mService.createUserSession(email, password, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    public void createUser(final CreateUserRequest createUserRequest, final Callback<User> cb)
    {
        mService.createUser(createUserRequest, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    public final void authFBUser(final CreateUserRequest createUserRequest, final Callback<User> cb)
    {
        mService.createUserSessionFB(
                createUserRequest,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        handleCreateSessionResponse(response, cb);
                    }
                }
        );
    }

    public final void getUser(final String userId, final String authToken, final Callback<User> cb)
    {
        mService.getUserInfo(userId, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                handleUserResponse(userId, authToken, response, cb);
            }
        });
    }

    public final void getUserExists(final String email, final Callback<UserExistsResponse> cb)
    {
        mService.getUserExists(email, new UserExistsHandyRetrofitCallback(cb));
    }

    /**
     * @param updateUserRequest
     * @param authToken         needed because the success callback sets this to the User object it
     *                          creates. ideally, it should not be used this way.
     * @param cb
     */
    public void updateUser(
            final UpdateUserRequest updateUserRequest,
            final String authToken,
            final Callback<User> cb
    )
    {
        mService.updateUserInfo(
                updateUserRequest.getUserId(),
                updateUserRequest,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(JSONObject response)
                    {
                        //TODO: auth token should not be set this way!
                        handleUserResponse(
                                updateUserRequest.getUserId(),
                                authToken,
                                response,
                                cb
                        );
                    }
                }
        );
    }

    public void updatePayment(final String userId, final String token, final Callback<Void> cb)
    {
        mService.updatePaymentInfo(userId, token, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public final void requestPasswordReset(final String email, final Callback<String> cb)
    {
        mService.requestPasswordReset(email, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(JSONObject response)
            {
                final JSONArray array = response.optJSONArray("messages");
                cb.onSuccess(array != null && array.length() > 0 ?
                                     array.isNull(0) ? null : array.optString(0) : null);
            }
        });
    }

    public final void getRequestProInfo(
            int bookingId,
            Callback<BookingRequestablePros> cb
    )
    {
        mService.getRequestProInfo(
                bookingId,
                new BookingRequestableProsResponseHandyRetroFitCallback(cb)
        );
    }

    public final void requestProForBooking(
            int bookingId,
            int requestedProId,
            Callback<BookingProRequestResponse> cb
    )
    {
        mService.requestProForBooking(
                bookingId,
                requestedProId,
                true,
                new BookingProRequestResponseHandyRetroFitCallback(cb)
        );
    }

    public final void getHelpInfo(
            final String nodeId,
            final String bookingId,
            final Callback<HelpNodeWrapper> cb
    )
    {
        mService.getHelpInfo(
                nodeId,
                bookingId,
                new HelpNodeWrapperResponseHandyRetroFitCallback(cb)
        );
    }

    public final void getHelpBookingsInfo(
            final String nodeId, final String bookingId,
            final Callback<HelpNodeWrapper> cb
    )
    {
        mService.getHelpBookingsInfo(
                nodeId,
                bookingId,
                new HelpNodeWrapperResponseHandyRetroFitCallback(cb)
        );
    }

    public final void createHelpCase(TypedInput body, final Callback<Void> cb)
    {
        mService.createHelpCase(body, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    public void requestPrepareReferrals(final Callback<ReferralResponse> cb)
    {
        mService.requestPrepareReferrals("", new ReferralResponseHandyRetrofitCallback(cb));
    }

    public void requestConfirmReferral(final String guid, final Callback<Void> cb)
    {
        mService.requestConfirmReferral(guid, new EmptyHandyRetroFitCallback(cb));
    }

    public void requestRedemptionDetails(
            @NonNull final String guid,
            @NonNull Callback<RedemptionDetailsResponse> cb
    )
    {
        mService.requestRedemptionDetails(
                guid,
                new RedemptionDetailsResponseHandyRetrofitCallback(cb)
        );
    }

    public void requestConfiguration(final Callback<Configuration> cb)
    {
        mService.requestConfiguration(
                new ConfigurationHandyRetrofitCallback(cb));
    }

    public void getRecurringBookings(final Callback<RecurringBookingsResponse> cb)
    {
        mService.getRecurringBookings(new RecurringBookingsResponseHandyRetrofitCallback(cb));
    }

    public final void updateBookingNoteToPro(
            int bookingId,
            BookingUpdateNoteToProTransaction descriptionTransaction,
            final Callback<Void> cb
    )
    {
        mService.updateBookingNoteToPro(
                bookingId,
                descriptionTransaction,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    /**
     * @param bookingId
     * @param callback
     * @deprecated - use getLocationStatus instead.
     */
    @Deprecated
    public void getBookingGeoStatus(
            final String bookingId,
            final Callback<BookingGeoStatus> callback
    )
    {
        mService.getBookingGeoStatus(
                bookingId,
                new BookingGeoStatusHandyRetrofitCallback(callback)
        );
    }

    public void getLocationStatus(
            final String bookingId,
            final Callback<Booking.LocationStatus> callback
    )
    {
        mService.getLocationStatus(
                bookingId,
                new BookingLocationStatusHandyRetrofitCallback(callback)
        );
    }

    public final void updateBookingEntryInformation(
            String bookingId,
            BookingEditEntryInformationRequest editEntryInformationRequest,
            final Callback<Void> cb
    )
    {
        mService.updateBookingEntryInformation(
                bookingId,
                editEntryInformationRequest,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    public final void updateBookingFrequency(
            int bookingId,
            BookingEditFrequencyRequest bookingEditFrequencyRequest,
            final Callback<Void> cb
    )
    {
        mService.updateBookingFrequency(
                bookingId,
                bookingEditFrequencyRequest,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    public final void getBookingPricesForFrequencies(
            int bookingId,
            final Callback<BookingEditFrequencyInfoResponse> cb
    )
    {
        mService.getBookingPricesForFrequencies(
                bookingId,
                new BookingPricesForFrequenciesHandyRetroFitCallback(cb)
        );
    }

    public void updateRecurringFrequency(
            final String recurringId,
            final BookingEditFrequencyRequest bookingEditFrequencyRequest,
            final Callback<Void> cb
    )
    {
        mService.updateRecurringFrequency(
                recurringId,
                bookingEditFrequencyRequest,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);

                    }
                }
        );
    }

    public void getRecurringFrequency(
            final String recurringId,
            final Callback<BookingEditFrequencyInfoResponse> cb
    )
    {
        mService.getRecurringFrequency(
                recurringId,
                new BookingPricesForFrequenciesHandyRetroFitCallback(cb)
        );
    }

    public void finalizeBooking(
            int bookingId,
            @NonNull FinalizeBookingRequestPayload finalizeBookingRequestPayload,
            @NonNull final Callback<Void> cb
    )
    {
        mService.finalizeBooking(
                bookingId,
                finalizeBookingRequestPayload,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    public void updatePreferences(
            int bookingId,
            @NonNull FinalizeBookingRequestPayload finalizeBookingRequestPayload,
            @NonNull final Callback<Void> cb
    )
    {
        mService.updatePreferences(
                bookingId,
                finalizeBookingRequestPayload,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    protected void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                }
        );
    }

    public void getHelpCenterInfo(final Callback<HelpCenterResponse> cb)
    {
        mService.getHelpCenterInfo(new HelpCenterResponseHandyRetrofitCallback(cb));
    }

    public void postLogs(final JsonObject eventLogBundle, final Callback<EventLogResponse> cb)
    {
        mService.postLogs(eventLogBundle, new HandyRetrofitCallback(cb)
        {
            @Override
            protected void success(final JSONObject response)
            {
                try
                {
                    cb.onSuccess(new Gson().fromJson(response.toString(), EventLogResponse.class));
                } catch (Exception e) {
                    Crashlytics.log("EventLogResponse error: " + response);
                    cb.onError(null);
                }
            }
        });
    }

    private void handleCreateSessionResponse(final JSONObject response, final Callback<User> cb)
    {
        final User user = new User();
        user.setAuthToken(response.isNull("auth_token") ? null : response.optString("auth_token"));
        user.setId(response.isNull("id") ? null : response.optString("id"));
        cb.onSuccess(user);
    }

    private void handleUserResponse(
            final String userId, final String authToken,
            final JSONObject response, final Callback<User> cb
    )
    {
        final User user = User.fromJson(response.toString());

        user.setAuthToken(authToken);
        user.setId(userId);
        cb.onSuccess(user);
    }

    private String parseAlertMessage(final JSONObject response)
    {
        if (response.optBoolean("alert", false))
        {
            final JSONArray array = response.optJSONArray("messages");
            return array != null && !array.isNull(0) ? array.optString(0) : null;
        }
        else
        {
            return null;
        }
    }

    public interface Callback<T>
    {
        void onSuccess(T response);

        void onError(DataManagerError error);
    }


    public interface CacheResponse<T>
    {
        void onResponse(T response);
    }


    public enum Type
    {
        OTHER, SERVER, CLIENT, NETWORK
    }


    public static class DataManagerError
    {
        private final Type type;
        private final String message;
        private String[] invalidInputs;

        public DataManagerError(final Type type)
        {
            this.type = type;
            this.message = null;
        }

        public DataManagerError(final Type type, final String message)
        {
            this.type = type;
            this.message = message;
        }

        final String[] getInvalidInputs()
        {
            return invalidInputs;
        }

        final void setInvalidInputs(final String[] inputs)
        {
            this.invalidInputs = inputs;
        }

        public String getMessage()
        {
            return message;
        }

        public Type getType()
        {
            return type;
        }
    }
}
