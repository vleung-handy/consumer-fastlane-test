package com.handybook.handybook.core.ui.descriptor;

import com.crashlytics.android.Crashlytics;
import com.handybook.handybook.R;

public enum ServiceDescriptor {
    HANGING_PICTURES_SHELVES(
            R.string.hanging_items,
            R.string.hanging_items_service_description,
            R.drawable.ic_service_handyman_pictures
    ),
    AC_REPAIR(
            R.string.ac_installation,
            R.string.ac_installation_service_description,
            R.drawable.ic_service_handyman_ac
    ),
    FURNITURE_ASSEMBLY(
            R.string.furniture_assembly,
            R.string.furniture_assembly_service_description,
            R.drawable.ic_service_handyman_furniture
    ),
    MOVING_HELP(
            R.string.moving_help,
            R.string.moving_help_service_description,
            R.drawable.ic_service_handyman_moving
    ),
    MOUNT_TV(
            R.string.tv_mounting,
            R.string.tv_mounting_service_description,
            R.drawable.ic_service_handyman_tv
    ),
    INSTALL_WINDOW_TREATMENTS(
            R.string.window_treatments,
            R.string.window_treatments_service_description,
            R.drawable.ic_service_handyman_window
    ),
    INSTALL_KNOBS_LOCKS(
            R.string.knobs_locks,
            R.string.knobs_locks_service_description,
            R.drawable.ic_service_handyman_locks
    ),
    OTHER_HANDYMAN_SERVICE(
            R.string.other_handyman,
            R.string.other_handyman_service_description,
            R.drawable.ic_service_handyman_custom
    ),

    UNCLOG_DRAINS(
            R.string.drains,
            R.string.drains_service_description,
            R.drawable.ic_service_plumbing_drains
    ),
    FAUCETS_REPLACEMENT(
            R.string.faucets,
            R.string.faucets_service_description,
            R.drawable.ic_service_plumbing_faucet
    ),
    TOILET_TROUBLE(
            R.string.toilets,
            R.string.toilets_service_description,
            R.drawable.ic_service_plumbing_toilet
    ),
    GARBAGE_DISPOSAL(
            R.string.garbage_disposals,
            R.string.garbage_disposals_service_description,
            R.drawable.ic_service_plumbing_garbage
    ),
    OTHER_PLUMBING(
            R.string.other_plumbing,
            R.string.other_plumbing_service_description,
            R.drawable.ic_service_plumbing_custom
    ),

    LIGHT_FIXTURES(
            R.string.light_fixtures,
            R.string.light_fixtures_service_description,
            R.drawable.ic_service_electrical_lights
    ),
    CEILING_FAN(
            R.string.ceiling_fan,
            R.string.ceiling_fan_service_description,
            R.drawable.ic_service_electrical_fan
    ),
    OUTLETS(
            R.string.outlets,
            R.string.outlets_service_description,
            R.drawable.ic_service_electrical_outlet
    ),
    OTHER_ELECTRICAL(
            R.string.other_electrical,
            R.string.other_electrical_service_description,
            R.drawable.ic_service_electrical_custom
    ),;

    private final int mTitle;
    private final int mDescription;
    private final int mIcon;

    ServiceDescriptor(int title, int description, int icon) {
        mTitle = title;
        mDescription = description;
        mIcon = icon;
    }

    public int getTitle() {
        return mTitle;
    }

    public int getDescription() {
        return mDescription;
    }

    public int getIcon() {
        return mIcon;
    }

    public static boolean hasValueOf(String name) {
        try {
            return ServiceDescriptor.valueOf(name) != null;
        }
        catch (IllegalArgumentException e) {
            Crashlytics.logException(new RuntimeException("Cannot find service type: " + name));
            return false;
        }

    }
}
