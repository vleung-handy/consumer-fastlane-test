package com.handybook.handybook.core.data;

import org.json.JSONObject;

public class VoidRetrofitCallback extends HandyRetrofitCallback {

    public VoidRetrofitCallback() {
        super(new VoidDataManagerCallback());
    }

    @Override
    protected void success(final JSONObject response) {
        // do nothing
    }
}
