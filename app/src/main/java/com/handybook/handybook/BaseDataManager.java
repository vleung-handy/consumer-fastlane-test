package com.handybook.handybook;

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
            public final void success(final JSONObject response) {
                final User user = new User();
                user.setAuthToken(response.optString("auth_token"));
                user.setId(response.optString("id"));
                cb.onSuccess(user);
            }
        });
    }
}
