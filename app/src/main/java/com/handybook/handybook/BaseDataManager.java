package com.handybook.handybook;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager {
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;
    private final SecurePreferences prefs;

    @Inject
    BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint,
                    final Bus bus, final SecurePreferences prefs) {
        super(bus);
        this.service = service;
        this.endpoint = endpoint;
        this.prefs = prefs;
    }

    @Override
    void setEnvironment(Environment env) {
        super.setEnvironment(env);
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
    final void getServices(final CacheResponse<List<Service>> cache,
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
                        service.setUniq(obj.optString("uniq"));
                        service.setName(obj.optString("name"));
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
                                service.setUniq(obj.optString("machine_name"));
                                service.setName(obj.optString("name"));
                                service.setOrder(obj.optInt("order", 0));
                                service.setParentId(obj.optInt("parent", 0));

                                final Service menuService;
                                if ((menuService = menuMap.get(service.getUniq())) != null) {
                                    menuService.setId(service.getId());
                                    continue;
                                }

                                ArrayList<Service> list;
                                if ((list = servicesMap.get(service.getParentId())) != null) list.add(service);
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
    final void validateBookingZip(final int serviceId, final String zipCode, final Callback<Void> cb) {
        service.validateBookingZip(serviceId, zipCode, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(null);
            }
        });
    }

    @Override
    final void getBookings(final User user, final Callback<List<Booking>> cb) {
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
    final void getBookingOptions(final int serviceId, final String userId, final Callback<List<BookingOption>> cb) {
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
    void getBookingQuote(final BookingRequest bookingRequest, final Callback<BookingQuote> cb) {
        service.createBooking(new HandyRetrofitService.BookingCreateRequest(bookingRequest),
                new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                cb.onSuccess(BookingQuote.fromJson(response.toString()));
            }
        });
    }

    @Override
    final void authUser(final String email, final String password, final Callback<User> cb) {
        service.createUserSession(email, password, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    @Override
    final void authFBUser(final String fbid, final String accessToken, final String email,
                                 final String firstName, String lastName, final Callback<User> cb) {
        service.createUserSessionFB(fbid, accessToken, email, firstName, lastName, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    final void getUser(final String userId, final String authToken, final Callback<User> cb) {
        service.getUserInfo(userId, authToken, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleUserResponse(userId, authToken, response, cb);
            }
        });
    }

    final void updateUser(final User user, final Callback<User> cb) {
        service.updateUserInfo(user.getId(), new HandyRetrofitService.UserUpdateRequest(user,
                user.getAuthToken()), new HandyRetrofitCallback(cb) {
            @Override
            void success(JSONObject response) {
                handleUserResponse(user.getId(), user.getAuthToken(), response, cb);
            }
        });
    }

    @Override
    final void requestPasswordReset(final String email, final Callback<String> cb) {
        service.requestPasswordReset(email, new HandyRetrofitCallback(cb) {
            @Override
            void success(JSONObject response) {
                final JSONArray array = response.optJSONArray("messages");
                cb.onSuccess(array != null && array.length() > 0 ? array.optString(0) : null);
            }
        });
    }

    private void handleCreateSessionResponse(final JSONObject response, final Callback<User> cb) {
        final User user = new User();
        user.setAuthToken(response.optString("auth_token"));
        user.setId(response.optString("id"));
        cb.onSuccess(user);
    }

    private void handleUserResponse(final String userId, final String authToken,
                                    final JSONObject response, final Callback<User> cb) {
        final Gson gson = new Gson();
        final User user = gson.fromJson(response.toString(), new TypeToken<User>(){}.getType());

        user.setAuthToken(authToken);
        user.setId(userId);
        cb.onSuccess(user);
    }
}
