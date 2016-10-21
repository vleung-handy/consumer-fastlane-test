package com.handybook.handybook.module.chat;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.handybook.handybook.data.DataManager;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.handybook.handybook.module.chat.Util.streamToString;

/**
 */
public class LayerAuthenticationProvider implements AuthenticationProvider<LayerAuthenticationProvider.Credentials> {
    private static final String TAG = LayerAuthenticationProvider.class.getName();

    private final SharedPreferences mPreferences;
    private Callback mCallback;
    protected DataManager mDataManager;

    //TODO: JIA: remove this
    private boolean mTesting = true;

    public LayerAuthenticationProvider(Context context, DataManager dataManager)
    {
        mPreferences = context.getSharedPreferences(LayerAuthenticationProvider.class.getSimpleName(), Context.MODE_PRIVATE);
        mDataManager = dataManager;
    }

    @Override
    public AuthenticationProvider<Credentials> setCredentials(Credentials credentials) {
        if (credentials == null) {
            //TODO: JIA: beware, this clears all of our preferences too....,
            mPreferences.edit().clear().commit();
            return this;
        }
        mPreferences.edit()
                    .putString("appId", credentials.getLayerAppId())
                    .putString("name", credentials.getUserName())
                    .putString("userId", credentials.getUserId())
                    .commit();
        return this;
    }

    @Override
    public boolean hasCredentials() {
        return mPreferences.contains("appId");
    }

    @Override
    public AuthenticationProvider<Credentials> setCallback(Callback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public void onAuthenticated(LayerClient layerClient, String userId) {
        Log.d(TAG, "Authenticated with Layer, user ID: " + userId);
        layerClient.connect();
        if (mCallback != null) {
            mCallback.onSuccess(this, userId);
        } else {
            Log.d(TAG, "onAuthenticated: callback is null");
        }
    }

    @Override
    public void onDeauthenticated(LayerClient layerClient) {
        Log.d(TAG, "Deauthenticated with Layer");
    }

    @Override
    public void onAuthenticationChallenge(LayerClient layerClient, String nonce) {
        Log.d(TAG, "Received challenge: " + nonce);

        if (!mTesting)
        {
            respondToChallenge(layerClient, nonce);
        }
        else
        {
            respondToSampleChallenge(layerClient, nonce);
        }
    }

    @Override
    public void onAuthenticationError(LayerClient layerClient, LayerException e) {
        String error = "Failed to authenticate with Layer: " + e.getMessage();
        Log.e(TAG, error, e);
        if (mCallback != null) {
            mCallback.onError(this, error);
        }
    }

    @Override
    public boolean routeLogin(LayerClient layerClient, String layerAppId) {

        if ((layerClient != null) && layerClient.isAuthenticated()) {
            // The LayerClient is authenticated: no action required.
            Log.d(TAG, "No authentication routing required");
            return false;
        }

        return true;
    }

    private void respondToSampleChallenge(LayerClient layerClient, String nonce)
    {
        Log.d(TAG, "respondToChallenge: ");
        Credentials credentials = new Credentials(
                mPreferences.getString("appId", null),
                mPreferences.getString("name", null),
                null
        );
        if (credentials.getUserName() == null || credentials.getLayerAppId() == null)
        {
            Log.d(TAG, "No stored credentials to respond to challenge with");
            return;
        }

        Log.d(TAG, "respondToChallenge: username: " + credentials.getUserName());

        try
        {
            // Post request
            String url = "https://layer-identity-provider.herokuapp.com/apps/" + credentials.getLayerAppId() + "/atlas_identities";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X_LAYER_APP_ID", credentials.getLayerAppId());

            // Credentials
            JSONObject rootObject = new JSONObject()
                    .put("nonce", nonce)
                    .put("name", credentials.getUserName());

            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

            OutputStream os = connection.getOutputStream();
            os.write(rootObject.toString().getBytes("UTF-8"));
            os.close();

            // Handle failure
            int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_CREATED)
            {
                String error = String.format("Got status %d when requesting authentication for '%s' with nonce '%s' from '%s'",
                                             statusCode,
                                             credentials.getUserName(),
                                             nonce,
                                             url
                );
                Log.e(TAG, "respondToChallenge: " + error);
                if (mCallback != null) { mCallback.onError(this, error); }
                return;
            }

            // Parse response
            InputStream in = new BufferedInputStream(connection.getInputStream());
            String result = streamToString(in);
            in.close();
            connection.disconnect();
            JSONObject json = new JSONObject(result);
            if (json.has("error"))
            {
                String error = json.getString("error");
                Log.e(TAG, "respondToChallenge: " + error);
                if (mCallback != null) { mCallback.onError(this, error); }
                return;
            }

            // Answer authentication challenge.
            String identityToken = json.optString("identity_token", null);
            Log.d(TAG, "Got identity token: " + identityToken);
            layerClient.answerAuthenticationChallenge(identityToken);
        }
        catch (Exception e)
        {
            String error = "Error when authenticating with provider: " + e.getMessage();
            Log.e(TAG, "respondToChallenge: " + error, e);
            if (mCallback != null) { mCallback.onError(this, error); }
        }
    }


    private void respondToChallenge(final LayerClient layerClient, String nonce)
    {
        Log.d(TAG, "respondToChallenge: ");
        Credentials credentials = new Credentials(
                mPreferences.getString("appId", null),
                mPreferences.getString("name", null),
                mPreferences.getString("userId", null)
        );
        if (credentials.getUserName() == null || credentials.getLayerAppId() == null || credentials.getUserId() == null)
        {
            Log.d(TAG, "No stored credentials to respond to challenge with");
            return;
        }

        Log.d(
                TAG,
                "respondToChallenge: using Handy auth server, username: " + credentials.getUserName() + " ID:" + credentials
                        .getUserId()
        );
        mDataManager.getLayerAuthToken(
                credentials.getUserId(),
                nonce,
                new DataManager.Callback<LayerResponseWrapper>()
                {
                    @Override
                    public void onSuccess(final LayerResponseWrapper response)
                    {
                        String token = response.getIdentityToken();

                        Log.d(TAG, "onSuccess: Got layer ID token back: " + token);
                        layerClient.answerAuthenticationChallenge(token);

                    }

                    @Override
                    public void onError(final DataManager.DataManagerError error)
                    {
                        Log.e(TAG, "onError: " + error.getMessage());
                        if (mCallback != null)
                        {
                            mCallback.onError(
                                    LayerAuthenticationProvider.this,
                                    error.getMessage()
                            );
                        }
                    }
                }
        );
    }

    public static class Credentials {
        private final String mLayerAppId;
        private final String mUserName;
        private final String mUserId;

        public Credentials(Uri layerAppId, String userName, String userId)
        {
            this(layerAppId == null ? null : layerAppId.getLastPathSegment(), userName, userId);
        }

        public Credentials(String layerAppId, String userName, String userId)
        {
            mLayerAppId = layerAppId == null ? null : (layerAppId.contains("/") ? layerAppId.substring(layerAppId.lastIndexOf("/") + 1) : layerAppId);
            mUserName = userName;
            mUserId = userId;
        }

        public String getUserName() {
            return mUserName;
        }

        public String getLayerAppId() {
            return mLayerAppId;
        }

        public String getUserId()
        {
            return mUserId;
        }
    }
}
