package com.handybook.handybook.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.booking.model.Booking;
import com.handybook.handybook.booking.model.BookingCompleteTransaction;
import com.handybook.handybook.booking.model.BookingCoupon;
import com.handybook.handybook.booking.model.BookingEditAddressRequest;
import com.handybook.handybook.booking.model.BookingEditExtrasInfoResponse;
import com.handybook.handybook.booking.model.BookingEditExtrasRequest;
import com.handybook.handybook.booking.model.BookingEditFrequencyInfoResponse;
import com.handybook.handybook.booking.model.BookingEditFrequencyRequest;
import com.handybook.handybook.booking.model.BookingEditHoursInfoResponse;
import com.handybook.handybook.booking.model.BookingEditHoursRequest;
import com.handybook.handybook.booking.model.BookingOptionsWrapper;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingProRequestResponse;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingRequestablePros;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.booking.model.BookingUpdateEntryInformationTransaction;
import com.handybook.handybook.booking.model.BookingUpdateNoteToProTransaction;
import com.handybook.handybook.booking.model.LaundryDropInfo;
import com.handybook.handybook.booking.model.PromoCode;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.booking.model.UserBookingsWrapper;
import com.handybook.handybook.constant.PrefsKey;
import com.handybook.handybook.core.BlockedWrapper;
import com.handybook.handybook.core.SuccessWrapper;
import com.handybook.handybook.core.User;
import com.handybook.handybook.helpcenter.model.HelpNodeWrapper;
import com.handybook.handybook.manager.PrefsManager;
import com.handybook.handybook.model.request.UpdateUserRequest;
import com.handybook.handybook.module.notifications.notificationsplash.model.SplashPromo;
import com.handybook.handybook.model.response.UserExistsResponse;
import com.handybook.handybook.module.notifications.notificationfeed.model.HandyNotification;

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

public final class BaseDataManager extends DataManager
{
    private final HandyRetrofitService mService;
    private final HandyRetrofitEndpoint mEndpoint;
    private final PrefsManager mPrefsManager;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint, final PrefsManager prefsManager)
    {
        mService = service;
        mEndpoint = endpoint;
        mPrefsManager = prefsManager;
    }

    @Override
    public String getBaseUrl()
    {
        return mEndpoint.getBaseUrl();
    }

    @Override
    public final void getServices(final CacheResponse<List<Service>> cache,
                                  final Callback<List<Service>> cb)
    {

        String cachedServicesJson = mPrefsManager.getString(PrefsKey.CACHED_SERVICES);
        List<Service> cachedServices = null;
        if (cachedServicesJson != null)
        {
            cachedServices = new Gson().fromJson(
                    mPrefsManager.getString(PrefsKey.CACHED_SERVICES),
                    new TypeToken<List<Service>>()
                    {
                    }.getType());
        }

        cache.onResponse(cachedServices != null ? cachedServices : new ArrayList<Service>());

        final ArrayList<Service> servicesMenu = new ArrayList<>();
        final HashMap<String, Service> menuMap = new HashMap<>();

        mService.getServicesMenu(new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final JSONArray array = response.optJSONArray("menu_structure");

                if (array == null)
                {
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
                    void success(final JSONObject response)
                    {
                        final JSONArray array = response.optJSONArray("services_list");

                        if (array == null)
                        {
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

                        mPrefsManager.setString(PrefsKey.CACHED_SERVICES, new Gson()
                                .toJsonTree(servicesMenu).getAsJsonArray().toString());
                        cb.onSuccess(servicesMenu);
                    }
                });
            }
        });
    }

    @Override
    public void getAvailableSplashPromo(final String userId, final Callback<SplashPromo> cb)
    {
        mService.getAvailableSplashPromo(userId, new AvailableSplashPromoRetrofitCallback(cb));
    }

    @Override
    public void editBookingAddress(final int bookingId,
                                   final BookingEditAddressRequest bookingEditAddressRequest,
                                   final Callback<SuccessWrapper> cb)
    {
        mService.editBookingAddress(bookingId, bookingEditAddressRequest, new SuccessHandyRetroFitCallback(cb));
    }

    @Override
    public void sendCancelRecurringBookingEmail(final int bookingRecurringId, final
                                                Callback<SuccessWrapper> cb)
    {
        mService.sendCancelRecurringBookingEmail(bookingRecurringId, new SuccessHandyRetroFitCallback(cb));
    }

    @Override
    public void getEditBookingExtrasInfo(final int bookingId, final Callback<BookingEditExtrasInfoResponse> cb)
    {
        mService.getEditExtrasInfo(bookingId, new EditExtrasInfoHandyRetroFitCallback(cb));
    }

    @Override
    public void editBookingExtras(final int bookingId,
                                  final BookingEditExtrasRequest bookingEditExtrasRequest,
                                  final Callback<SuccessWrapper> cb)
    {
        mService.editServiceExtras(bookingId, bookingEditExtrasRequest, new SuccessHandyRetroFitCallback(cb));
    }

    @Override
    public void editBookingHours(final int bookingId,
                                  final BookingEditHoursRequest bookingEditHoursRequest,
                                  final Callback<SuccessWrapper> cb)
    {
        mService.editBookingHours(bookingId, bookingEditHoursRequest, new SuccessHandyRetroFitCallback(cb));
    }

    @Override
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
                    void success(JSONObject response)
                    {
                        final BlockedWrapper blockedWrapper = BlockedWrapper
                                .fromJson(response.toString());
                        blockedWrapperCallback.onSuccess(blockedWrapper);
                    }
                });
    }

    @Override
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

    @Override
    public final void getQuoteOptions(final int serviceId, final String userId,
                                      final Callback<BookingOptionsWrapper> cb)
    {
        mService.getQuoteOptions(serviceId, userId, new BookingOptionsWrapperHandyRetroFitCallback(cb));
    }

    @Override
    public void createQuote(final BookingRequest bookingRequest, final Callback<BookingQuote> cb)
    {
        mService.createQuote(bookingRequest, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void updateQuoteDate(final int quoteId, final Date date,
                                final Callback<BookingQuote> cb)
    {
        mService.updateQuoteDate(quoteId, date, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void applyPromo(final String promoCode, final int quoteId, final String userId,
                           final String email, final String authToken, final Callback<BookingCoupon> cb)
    {
        mService.applyPromo(promoCode, quoteId, userId, email, authToken,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    void success(final JSONObject response)
                    {
                        cb.onSuccess(BookingCoupon.fromJson(response.toString()));
                    }
                });
    }

    @Override
    public void removePromo(final int quoteId, final Callback<BookingCoupon> cb)
    {
        mService.removePromo(quoteId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(BookingCoupon.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void createBooking(final BookingTransaction bookingTransaction,
                              final Callback<BookingCompleteTransaction> cb)
    {
        mService.createBooking(bookingTransaction.getBookingId(), bookingTransaction, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(BookingCompleteTransaction.fromJson(response.toString()));
            }
        });
    }

    @Override
    public final void validateBookingZip(final int serviceId, final String zipCode, final String userId,
                                         final String authToken, final String promoCode,
                                         final Callback<Void> cb)
    {
        mService.validateBookingZip(serviceId, zipCode, userId, authToken, promoCode,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                });
    }

    @Override
    public final void getBookings(
            final User user,
            final Callback<UserBookingsWrapper> cb)
    {
        mService.getBookings(
                user.getAuthToken(),
                null,
                new UserBookingsWrapperHandyRetroFitCallback(cb)
        );
    }

    @Override
    public final void getBookings(
            final User user,
            @NonNull @Booking.List.OnlyBookingValues final String onlyBookingValue,
            final Callback<UserBookingsWrapper> cb)
    {
        mService.getBookings(
                user.getAuthToken(),
                onlyBookingValue,
                new UserBookingsWrapperHandyRetroFitCallback(cb)
        );
    }

    @Override
    public final void getEditHoursInfo(final int bookingId,
            final Callback<BookingEditHoursInfoResponse> cb)
    {
        mService.getEditHoursInfo(
                bookingId,
                new EditHoursInfoHandyRetroFitCallback(cb)
        );
    }

    @Override
    public void getBooking(final String bookingId, final Callback<Booking> cb)
    {
        mService.getBooking(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(Booking.fromJson(response.optJSONObject("booking").toString()));
            }
        });
    }

    @Override
    public void getPreBookingPromo(final String promoCode, final Callback<PromoCode> cb)
    {
        mService.getPreBookingPromo(promoCode, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                if (response.has("coupon") && response.optInt("coupon") == 1)
                {
                    cb.onSuccess(new PromoCode(PromoCode.Type.COUPON, promoCode));
                }
                else if (response.has("voucher"))
                {
                    final JSONObject voucher = response.optJSONObject("voucher");

                    final PromoCode code = new PromoCode(PromoCode.Type.VOUCHER,
                            voucher.optString("code"));

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

    @Override
    public void addBookingPostInfo(final int bookingId, final BookingPostInfo postInfo,
                                   final Callback<Void> cb)
    {
        mService.addBookingPostInfo(bookingId, postInfo, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void getPreRescheduleInfo(final String bookingId, final Callback<String> cb)
    {
        mService.getPreRescheduleInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(response.isNull("notice") ? null : response.optString("notice", null));
            }
        });
    }

    @Override
    public void rescheduleBooking(final String bookingId, final String date, final boolean rescheduleAll,
                                  final String userId, final String authToken,
                                  final Callback<Pair<String, BookingQuote>> cb)
    {
        mService.rescheduleBooking(bookingId, date, rescheduleAll ? 1 : 0, userId, authToken,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    void success(final JSONObject response)
                    {
                        final String message = parseAlertMessage(response);
                        BookingQuote quote = null;

                        if (response.optJSONArray("dynamic_options") != null)
                        {
                            quote = BookingQuote.fromJson(response.toString());
                        }

                        cb.onSuccess(new Pair<>(message, quote));
                    }
                });
    }

    @Override
    public void getPreCancelationInfo(final String bookingId,
                                      final Callback<Pair<String, List<String>>> cb)
    {
        mService.getPreCancelationInfo(bookingId, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final String notice = response.isNull("notice") ? null
                        : response.optString("notice", null);

                final JSONArray array = response.optJSONArray("options");
                final Gson gson = new Gson();
                final List<String> options = gson.fromJson(array.toString(),
                        new TypeToken<List<String>>()
                        {
                        }.getType());

                cb.onSuccess(new Pair<>(notice, options));
            }
        });
    }

    @Override
    public void cancelBooking(final String bookingId, final int reasonCode, final String userId,
                              final String authToken, final Callback<String> cb)
    {
        final HandyRetrofitCallback callback = new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final String message = parseAlertMessage(response);
                cb.onSuccess(message);
            }
        };

        if (reasonCode >= 0)
        {
            mService.cancelBooking(bookingId, reasonCode, userId, authToken, callback);
        }
        else
        {
            mService.cancelBooking(bookingId, userId, authToken, callback);
        }
    }

    @Override
    public void getLaundryScheduleInfo(final int bookingId, final String authToken,
                                       final Callback<LaundryDropInfo> cb)
    {
        mService.getLaundryScheduleInfo(bookingId, authToken, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(LaundryDropInfo.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void setLaundryDropOff(final int bookingId, final String authToken, final String date,
                                  final int hour, final int minute, final String type,
                                  final Callback<Void> cb)
    {
        mService.setLaundryDropOff(bookingId, authToken, date, hour, minute, type,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    void success(final JSONObject response)
                    {
                        cb.onSuccess(null);
                    }
                });
    }

    @Override
    public void getAddLaundryInfo(final int bookingId, final String authToken,
                                  final Callback<Booking> cb)
    {
        mService.getAddLaundryInfo(bookingId, authToken, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                final Booking booking = Booking.fromJson(response.optJSONObject("booking").toString());
                booking.setId(Integer.toString(bookingId));
                cb.onSuccess(booking);
            }
        });
    }

    @Override
    public void addLaundry(final int bookingId, final String authToken, final Callback<Void> cb)
    {
        mService.addLaundry(bookingId, authToken, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void ratePro(final int bookingId, final int rating, final Integer tipAmount, final Callback<Void> cb)
    {
        mService.ratePro(bookingId, rating, tipAmount, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void tipPro(final int bookingId, final Integer tipAmount, final Callback<Void> cb)
    {
        mService.tipPro(bookingId, tipAmount, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void submitProRatingDetails(final int bookingId, final String positiveFeedback,
                                       final Callback<Void> cb)
    {
        mService.submitProRatingDetails(bookingId, new HandyRetrofitService
                .RateProRequest(positiveFeedback), new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
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
        });
    }

    @Override
    public final void authUser(final String email, final String password, final Callback<User> cb)
    {
        mService.createUserSession(email, password, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    @Override
    public final void authFBUser(final String fbid, final String accessToken, final String email,
                                 final String firstName, String lastName, final Callback<User> cb)
    {
        mService.createUserSessionFB(fbid, accessToken, email, firstName, lastName,
                new HandyRetrofitCallback(cb)
                {
                    @Override
                    void success(final JSONObject response)
                    {
                        handleCreateSessionResponse(response, cb);
                    }
                });
    }

    public final void getUser(final String userId, final String authToken, final Callback<User> cb)
    {
        mService.getUserInfo(userId, authToken, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                handleUserResponse(userId, authToken, response, cb);
            }
        });
    }

    @Override
    public final void getUserExists(final String email, final Callback<UserExistsResponse> cb)
    {
        mService.getUserExists(email, new UserExistsHandyRetrofitCallback(cb));
    }

    /**
     *
     * @param updateUserRequest
     * @param authToken needed because the success callback sets this
     *                  to the User object it creates.
     *                  ideally, it should not be used this way.
     * @param cb
     */
    @Override
    public final void updateUser(final UpdateUserRequest updateUserRequest, final String authToken, final Callback<User> cb)
    {
        mService.updateUserInfo(updateUserRequest.getUserId(), updateUserRequest, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(JSONObject response)
            {
                //TODO: auth token should not be set this way!
                handleUserResponse(updateUserRequest.getUserId(), authToken, response, cb);
            }
        });
    }

    @Override
    public void updatePayment(final String userId, final String token, final Callback<Void> cb)
    {
        mService.updatePaymentInfo(userId, token, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public final void requestPasswordReset(final String email, final Callback<String> cb)
    {
        mService.requestPasswordReset(email, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(JSONObject response)
            {
                final JSONArray array = response.optJSONArray("messages");
                cb.onSuccess(array != null && array.length() > 0 ?
                        array.isNull(0) ? null : array.optString(0) : null);
            }
        });
    }

    @Override
    public final void getRequestProInfo(int bookingId,
                                        Callback<BookingRequestablePros> cb)
    {
        mService.getRequestProInfo(bookingId, new BookingRequestableProsResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void requestProForBooking(int bookingId,
                                           int requestedProId,
                                           Callback<BookingProRequestResponse> cb)
    {
        mService.requestProForBooking(bookingId, requestedProId, true, new BookingProRequestResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void getHelpInfo(final String nodeId, final String authToken, final String bookingId, final Callback<HelpNodeWrapper> cb)
    {
        mService.getHelpInfo(nodeId, authToken, bookingId, new HelpNodeWrapperResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void getHelpBookingsInfo(final String nodeId, final String authToken, final String bookingId,
                                          final Callback<HelpNodeWrapper> cb)
    {
        mService.getHelpBookingsInfo(nodeId, authToken, bookingId, new HelpNodeWrapperResponseHandyRetroFitCallback(cb));
    }

    @Override
    public final void createHelpCase(TypedInput body, final Callback<Void> cb)
    {
        mService.createHelpCase(body, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public final void updateBookingNoteToPro(int bookingId,
                                             BookingUpdateNoteToProTransaction descriptionTransaction,
                                             final Callback<Void> cb)
    {
        mService.updateBookingNoteToPro(bookingId, descriptionTransaction, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public final void updateBookingEntryInformation(int bookingId,
                                                    BookingUpdateEntryInformationTransaction entryInformationTransaction,
                                                    final Callback<Void> cb)
    {
        mService.updateBookingEntryInformation(bookingId, entryInformationTransaction, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public final void updateBookingFrequency(int bookingId,
                                             BookingEditFrequencyRequest bookingEditFrequencyRequest,
                                             final Callback<Void> cb)
    {
        mService.updateBookingFrequency(bookingId, bookingEditFrequencyRequest, new HandyRetrofitCallback(cb)
        {
            @Override
            void success(final JSONObject response)
            {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public final void getBookingPricesForFrequencies(int bookingId,
                                                     final Callback<BookingEditFrequencyInfoResponse> cb)
    {
        mService.getBookingPricesForFrequencies(bookingId, new BookingPricesForFrequenciesHandyRetroFitCallback(cb));
    }

    private void handleCreateSessionResponse(final JSONObject response, final Callback<User> cb)
    {
        final User user = new User();
        user.setAuthToken(response.isNull("auth_token") ? null : response.optString("auth_token"));
        user.setId(response.isNull("id") ? null : response.optString("id"));
        cb.onSuccess(user);
    }

    private void handleUserResponse(final String userId, final String authToken,
                                    final JSONObject response, final Callback<User> cb)
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

}
