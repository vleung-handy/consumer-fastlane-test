package com.handybook.handybook.core.data;

import android.content.Context;
import android.os.Build;
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
import com.handybook.handybook.core.User;
import com.handybook.handybook.core.UserManager;
import com.handybook.handybook.library.util.PropertiesReader;
import com.handybook.handybook.library.util.SystemUtils;

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

public class DynamicBaseUrlServiceProvider {

    private Context mContext;
    private UserManager mUserManager;
    private Properties mConfigProperties;

    private Retrofit mRetrofit;
    private HandyRetrofit2Service mHandyRetrofit2Service;

    private UrlResolver mServiceUrlResolver;

    public DynamicBaseUrlServiceProvider(
            @NonNull Context context,
            @NonNull UrlResolver serviceUrlResolver,
            @NonNull UserManager userManager
    ) {

        mContext = context;
        mUserManager = userManager;
        mServiceUrlResolver = serviceUrlResolver;

        mConfigProperties = PropertiesReader.getProperties(mContext, "config.properties");

        initializeRetrofit();
    }

    @NonNull
    public HandyRetrofit2Service getService() {
        String currentServiceUrl = mRetrofit.baseUrl().url().toString();
        String resolvedServiceUrl = mServiceUrlResolver.getUrl();

        //current service url is different from the resolved service url
        if (!currentServiceUrl.equalsIgnoreCase(resolvedServiceUrl)) {

            //change the base url of Retrofit and rebuild the service
            mRetrofit = mRetrofit.newBuilder().baseUrl(resolvedServiceUrl).build();
            mHandyRetrofit2Service = mRetrofit.create(HandyRetrofit2Service.class);
        }
        else if (mHandyRetrofit2Service == null) {
            mHandyRetrofit2Service = mRetrofit.create(HandyRetrofit2Service.class);
        }
        //else unchanged. don't rebuild

        return mHandyRetrofit2Service;
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
                .baseUrl(mServiceUrlResolver.getUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClientBuilder.build())
                .build();
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
                        .addQueryParameter("app_device_id", SystemUtils.getDeviceId(mContext))
                        .addQueryParameter("app_device_model", SystemUtils.getDeviceModel())
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
}