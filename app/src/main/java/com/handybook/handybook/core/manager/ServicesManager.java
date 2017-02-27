package com.handybook.handybook.core.manager;

import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handybook.handybook.booking.BookingEvent;
import com.handybook.handybook.booking.model.Service;
import com.handybook.handybook.configuration.manager.ConfigurationManager;
import com.handybook.handybook.core.constant.PrefsKey;
import com.handybook.handybook.core.data.DataManager;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ServicesManager {

    private static final String TAG = ServicesManager.class.getSimpleName();
    private static final String CACHE_KEY = "servicesJson";

    private DataManager mDataManager;
    private Bus mBus;
    private final SecurePreferencesManager mSecurePreferencesManager;
    private final ConfigurationManager mConfigurationManager;
    private final SessionManager mSessionManager;

    @Inject
    public ServicesManager(
            final DataManager dataManager,
            final Bus bus,
            final SecurePreferencesManager securePreferencesManager,
            final ConfigurationManager configurationManager,
            final SessionManager sessionManager
    ) {
        mDataManager = dataManager;
        mSecurePreferencesManager = securePreferencesManager;
        mConfigurationManager = configurationManager;
        mSessionManager = sessionManager;
        mBus = bus;
        mBus.register(this);
    }

    public Service getServiceNameByServiceId(int serviceId) {
        if (getCachedServices() == null) { return null; }

        for (Service service : getCachedServices()) {
            if (service.getId() == serviceId) { return service; }
        }

        return null;
    }

    @Subscribe
    public void onRequestCachedServices(final BookingEvent.RequestCachedServices event) {
        mBus.post(new BookingEvent.ReceiveCachedServicesSuccess(getCachedServices()));
    }

    /**
     * Will get the list of Home Service Menu items as well as subcategories
     * @param event
     */
    @Subscribe
    public void onRequestServices(final BookingEvent.RequestServices event) {
        requestServices(event.getZip(), true);
    }

    public void requestServices(final String zip, boolean useCacheIfExist) {
        //If this is old onboarding then handle old way
        if (useCacheIfExist) {
            //If old way, check if cache is null
            final List<Service> cachedServices = getCachedServices();

            if (cachedServices != null) {
                //if there is a cached version, we notify right away.
                mBus.post(new BookingEvent.ReceiveCachedServicesSuccess(cachedServices));
            }
        }

        mDataManager.getServiceMenu(new DataManager.Callback<JSONArray>() {
            @Override
            public void onSuccess(final JSONArray menuStructure) {
                //If this is old onboarding then handle old way
                if (!mConfigurationManager.getPersistentConfiguration().isOnboardingV2Enabled()
                    && menuStructure == null
                    && getCachedServices() == null) {
                    //we only notify of error if there is not already a cached version returned.
                    onError(new DataManager.DataManagerError(DataManager.Type.SERVER));
                    return;
                }

                final List<Service> servicesMenu = new ArrayList<>();
                final Map<Integer, Service> menuMap = new HashMap<>();

                //Handle the Home service menu response
                handleServicesMenuResponse(servicesMenu, menuMap, menuStructure);

                //Request the services subcategory list after we get the service menu
                requestServices(servicesMenu, menuMap, zip);
            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new BookingEvent.ReceiveServicesError(error));
            }
        });
    }

    /**
     * Gets all services including subcategories
     */
    private void requestServices(
            final List<Service> servicesMenu,
            final Map<Integer, Service> menuMap,
            final String zip
    ) {
        mDataManager.getServices(zip, new DataManager.Callback<JSONArray>() {
            @Override
            public void onSuccess(final JSONArray servicesListJson) {
                //If this is old onboarding then handle old way
                if (!mConfigurationManager.getPersistentConfiguration().isOnboardingV2Enabled()
                    && servicesListJson == null) {
                    //we only notify of error if there is not already a cached version returned.
                    onError(new DataManager.DataManagerError(DataManager.Type.SERVER));
                    return;
                }

                List<Service> serviceListWithSubcategories;

                if (servicesListJson == null) {
                    serviceListWithSubcategories = new ArrayList<>();
                }
                else {
                    List<Service> serviceList = new Gson().fromJson(
                            servicesListJson.toString(),
                            new TypeToken<List<Service>>() {}.getType()
                    );
                    serviceListWithSubcategories = handleServicesResponse(
                            servicesMenu,
                            menuMap,
                            serviceList
                    );

                    //updates the cache with fresh version of services
                    mSessionManager.putToSessionCache(CACHE_KEY, new Gson()
                            .toJsonTree(servicesMenu).getAsJsonArray().toString());
                }

                mBus.post(new BookingEvent.ReceiveServicesSuccess(
                        serviceListWithSubcategories,
                        zip
                ));

            }

            @Override
            public void onError(final DataManager.DataManagerError error) {
                mBus.post(new BookingEvent.ReceiveServicesError(error));
            }
        });

    }

    /**
     * handle the response for the most common services.
     * @param servicesMenu
     * @param menuMap
     * @param menuStructure
     */
    private void handleServicesMenuResponse(
            List<Service> servicesMenu,
            Map<Integer, Service> menuMap,
            JSONArray menuStructure
    ) {
        for (int i = 0; i <= menuStructure.length(); i++) {
            final JSONObject obj = menuStructure.optJSONObject(i);

            if (obj != null) {
                final String name = obj.isNull("name") ? null : obj.optString("name", null);
                final int ignore = obj.optInt("ignore", 1);

                if (name == null || ignore == 1) {
                    continue;
                }

                final Service service = new Service();
                service.setUniq(obj.isNull("uniq") ? null : obj.optString("uniq"));
                service.setName(obj.isNull("name") ? null : obj.optString("name"));
                service.setOrder(obj.optInt("order", 0));
                service.setId(obj.optInt("id", -1));
                servicesMenu.add(service);
                menuMap.put(service.getId(), service);
            }
        }
    }

    /**
     * Handle response for the complete services items and split them into the subcategories
     * @param mostCommonServices
     * @param menuMap
     * @param jsonServices
     * @return
     */
    private List<Service> handleServicesResponse(
            List<Service> mostCommonServices,
            Map<Integer, Service> menuMap,
            List<Service> jsonServices
    ) {
        Map<Integer, Service> knownParentsMenuMap = new HashMap<>();
        //This should never happen, but this is just for backup
        //If the servicesMenu isn't set, this means that we get it from the jsonServices
        if (mostCommonServices == null) {
            //Set up the new service menu that only has services with sub-categories
            mostCommonServices = new ArrayList<>();
            for (Service service : jsonServices) {
                if (!service.isIgnore() && service.getParentId() == 0) {
                    mostCommonServices.add(service);
                    knownParentsMenuMap.put(service.getId(), service);
                }
            }
        }

        //Loops through the services and only create a menu with supported services
        //Also adds the service if it's a child of the parent service
        for (Service service : jsonServices) {
            //if the service is not to be ignored
            if (!service.isIgnore()) {
                //The service list can be the top level service or can be a child service
                if (menuMap.containsKey(service.getId())) {
                    knownParentsMenuMap.put(service.getId(), service);
                }
                else if (menuMap.containsKey(service.getParentId())) {
                    Service parentService = menuMap.get(service.getParentId());
                    parentService.addChildService(service);
                    knownParentsMenuMap.put(parentService.getId(), parentService);
                }
            }
        }

        //Loop through all child services and sort them
        for (final Service parentService : knownParentsMenuMap.values()) {
            Collections.sort(parentService.getChildServices(), new Comparator<Service>() {
                @Override
                public int compare(final Service lhs, final Service rhs) {
                    return lhs.getOrder() - rhs.getOrder();
                }
            });
        }

        //Sort the new menu list of most common services
        List<Service> newMostCommonServices = new ArrayList<>(knownParentsMenuMap.values());
        Collections.sort(newMostCommonServices, new Comparator<Service>() {
            @Override
            public int compare(final Service lhs, final Service rhs) {
                return lhs.getOrder() - rhs.getOrder();
            }
        });

        //we only notify of error if there is not already a cached version returned.
        return newMostCommonServices;
    }

    @Nullable
    public List<Service> getCachedServices() {
        String cachedServicesJson;
        //If this is the old way, then we use the cached services, otherwise we use the session cache
        if (mConfigurationManager.getPersistentConfiguration().isOnboardingV2Enabled()) {
            cachedServicesJson = (String) mSessionManager.getFromSessionCache(CACHE_KEY);
        }
        else {
            cachedServicesJson = mSecurePreferencesManager.getString(PrefsKey.CACHED_SERVICES);
        }

        List<Service> cachedServices = null;
        if (cachedServicesJson != null) {
            try {
                cachedServices = new Gson().fromJson(
                        cachedServicesJson,
                        new TypeToken<List<Service>>() {
                        }.getType()
                );
            }
            catch (Exception e) {
                //if there is ever an error parsing this, fall out and let it create a new set
                Crashlytics.log(TAG + " error when deserializing JSON:" + cachedServicesJson);
                Crashlytics.logException(e);
            }
        }

        return cachedServices;
    }
}
