package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.crashlytics.android.Crashlytics;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Booking implements Parcelable
{
    public static final Comparator<? super Booking> COMPARATOR_DATE = new Comparator<Booking>()
    {
        @Override
        public int compare(@NonNull final Booking lhs, @NonNull final Booking rhs)
        {
            return lhs.getStartDate().compareTo(rhs.getStartDate());
        }
    };
    @SerializedName("id")
    private String mId;
    @SerializedName("booking_status")
    private int mIsPast;
    @SerializedName("booking_timezone")
    private String mBookingTimezone;
    @SerializedName("service_name")
    private String mServiceName;
    @SerializedName("service_machine")
    private String mServiceMachineName;
    @NonNull
    @SerializedName("service")
    private BookingService mService;
    @SerializedName("date_start")
    private Date mStartDate;
    @SerializedName("hours")
    private float mHours;
    @SerializedName("price")
    private float mPrice;
    @SerializedName("recurring")
    private int mRecurring; //WARNING DECEPTIVE VARIABLE NAME! THIS DOES NOT ACTUALLY INDICATE IF A BOOKING IS RECURRING!!!! - this can be 0 if it is either a non-isRecurring booking or is a booking in a isRecurring series but is not the first one, use recurring_id to check if is isRecurring
    @SerializedName("recurring_id")
    private String mRecurringId;  //This actually indicates if a booking is isRecurring, non-null/empty id
    @SerializedName("recurring_string")
    private String mRecurringInfo; //User facing display string of frequency, i.e. once, every 2 weeks, every 4 weeks
    @SerializedName("getin")
    private int mEntryType; //numeric, must keep synced against server, start using an auto deserialized enum?
    @SerializedName("getin_string")
    private String mEntryInfo;   //string descriptor of the entry type
    @SerializedName("getintxt")
    private String mExtraEntryInfo; //additional information i.e. where user will hide the key
    @SerializedName("lockbox_code")
    private String mLockboxCode; //ugly because hack
    @SerializedName("msg_to_pro")
    private String mProNote;
    @SerializedName("laundry_status")
    private LaundryStatus mLaundryStatus;
    @SerializedName("address")
    private Address mAddress;
    @SerializedName("provider")
    private Provider mProvider;
    @SerializedName("billed_status")
    private String mBilledStatus;
    @SerializedName("payment_hash")
    private ArrayList<LineItem> mPaymentInfo;
    @SerializedName("extras_info")
    private ArrayList<ExtraInfo> mExtrasInfo;
    @SerializedName("can_edit_hours") //we want them false by default
    private boolean mCanEditHours;
    @SerializedName("can_edit_frequency")
    private boolean mCanEditFrequency;
    @SerializedName("can_edit_extras")
    private boolean mCanEditExtras;
    @SerializedName("can_leave_tip")
    private boolean mCanLeaveTip;
    @SerializedName("milestones_enabled")
    private boolean mMilestonesEnabled;
    @SerializedName("instructions")
    private Instructions mInstructions;
    @SerializedName("active_booking_status")
    private ActiveBookingStatus mActiveBookingStatus;

    public String getLockboxCode()
    {
        return mLockboxCode;
    }

    public boolean canEditExtras()
    {
        return mCanEditExtras;
    }

    public String getId()
    {
        return mId;
    }

    public final void setId(final String id)
    {
        mId = id;
    }

    public final boolean isPast()
    {
        return mIsPast == 1;
    }

    final void setIsPast(final boolean isPast)
    {
        if (isPast)
        {
            mIsPast = 1;
        }
        else
        {
            mIsPast = 0;
        }
    }

    public String getBookingTimezone()
    {
        return mBookingTimezone;
    }

    public void setBookingTimezone(final String bookingTimezone)
    {
        mBookingTimezone = bookingTimezone;
    }

    public final boolean hasAssignedProvider()
    {
        return mProvider != null && mProvider.getStatus() == Provider.PROVIDER_STATUS_ASSIGNED;
    }

    public final boolean isRecurring()
    {
        return mRecurringId != null && !mRecurringId.isEmpty() && !"0".equals(mRecurringId);
    }

    public ActiveBookingStatus getActiveBookingStatus()
    {
        return mActiveBookingStatus;
    }

    public BookingService getService()
    {
        return mService;
    }

    public final String getRecurringInfo()
    {
        return mRecurringInfo;
    }

    public final String getEntryInfo()
    {
        return mEntryInfo;
    }

    public final String getExtraEntryInfo()
    {
        return mExtraEntryInfo;
    }

    public final String getProNote()
    {
        return mProNote;
    }

    public final String getServiceName()
    {
        return mServiceName;
    }

    public String getServiceMachineName()
    {
        return mServiceMachineName;
    }

    //TODO: Auto-enum these vars a la Booking.LaundryStatus . From the Service table , select distinct(machine_name) from service
    public static final String SERVICE_CLEANING = "cleaning";
    public static final String SERVICE_HOME_CLEANING = "home_cleaning";
    public static final String SERVICE_OFFICE_CLEANING = "office_cleaning";
    public static final String SERVICE_HANDYMAN = "handyman";
    public static final String SERVICE_PLUMBING = "plumbing";
    public static final String SERVICE_ELECTRICIAN = "electrician";
    public static final String SERVICE_ELECTRICAL = "electrical";
    public static final String SERVICE_PAINTING = "painting";

    public boolean canLeaveTip()
    {
        return mCanLeaveTip;
    }

    public boolean isMilestonesEnabled() { return mMilestonesEnabled; }

    public Instructions getInstructions()
    {
        return mInstructions;
    }


    /*
    Service list from api/v3/services:
    cleaning
    handyman
    home_cleaning
    office_cleaning
    hanging_pictures_shelves
    ac_repair
    furniture_assembly
    general_labor
    key_courier
    moving_help
    other_handyman_service
    unclog_drains
    faucets_replacement
    toilet_trouble
    water_heater_issues
    other_plumbing
    vacation_rental_cleanup
    mount_tv
    painting
    install_window_treatments
    install_knobs_locks
    garbage_disposal
    electrician
    light_fixtures
    ceiling_fan
    outlets
    thermostat
    other_electrical
    other_service
    vacation_rental_cleaning
    home_cleaning_job_block
    */
    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            SERVICE_CLEANING,
            SERVICE_HOME_CLEANING,
            SERVICE_OFFICE_CLEANING,
            SERVICE_HANDYMAN,
            SERVICE_PLUMBING,
            SERVICE_ELECTRICIAN,
            SERVICE_ELECTRICAL,
            SERVICE_PAINTING
            //TODO:Implement the rest of service types from above
    })
    public @interface ServiceType
    {
    }


    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            ExtrasMachineName.INSIDE_CABINETS,
            ExtrasMachineName.INSIDE_FRIDGE,
            ExtrasMachineName.INSIDE_OVEN,
            ExtrasMachineName.LAUNDRY,
            ExtrasMachineName.INTERIOR_WINDOWS,
    })
    public @interface ExtrasType
    {

    }


    public final static class ExtrasMachineName
    {
        public final static String INSIDE_CABINETS = "inside_cabinets";
        public final static String INSIDE_FRIDGE = "inside_fridge";
        public final static String INSIDE_OVEN = "inside_oven";
        public final static String LAUNDRY = "laundry";
        public final static String INTERIOR_WINDOWS = "interior_windows";
    }

    //TODO: ideally we shouldn't have this logic in the model. find/create a better place for it
    public static int getImageResourceIdForMachineName(@ExtrasType String extrasMachineName)
    {
        switch (extrasMachineName)
        {
            case Booking.ExtrasMachineName.INSIDE_CABINETS:
                return R.drawable.ic_booking_extra_cabinets;
            case Booking.ExtrasMachineName.INSIDE_FRIDGE:
                return R.drawable.ic_booking_extra_fridge;
            case Booking.ExtrasMachineName.INSIDE_OVEN:
                return R.drawable.ic_booking_extra_oven;
            case Booking.ExtrasMachineName.INTERIOR_WINDOWS:
                return R.drawable.ic_booking_extra_window;
            case Booking.ExtrasMachineName.LAUNDRY:
                return R.drawable.ic_booking_extra_laundry;
            default:
                return R.drawable.ic_booking_detail_logo;
        }
    }

    public Date getStartDate()
    {
        return mStartDate;
    }

    public final void setStartDate(final Date startDate)
    {
        mStartDate = startDate;
    }

    public Date getEndDate()
    {
        final Calendar endDate = Calendar.getInstance();
        endDate.setTime(this.getStartDate());
        //hours is a float may come back as something like 3.5, and can't add float hours to a calendar
        endDate.add(Calendar.MINUTE, (int) (60 * this.getHours()));
        return endDate.getTime();
    }

    public final float getHours()
    {
        return mHours;
    }

    final void setHours(float hours)
    {
        mHours = hours;
    }

    public final float getPrice()
    {
        return mPrice;
    }

    final void setPrice(float price)
    {
        mPrice = price;
    }

    public Address getAddress()
    {
        return mAddress;
    }

    final void setAddress(final Address address)
    {
        mAddress = address;
    }

    public Provider getProvider()
    {
        return mProvider;
    }

    final void setProvider(final Provider provider)
    {
        mProvider = provider;
    }

    public final LaundryStatus getLaundryStatus()
    {
        return mLaundryStatus;
    }

    public final String getBilledStatus()
    {
        return mBilledStatus;
    }

    public final ArrayList<LineItem> getPaymentInfo()
    {
        return mPaymentInfo;
    }

    public ArrayList<ExtraInfo> getExtrasInfo()
    {
        return mExtrasInfo;
    }

    private Booking(final Parcel in)
    {
        final String[] stringData = new String[12];
        in.readStringArray(stringData);
        mId = stringData[0];
        mServiceName = stringData[1];
        mServiceMachineName = stringData[2];

        try
        {
            mLaundryStatus = LaundryStatus.valueOf(stringData[3]);
        }
        catch (IllegalArgumentException x)
        {
            mLaundryStatus = null;
        }

        mRecurringInfo = stringData[4];
        mEntryInfo = stringData[5];
        mExtraEntryInfo = stringData[6];
        mProNote = stringData[7];
        mBilledStatus = stringData[8];
        mRecurringId = stringData[9];
        mBookingTimezone = stringData[10];
        mLockboxCode = stringData[11];

        final int[] intData = new int[2];
        in.readIntArray(intData);
        mIsPast = intData[0];
        mEntryType = intData[1];

        final float[] floatData = new float[2];
        in.readFloatArray(floatData);
        mHours = floatData[0];
        mPrice = floatData[1];

        mStartDate = new Date(in.readLong());
        mAddress = in.readParcelable(Address.class.getClassLoader());
        mProvider = (Provider) in.readSerializable();
        mActiveBookingStatus = in.readParcelable(ActiveBookingStatus.class.getClassLoader());
        mService = in.readParcelable(BookingService.class.getClassLoader());

        mPaymentInfo = new ArrayList<LineItem>();
        in.readTypedList(mPaymentInfo, LineItem.CREATOR);

        mExtrasInfo = new ArrayList<ExtraInfo>();
        in.readTypedList(mExtrasInfo, ExtraInfo.CREATOR);

        final boolean[] booleanData = new boolean[5];
        in.readBooleanArray(booleanData);

        mCanEditFrequency = booleanData[0];
        mCanEditExtras = booleanData[1];
        mCanEditHours = booleanData[2];
        mCanLeaveTip = booleanData[3];
        mMilestonesEnabled = booleanData[4];

        mInstructions = in.readParcelable(Instructions.class.getClassLoader());


    }

    public static Booking fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, Booking.class);
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags)
    {
        out.writeStringArray(new String[]
                {
                        mId,
                        mServiceName,
                        mServiceMachineName,
                        mLaundryStatus != null ? mLaundryStatus.name() : "",
                        mRecurringInfo,
                        mEntryInfo,
                        mExtraEntryInfo,
                        mProNote,
                        mBilledStatus,
                        mRecurringId,
                        mBookingTimezone,
                        mLockboxCode
                }
        );

        out.writeIntArray(new int[]{mIsPast, mEntryType});
        out.writeFloatArray(new float[]{mHours, mPrice});
        out.writeLong(mStartDate.getTime());
        out.writeParcelable(mAddress, 0);
        out.writeSerializable(mProvider);
        out.writeParcelable(mActiveBookingStatus, 0);
        out.writeParcelable(mService, 0);
        out.writeTypedList(mPaymentInfo);
        out.writeTypedList(mExtrasInfo);
        out.writeBooleanArray(new boolean[]
                {
                        mCanEditFrequency,
                        mCanEditExtras,
                        mCanEditHours,
                        mCanLeaveTip,
                        mMilestonesEnabled,
                });
        out.writeParcelable(mInstructions, 0);
    }

    @Override
    public final int describeContents()
    {
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public Booking createFromParcel(final Parcel in)
        {
            return new Booking(in);
        }

        public Booking[] newArray(final int size)
        {
            return new Booking[size];
        }
    };

    public int getEntryType()
    {
        return mEntryType;
    }

    public boolean canEditHours()
    {
        return mCanEditHours;
    }

    public boolean canEditFrequency()
    {
        return mCanEditFrequency;
    }

    /**
     * Tries to convert String representation of recurringId to Long
     *
     * @return Long value or null if the String value couldn't be converted.
     */
    @Nullable
    public Long getRecurringId()
    {
        Long longValue = null;
        try
        {
            longValue = Long.parseLong(mRecurringId);
        }
        catch (NumberFormatException nfe)
        {
            Crashlytics.log("Error converting recurringId to Long");
            Crashlytics.logException(nfe);
        }
        return longValue;
    }

    public static class Address implements Parcelable
    {
        @SerializedName("address1")
        private String address1;
        @SerializedName("address2")
        private String address2;
        @SerializedName("city")
        private String city;
        @SerializedName("state")
        private String state;
        @SerializedName("zipcode")
        private String zip;
        @SerializedName("latitude")
        private double latitude;
        @SerializedName("longitude")
        private double longitude;

        public String getAddress1()
        {
            return address1;
        }

        final void setAddress1(final String address1)
        {
            this.address1 = address1;
        }

        public final String getAddress2()
        {
            return address2;
        }

        final void setAddress2(final String address2)
        {
            this.address2 = address2;
        }

        public final String getCity()
        {
            return city;
        }

        final void setCity(final String city)
        {
            this.city = city;
        }

        public final String getState()
        {
            return state;
        }

        final void setState(final String state)
        {
            this.state = state;
        }

        public String getZip()
        {
            return zip;
        }

        final void setZip(final String zip)
        {
            this.zip = zip;
        }

        public double getLatitude()
        {
            return latitude;
        }

        public void setLatitude(final double latitude)
        {
            this.latitude = latitude;
        }

        public double getLongitude()
        {
            return longitude;
        }

        public void setLongitude(final double longitude)
        {
            this.longitude = longitude;
        }

        private Address(final Parcel in)
        {
            final String[] stringData = new String[5];
            in.readStringArray(stringData);
            address1 = stringData[0];
            address2 = stringData[1];
            city = stringData[2];
            state = stringData[3];
            zip = stringData[4];

            latitude = in.readDouble();
            longitude = in.readDouble();
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeStringArray(new String[]{address1, address2, city, state, zip});
            out.writeDouble(latitude);
            out.writeDouble(longitude);
        }

        @Override
        public final int describeContents()
        {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            public Address createFromParcel(final Parcel in)
            {
                return new Address(in);
            }

            public Address[] newArray(final int size)
            {
                return new Address[size];
            }
        };
    }


    public static final class LineItem implements Parcelable
    {
        @SerializedName("order")
        private int order;
        @SerializedName("label")
        private String label;
        @SerializedName("amount")
        private String amount;

        public final int getOrder()
        {
            return order;
        }

        public final String getLabel()
        {
            return label;
        }

        public final String getAmount()
        {
            return amount;
        }

        private LineItem(final Parcel in)
        {
            final int[] intData = new int[1];
            in.readIntArray(intData);
            order = intData[0];

            final String[] stringData = new String[2];
            in.readStringArray(stringData);
            label = stringData[0];
            amount = stringData[1];
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeIntArray(new int[]{order});
            out.writeStringArray(new String[]{label, amount});
        }

        @Override
        public final int describeContents()
        {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            public LineItem createFromParcel(final Parcel in)
            {
                return new LineItem(in);
            }

            public LineItem[] newArray(final int size)
            {
                return new LineItem[size];
            }
        };
    }


    public static class ExtraInfo implements Parcelable
    {
        @SerializedName("label")
        private String label;
        @SerializedName("image_name")
        private ExtraInfoImageName imageName;


        public enum ExtraInfoImageName
        {
            //TODO: Why is the server sending a full imageName name for something that is supposed to be bundled as part of the app? Should just send an identifier or nothing and use label
            //They are always coming back as _disabled in booking details, not sure why, to investigate? Possibly a hold over from web?
            @SerializedName("inside_cabinets_extras_disabled.png")INSIDE_CABINETS_DISABLED,
            @SerializedName("inside_fridge_extras_disabled.png")INSIDE_FRIDGE_DISABLED,
            @SerializedName("inside_oven_extras_disabled.png")INSIDE_OVEN_DISABLED,
            @SerializedName("laundry_extras_disabled.png")LAUNDRY_DISABLED,
            @SerializedName("interior_windows_extras_disabled.png")WINDOWS_DISABLED,
            @SerializedName("default.png")DEFAULT_IMAGE_NAME,
        }


        private static final Map<ExtraInfoImageName, Integer> EXTRAS_ICONS;

        static
        {
            EXTRAS_ICONS = new HashMap<>();
            EXTRAS_ICONS.put(Booking.ExtraInfo.ExtraInfoImageName.INSIDE_CABINETS_DISABLED, R.drawable.ic_booking_extra_cabinets);
            EXTRAS_ICONS.put(Booking.ExtraInfo.ExtraInfoImageName.INSIDE_FRIDGE_DISABLED, R.drawable.ic_booking_extra_fridge);
            EXTRAS_ICONS.put(Booking.ExtraInfo.ExtraInfoImageName.INSIDE_OVEN_DISABLED, R.drawable.ic_booking_extra_oven);
            EXTRAS_ICONS.put(Booking.ExtraInfo.ExtraInfoImageName.LAUNDRY_DISABLED, R.drawable.ic_booking_extra_laundry);
            EXTRAS_ICONS.put(Booking.ExtraInfo.ExtraInfoImageName.WINDOWS_DISABLED, R.drawable.ic_booking_extra_window);
            EXTRAS_ICONS.put(Booking.ExtraInfo.ExtraInfoImageName.DEFAULT_IMAGE_NAME, R.drawable.ic_booking_detail_logo);
            //TODO: Need to add missing icons like ladders and painting
        }

        public final String getLabel()
        {
            return label;
        }

        public final ExtraInfoImageName getImageName()
        {
            return imageName;
        }

        public final int getImageResource()
        {
            return getImageResource(imageName);
        }

        private final int getImageResource(ExtraInfoImageName extraInfoImageName)
        {
            if (EXTRAS_ICONS.containsKey(extraInfoImageName))
            {
                return EXTRAS_ICONS.get(extraInfoImageName);
            }
            else
            {
                Crashlytics.log("ExtraInfo::getImageResource unsupported image name : " + String.valueOf(extraInfoImageName));
                return 0;
            }
        }

        private ExtraInfo(final Parcel in)
        {
            final String[] stringData = new String[2];
            in.readStringArray(stringData);
            label = stringData[0];
            try
            {
                imageName = ExtraInfoImageName.valueOf(stringData[1]);
            }
            catch (IllegalArgumentException e)
            {
                Crashlytics.log("Could not convert string : " + stringData[1] + " to extras image name");
            }

            if (imageName == null)
            {
                imageName = ExtraInfoImageName.DEFAULT_IMAGE_NAME;
            }
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeStringArray(new String[]{
                    label,
                    imageName != null ?
                            imageName.toString()
                            : ExtraInfoImageName.DEFAULT_IMAGE_NAME.toString()
            });
        }

        @Override
        public final int describeContents()
        {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            public ExtraInfo createFromParcel(final Parcel in)
            {
                return new ExtraInfo(in);
            }

            public ExtraInfo[] newArray(final int size)
            {
                return new ExtraInfo[size];
            }
        };
    }


    public enum LaundryStatus
    {
        @SerializedName("ready_for_pickup")READY_FOR_PICKUP,
        @SerializedName("in_progress")IN_PROGRESS,
        @SerializedName("out_for_delivery")OUT_FOR_DELIVERY,
        @SerializedName("delivered")DELIVERED,
        @SerializedName("skipped")SKIPPED,
    }


    public static final int ENTRY_TYPE_WILL_BE_HOME = 0;
    public static final int ENTRY_TYPE_DOORMAN = 1;
    public static final int ENTRY_TYPE_HIDE_THE_KEYS = 2;


    public static class List extends ArrayList<Booking>
    {
        public static final String VALUE_ONLY_BOOKINGS_PAST = "past";
        public static final String VALUE_ONLY_BOOKINGS_UPCOMING = "upcoming";


        @Retention(RetentionPolicy.SOURCE)
        @StringDef({VALUE_ONLY_BOOKINGS_PAST, VALUE_ONLY_BOOKINGS_UPCOMING})
        public @interface OnlyBookingValues
        {
        }
    }


    public static class ActiveBookingStatus implements Parcelable
    {
        @SerializedName("map_enabled")
        private boolean mMapEnabled;

        @SerializedName("booking")
        private Location mBookingLocation;

        @SerializedName("provider")
        private Location mProviderLocation;

        public boolean isMapEnabled()
        {
            return mMapEnabled;
        }

        public Location getBookingLocation()
        {
            return mBookingLocation;
        }

        public Location getProviderLocation()
        {
            return mProviderLocation;
        }

        private ActiveBookingStatus(final Parcel in)
        {
            final boolean[] booleanArray = new boolean[1];
            in.readBooleanArray(booleanArray);
            mMapEnabled = booleanArray[0];
            mBookingLocation = in.readParcelable(Location.class.getClassLoader());
            mProviderLocation = in.readParcelable(Location.class.getClassLoader());

        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeBooleanArray(new boolean[]{mMapEnabled});
            out.writeParcelable(mBookingLocation, 0);
            out.writeParcelable(mProviderLocation, 0);
        }

        @Override
        public final int describeContents()
        {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            public ActiveBookingStatus createFromParcel(final Parcel in)
            {
                return new ActiveBookingStatus(in);
            }

            public ActiveBookingStatus[] newArray(final int size)
            {
                return new ActiveBookingStatus[size];
            }
        };

    }


    public static class Location implements Parcelable
    {
        //the server returns lat/long as strings
        @SerializedName("latitude")
        private String mLatitude;

        @SerializedName("longitude")
        private String mLongitude;

        @SerializedName("timestamp")
        private Date mTimeStamp;

        private Location(final Parcel in)
        {
            final String[] stringArray = new String[2];
            in.readStringArray(stringArray);
            mLatitude = stringArray[0];
            mLongitude = stringArray[1];

            long tmpDate = in.readLong();
            mTimeStamp = tmpDate == -1 ? null : new Date(tmpDate);
        }

        public String getLatitude()
        {
            return mLatitude;
        }

        public String getLongitude()
        {
            return mLongitude;
        }

        public Date getTimeStamp()
        {
            return mTimeStamp;
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeStringArray(new String[]{mLatitude, mLongitude});

            //some locations don't report time stamps
            out.writeLong(mTimeStamp != null ? mTimeStamp.getTime() : -1);
        }

        @Override
        public final int describeContents()
        {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            public Location createFromParcel(final Parcel in)
            {
                return new Location(in);
            }

            public Location[] newArray(final int size)
            {
                return new Location[size];
            }
        };
    }

}
