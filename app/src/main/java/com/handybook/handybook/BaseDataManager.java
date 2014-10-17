package com.handybook.handybook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.inject.Inject;

public final class BaseDataManager extends DataManager {
    private final HandyRetrofitService service;
    private final HandyRetrofitEndpoint endpoint;

    @Inject
    BaseDataManager(final HandyRetrofitService service, final HandyRetrofitEndpoint endpoint) {
        this.service = service;
        this.endpoint = endpoint;
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
    public final String[] getServices() {
        return new String[]{"Category 1", "Category 2", "Category 3", "Category 4"};
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
        service.createUserSessionFB(fbid, accessToken, email, firstName, lastName, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                handleCreateSessionResponse(response, cb);
            }
        });
    }

    public final void getUserInfo(final String userId, final String authToken, final Callback<User> cb) {
        service.getUserInfo(userId, authToken, new HandyRetrofitCallback(cb) {
            @Override
            void success(final JSONObject response) {
                final Gson gson = new Gson();
                final User user = gson.fromJson(response.toString(), new TypeToken<User>(){}.getType());

                user.setAuthToken(authToken);
                user.setId(userId);
                cb.onSuccess(user);
            }
        });
    }

    @Override
    public final void requestPasswordReset(final String email, final Callback<String> cb) {
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
}
