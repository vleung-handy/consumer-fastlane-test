package com.handybook.handybook.data;

import android.support.v4.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.core.Booking;
import com.handybook.handybook.core.BookingCompleteTransaction;
import com.handybook.handybook.core.BookingCoupon;
import com.handybook.handybook.core.BookingOption;
import com.handybook.handybook.core.BookingPostInfo;
import com.handybook.handybook.core.BookingQuote;
import com.handybook.handybook.core.BookingRequest;
import com.handybook.handybook.core.BookingTransaction;
import com.handybook.handybook.core.PromoCode;
import com.handybook.handybook.core.Service;
import com.handybook.handybook.core.User;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager {
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final SecurePreferences prefs;

    @Inject
    public BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint,
                    final Bus bus, final SecurePreferences prefs) {
        super(bus);
        this.service = service;
        this.endpoint = endpoint;
        this.prefs = prefs;
    }

    @Override
    public void setEnvironment(final Environment env, final boolean notify) {
        super.setEnvironment(env, notify);
        switch (env) {
            case P:
                endpoint.setEnv(HandyRetrofitEndpoint.Environment.P);
                break;

            case Q1:
                endpoint.setEnv(HandyRetrofitEndpoint.Environment.Q1);
                break;

            case Q2:
                endpoint.setEnv(HandyRetrofitEndpoint.Environment.Q2);
                break;

            case Q3:
                endpoint.setEnv(HandyRetrofitEndpoint.Environment.Q3);
                break;

            case Q4:
                endpoint.setEnv(HandyRetrofitEndpoint.Environment.Q4);
                break;

            default:
                endpoint.setEnv(HandyRetrofitEndpoint.Environment.S);
        }
    }

    @Override
    public final void getServices(final CacheResponse<List<Service>> cache,
                                  final Callback<List<Service>> cb) {
        final List<Service> cachedServices = new Gson().fromJson(prefs.getString("CACHED_SERVICES"),
                new TypeToken<List<Service>>(){}.getType());
        cache.onResponse(cachedServices != null ? cachedServices : new ArrayList<Service>());

        final ArrayList<Service> servicesMenu = new ArrayList<>();
        final HashMap<String, Service> menuMap = new HashMap<>();

        service.getServicesMenu(new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                final JSONArray array = response.optJSONArray("menu_structure");

                if (array == null) {
                    cb.onError(new DataManagerError(Type.SERVER));
                    return;
                }

                for (int i = 0; i <= array.length(); i ++) {
                    final JSONObject obj = array.optJSONObject(i);

                    if (obj != null) {
                        final String name = obj.isNull("name") ? null : obj.optString("name", null);
                        final int ignore = obj.optInt("ignore", 1);

                        if (name == null || ignore == 1) continue;

                        final Service service = new Service();
                        service.setUniq(obj.isNull("uniq") ? null : obj.optString("uniq"));
                        service.setName(obj.isNull("name") ? null : obj.optString("name"));
                        service.setOrder(obj.optInt("order", 0));
                        servicesMenu.add(service);
                        menuMap.put(service.getUniq(), service);
                    }
                }

                final HashMap<Integer, ArrayList<Service>> servicesMap = new HashMap<>();

                service.getServices(new HandyRetrofitCallback(cb) {
                    @Override
                    void success(final JSONObject response) {
                        final JSONArray array = response.optJSONArray("services_list");

                        if (array == null) {
                            cb.onError(new DataManagerError(Type.SERVER));
                            return;
                        }

                        for (int i = 0; i <= array.length(); i ++) {
                            final JSONObject obj = array.optJSONObject(i);

                            if (obj != null && obj.optBoolean("no_show", true)) {
                                final Service service = new Service();
                                service.setId(obj.optInt("id"));

                                service.setUniq(obj.isNull("machine_name") ? null
                                        : obj.optString("machine_name"));

                                service.setName(obj.isNull("name") ? null : obj.optString("name"));
                                service.setOrder(obj.optInt("order", 0));
                                service.setParentId(obj.optInt("parent", 0));

                                final Service menuService;
                                if ((menuService = menuMap.get(service.getUniq())) != null) {
                                    menuService.setId(service.getId());
                                    continue;
                                }

                                ArrayList<Service> list;
                                if ((list = servicesMap.get(service.getParentId())) != null)
                                    list.add(service);
                                else {
                                    list = new ArrayList<Service>();
                                    list.add(service);
                                    servicesMap.put(service.getParentId(), list);
                                }
                            }
                        }

                        for (final Service service : servicesMenu) {
                            final List<Service> services;
                            if ((services = servicesMap.get(service.getId())) != null) {
                                Collections.sort(services, new Comparator<Service>() {
                                    @Override
                                    public int compare(final Service lhs, final Service rhs) {
                                        return lhs.getOrder() - rhs.getOrder();
                                    }
                                });
                                service.setServices(services);
                            }
                        }

                        Collections.sort(servicesMenu, new Comparator<Service>() {
                            @Override
                            public int compare(final Service lhs, final Service rhs) {
                                return lhs.getOrder() - rhs.getOrder();
                            }
                        });

                        prefs.put("CACHED_SERVICES", new Gson()
                                .toJsonTree(servicesMenu).getAsJsonArray().toString());
                        cb.onSuccess(servicesMenu);
                    }
                });
            }
        });
    }

    @Override
    public final void validateBookingZip(final int serviceId, final String zipCode, final String userId,
                                         final String authToken, final String promoCode,
                                         final Callback<Void> cb) {
        service.validateBookingZip(serviceId, zipCode, userId, authToken, promoCode, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public final void getBookings(final User user, final Callback<List<Booking>> cb) {
        service.getBookings(user.getAuthToken(), new HandyRetrofitCallback(cb) {
            @Override
            void success(JSONObject response) {
                final JSONArray array = response.optJSONArray("user_bookings");

                if (array == null) {
                    cb.onError(new DataManagerError(Type.SERVER));
                    return;
                }

                final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
                final List<Booking> bookings = gson.fromJson(array.toString(),
                        new TypeToken<List<Booking>>(){}.getType());
                cb.onSuccess(bookings);
            }
        });
    }

    @Override
    public final void getBookingOptions(final int serviceId, final String userId,
                                        final Callback<List<BookingOption>> cb) {
        service.getBookingOptions(serviceId, userId, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                final JSONArray array = response.optJSONArray("booking_options");

                if (array == null) {
                    cb.onError(new DataManagerError(Type.SERVER));
                    return;
                }

                final Gson gson = new Gson();
                final List<BookingOption> bookingOptions = gson.fromJson(array.toString(),
                        new TypeToken<List<BookingOption>>(){}.getType());
                cb.onSuccess(bookingOptions);
            }
        });
    }

    @Override
    public void getBookingQuote(final BookingRequest bookingRequest, final Callback<BookingQuote> cb) {
        service.createBooking(new HandyRetrofitService.BookingCreateRequest(bookingRequest),
                new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void updateBookingDate(final int bookingId, final Date date,
                                  final Callback<BookingQuote> cb) {
        service.updateBookingDate(bookingId, date, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void applyPromo(final String promoCode, final int bookingId, final String userId,
                           final String email, final String authToken, final Callback<BookingCoupon> cb) {
        service.applyPromo(promoCode, bookingId, userId, email, authToken,
                new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(BookingCoupon.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void removePromo(final int bookingId, final Callback<BookingCoupon> cb) {
        service.removePromo(bookingId, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(BookingCoupon.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void getPreBookingPromo(final String promoCode, final Callback<PromoCode> cb) {
        service.getPreBookingPromo(promoCode, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                if (response.has("coupon") && response.optInt("coupon") == 1) {
                    cb.onSuccess(new PromoCode(PromoCode.Type.COUPON, promoCode));
                }
                else if (response.has("voucher")) {
                    final JSONObject voucher = response.optJSONObject("voucher");

                    final PromoCode code = new PromoCode(PromoCode.Type.VOUCHER,
                            voucher.optString("code"));

                    code.setServiceId(voucher.optInt("service_id"));

                    //TODO remove this harcoding below after update to API response
                    code.setUniq("home_cleaning");
                    //code.setUniq(voucher.optString("machine_name"));

                    cb.onSuccess(code);
                }
                else {
                    cb.onError(new DataManagerError(Type.SERVER));
                }
            }
        });
    }

    @Override
    public void completeBooking(final BookingTransaction bookingTransaction,
                                final Callback<BookingCompleteTransaction> cb) {
        service.completeBooking(bookingTransaction, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(BookingCompleteTransaction.fromJson(response.toString()));
            }
        });
    }

    @Override
    public void addBookingPostInfo(final int bookingId, final BookingPostInfo postInfo,
                                   final Callback<Void> cb) {
        service.addBookingPostInfo(bookingId, postInfo, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    public void getPreRescheduleInfo(final String bookingId, final Callback<String> cb) {
        service.getPreRescheduleInfo(bookingId, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(response.isNull("notice") ? null : response.optString("notice", null));
            }
        });
    }

    @Override
    public void rescheduleBooking(final String bookingId, final String date, final boolean rescheduleAll,
                                  final String userId, final String authToken,
                                  final Callback<Pair<String, BookingQuote>> cb) {
        service.rescheduleBooking(bookingId, date, rescheduleAll ? 1 : 0, userId, authToken,
                new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                final String message = parseAlertMessage(response);
                BookingQuote quote = null;

                if (response.optJSONArray("dynamic_options") != null) {
                    quote = BookingQuote.fromJson(response.toString());
                }

                cb.onSuccess(new Pair<>(message, quote));
            }
        });
    }

    @Override
    public void getPreCancelationInfo(final String bookingId,
                                      final Callback<Pair<String, List<String>>> cb) {
        service.getPreCancelationInfo(bookingId, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                final String notice = response.isNull("notice") ? null
                        : response.optString("notice", null);

                final JSONArray array = response.optJSONArray("options");
                final Gson gson = new Gson();
                final List<String> options = gson.fromJson(array.toString(),
                        new TypeToken<List<String>>() {
                        }.getType());

                cb.onSuccess(new Pair<>(notice, options));
            }
        });
    }

    @Override
    public void cancelBooking(final String bookingId, final int reasonCode, final String userId,
                              final String authToken, final Callback<String> cb) {
        final HandyRetrofitCallback callback = new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                final String message = parseAlertMessage(response);
                cb.onSuccess(message);
            }
        };

        if (reasonCode >= 0) service.cancelBooking(bookingId,reasonCode, userId, authToken, callback);
        else service.cancelBooking(bookingId, userId, authToken, callback);
    }

    @Override
    public final void authUser(final String email, final String password, final Callback<User> cb) {
        service.createUserSession(email, password, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    @Override
    public final void authFBUser(final String fbid, final String accessToken, final String email,
                                 final String firstName, String lastName, final Callback<User> cb) {
        service.createUserSessionFB(fbid, accessToken, email, firstName, lastName,
                new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    public final void getUser(final String userId, final String authToken, final Callback<User> cb) {
        service.getUserInfo(userId, authToken, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleUserResponse(userId, authToken, response, cb);
            }
        });
    }

    public final void getUser(final String email, final Callback<String> cb) {
        service.getUserInfo(email, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(response.isNull("name") ? null : response.optString("name"));
            }
        });
    }
    
    public final void updateUser(final User user, final Callback<User> cb) {
        service.updateUserInfo(user.getId(), new HandyRetrofitService.UserUpdateRequest(user,
                user.getAuthToken()), new HandyRetrofitCallback(cb) {
            @Override
            void success(JSONObject response) {
                handleUserResponse(user.getId(), user.getAuthToken(), response, cb);
            }
        });
    }

    @Override
    public final void requestPasswordReset(final String email, final Callback<String> cb) {
        service.requestPasswordReset(email, new HandyRetrofitCallback(cb) {
            @Override
            void success(JSONObject response) {
                final JSONArray array = response.optJSONArray("messages");
                cb.onSuccess(array != null && array.length() > 0 ?
                        (array.isNull(0) ? null : array.optString(0)) : null);
            }
        });
    }

    private void handleCreateSessionResponse(final JSONObject response, final Callback<User> cb) {
        final User user = new User();
        user.setAuthToken(response.isNull("auth_token") ? null : response.optString("auth_token"));
        user.setId(response.isNull("id") ? null : response.optString("id"));
        cb.onSuccess(user);
    }

    private void handleUserResponse(final String userId, final String authToken,
                                    final JSONObject response, final Callback<User> cb) {
        final User user = User.fromJson(response.toString());

        user.setAuthToken(authToken);
        user.setId(userId);
        cb.onSuccess(user);
    }

    private String parseAlertMessage(final JSONObject response) {
        if (response.optBoolean("alert", false)) {
            final JSONArray array = response.optJSONArray("messages");
            return array != null && !array.isNull(0) ? array.optString(0) : null;
        }
        else return null;
    }
}