package com.handybook.handybook.core.data;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handybook.handybook.BuildConfig;
import com.handybook.handybook.booking.model.BookingPostInfo;
import com.handybook.handybook.booking.model.BookingQuote;
import com.handybook.handybook.booking.model.BookingRequest;
import com.handybook.handybook.booking.model.BookingTransaction;
import com.handybook.handybook.core.BaseApplication;
import com.handybook.handybook.core.EnvironmentModifier;
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.library.util.PropertiesReader;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.handybook.handybook.library.util.DateTimeUtils.UNIVERSAL_DATE_FORMAT;

/**
 * for Retrofit2 only
 */
public class DynamicBaseUrlServiceProvider {

    private Context mContext;
    private EnvironmentModifier mEnvironmentModifier;
    private UserManager mUserManager;
    private Properties mConfigProperties;

    private String mApiEndpoint;
    private String mApiEndpointNamespace;
    private String mApiEndpointLocal;

    private String mBaseUrl;
    private String mBaseUrlNamespace;
    private String mBaseUrlLocal;

    private Retrofit mRetrofit;
    private HandyRetrofit2Service mHandyRetrofit2Service;

    public DynamicBaseUrlServiceProvider(
            @NonNull Context context,
            @NonNull EnvironmentModifier environmentModifier,
            @NonNull UserManager userManager
    ) {

        mContext = context;
        mUserManager = userManager;
        mEnvironmentModifier = environmentModifier;

        mConfigProperties = PropertiesReader.getProperties(mContext, "config.properties");

        mApiEndpoint = mConfigProperties.getProperty("api_endpoint");
        mApiEndpointNamespace = mConfigProperties.getProperty("api_endpoint_namespace");
        mApiEndpointLocal = mConfigProperties.getProperty("api_endpoint_local");

        mBaseUrl = mConfigProperties.getProperty("base_url");
        mBaseUrlNamespace = mConfigProperties.getProperty("base_url_namespace");
        mBaseUrlLocal = mConfigProperties.getProperty("base_url_local");

        initializeRetrofit();
    }

    @NonNull
    public HandyRetrofit2Service getService() {
        if (!mRetrofit.baseUrl().url().toString().equalsIgnoreCase(getBaseUrl())) {
            mRetrofit = mRetrofit.newBuilder().baseUrl(getBaseUrl()).build();
            mHandyRetrofit2Service = mRetrofit.create(HandyRetrofit2Service.class);
        }
        //else unchanged
        return mHandyRetrofit2Service;
    }

    private String getBaseUrl() {
        String apiUrl;
        if (mEnvironmentModifier.isNamespace()) {
            apiUrl = mApiEndpointNamespace.replace(
                    "#",
                    mEnvironmentModifier.getEnvironmentPrefix()
            );
        }
        else if (mEnvironmentModifier.isLocal()) {
            apiUrl = mApiEndpointLocal.replace("#", mEnvironmentModifier.getEnvironmentPrefix());
        }
        else {
            apiUrl = mApiEndpoint;
        }
        return apiUrl;
    }

    private void initializeRetrofit() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okhttp3.OkHttpClient.Builder httpClientBuilder = new okhttp3.OkHttpClient
                .Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(getHttpRequestInterceptor())
                .addInterceptor(httpLoggingInterceptor);

        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD) && !BuildConfig.DEBUG) {
            httpClientBuilder.certificatePinner(
                    new okhttp3.CertificatePinner.Builder().add(
                            mConfigProperties.getProperty("hostname"),
                            "sha1/tbHJQrYmt+5isj5s44sk794iYFc=",
                            "sha1/SXxoaOSEzPC6BgGmxAt/EAcsajw=",
                            "sha1/blhOM3W9V/bVQhsWAcLYwPU6n24=",
                            "sha1/T5x9IXmcrQ7YuQxXnxoCmeeQ84c="
                    ).build());
        }

        Gson gson = getGson();

        mRetrofit = new Retrofit
                .Builder()
                .baseUrl(getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();
        mHandyRetrofit2Service = mRetrofit.create(HandyRetrofit2Service.class);
    }

    @NonNull
    private Interceptor getHttpRequestInterceptor() {
        final String username = mConfigProperties.getProperty("api_username");
        String password = mConfigProperties.getProperty("api_password_internal");
        if (BuildConfig.FLAVOR.equals(BaseApplication.FLAVOR_PROD)) {
            password = mConfigProperties.getProperty("api_password");
        }
        final String auth = "Basic " + Base64.encodeToString(
                (username + ":" + password).getBytes(),
                Base64.NO_WRAP
        );
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                okhttp3.HttpUrl.Builder urlBuilder = originalHttpUrl
                        .newBuilder()
                        .addQueryParameter("client", "android")
                        .addQueryParameter("app_version", BuildConfig.VERSION_NAME)
                        .addQueryParameter("api_sub_version", "6.0")
                        .addQueryParameter("app_device_id", getDeviceId())
                        .addQueryParameter("app_device_model", getDeviceName())
                        .addQueryParameter("app_device_os", Build.VERSION.RELEASE)
                        .addQueryParameter(
                                "app_version_code",
                                String.valueOf(BuildConfig.VERSION_CODE)
                        );
                final User user = mUserManager.getCurrentUser();
                if (user != null) {
                    urlBuilder.addQueryParameter("app_user_id", user.getId());
                }
                // Request customization: add request headers
                Request.Builder requestBuilder = original
                        .newBuilder()
                        .addHeader("Authorization", auth)
                        .addHeader("Accept", "application/json")
                        .url(urlBuilder.build());
                if (user != null && user.getAuthToken() != null) {
                    requestBuilder.addHeader("X-Auth-Token", user.getAuthToken());
                }

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        };
    }

    @NonNull
    private Gson getGson() {
        return new GsonBuilder()
                .setDateFormat(UNIVERSAL_DATE_FORMAT)
                .setExclusionStrategies(
                        BookingRequest.getExclusionStrategy()
                )
                .registerTypeAdapter(
                        BookingRequest.class,
                        new BookingRequest.BookingRequestApiSerializer()
                )
                .setExclusionStrategies(
                        BookingQuote
                                .getExclusionStrategy())
                .registerTypeAdapter(
                        BookingQuote.class,
                        new BookingQuote.BookingQuoteSerializer()
                )
                .setExclusionStrategies(
                        BookingPostInfo
                                .getExclusionStrategy())
                .registerTypeAdapter(
                        BookingPostInfo.class,
                        new BookingPostInfo.BookingPostInfoSerializer()
                )
                .setExclusionStrategies(
                        BookingTransaction
                                .getExclusionStrategy())
                .registerTypeAdapter(
                        BookingTransaction.class,
                        new BookingTransaction.BookingTransactionSerializer()
                )
                .setExclusionStrategies(
                        User.getExclusionStrategy())
                .registerTypeAdapter(
                        User.class,
                        new User.UserSerializer()
                )
                .create();
    }

    //FIXME put in utils
    private String getDeviceId() {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //FIXME put in utils
    private String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;

        if (model.startsWith(manufacturer)) {
            return model;
        }
        else {
            return manufacturer + " " + model;
        }
    }
}