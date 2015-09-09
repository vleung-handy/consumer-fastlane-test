package com.handybook.handybook.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public final class Booking implements Parcelable
{
    @SerializedName("id")
    private String id;
    @SerializedName("booking_status")
    private int isPast;   //previously isPast
    @SerializedName("service_name")
    private String service;
    @SerializedName("date_start")
    private Date startDate;
    @SerializedName("hours")
    private float hours;
    @SerializedName("price")
    private float price;
    @SerializedName("recurring")
    private int recurring; //THIS DOES NOT ACTUALLY INDICATE IF A BOOKING IS RECURRING!!!! - this can be 0 if it is either a non-isRecurring booking or is a booking in a isRecurring series but is not the first one, use recurring_id to check if is isRecurring
    @SerializedName("recurring_id")
    private String recurringId;  //This actually indicates if a booking is isRecurring, non-null/empty id
    @SerializedName("recurring_string")
    private String recurringInfo;
    @SerializedName("getin_string")
    private String entryInfo;
    @SerializedName("getintxt")
    private String extraEntryInfo;
    @SerializedName("msg_to_pro")
    private String proNote;
    @SerializedName("laundry_status")
    private LaundryStatus laundryStatus;
    @SerializedName("address")
    private Address address;
    @SerializedName("provider")
    private Provider provider;
    @SerializedName("billed_status")
    private String billedStatus;
    @SerializedName("payment_hash")
    private ArrayList<LineItem> paymentInfo;
    @SerializedName("extras_info")
    private ArrayList<ExtraInfo> extrasInfo;

    public final String getId()
    {
        return id;
    }

    public final void setId(final String id)
    {
        this.id = id;
    }

    public final boolean isPast()
    {
        return isPast == 1;
    }

    final void setIsPast(final boolean isPast)
    {
        if (isPast)
        {
            this.isPast = 1;
        }
        else
        {
            this.isPast = 0;
        }
    }

    public final boolean isRecurring()
    {
        return recurringId != null && !recurringId.isEmpty();
    }

    public final String getRecurringInfo()
    {
        return recurringInfo;
    }

    public final String getEntryInfo()
    {
        return entryInfo;
    }

    public final String getExtraEntryInfo()
    {
        return extraEntryInfo;
    }

    public final String getProNote()
    {
        return proNote;
    }

    public final String getService()
    {
        return service;
    }

    final void setService(final String service)
    {
        this.service = service;
    }

    public final Date getStartDate()
    {
        return startDate;
    }

    public final void setStartDate(final Date startDate)
    {
        this.startDate = startDate;
    }

    public final float getHours()
    {
        return hours;
    }

    final void setHours(float hours)
    {
        this.hours = hours;
    }

    public final float getPrice()
    {
        return price;
    }

    final void setPrice(float price)
    {
        this.price = price;
    }

    public final Address getAddress()
    {
        return address;
    }

    final void setAddress(final Address address)
    {
        this.address = address;
    }

    public final Provider getProvider()
    {
        return provider;
    }

    final void setProvider(final Provider provider)
    {
        this.provider = provider;
    }

    public final LaundryStatus getLaundryStatus()
    {
        return laundryStatus;
    }

    public final String getBilledStatus()
    {
        return billedStatus;
    }

    public final ArrayList<LineItem> getPaymentInfo()
    {
        return paymentInfo;
    }

    public final ArrayList<ExtraInfo> getExtrasInfo()
    {
        return extrasInfo;
    }

    private Booking(final Parcel in)
    {
        final String[] stringData = new String[9];
        in.readStringArray(stringData);
        id = stringData[0];
        service = stringData[1];

        try
        {
            laundryStatus = LaundryStatus.valueOf(stringData[2]);
        } catch (IllegalArgumentException x)
        {
            laundryStatus = null;
        }

        recurringInfo = stringData[3];
        entryInfo = stringData[4];
        extraEntryInfo = stringData[5];
        proNote = stringData[6];
        billedStatus = stringData[7];
        recurringId = stringData[8];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        isPast = intData[0];

        final float[] floatData = new float[2];
        in.readFloatArray(floatData);
        hours = floatData[0];
        price = floatData[1];

        startDate = new Date(in.readLong());
        address = in.readParcelable(Address.class.getClassLoader());
        provider = in.readParcelable(Provider.class.getClassLoader());

        paymentInfo = new ArrayList<LineItem>();
        in.readTypedList(paymentInfo, LineItem.CREATOR);

        extrasInfo = new ArrayList<ExtraInfo>();
        in.readTypedList(extrasInfo, ExtraInfo.CREATOR);
    }

    public static Booking fromJson(final String json)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, Booking.class);
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags)
    {
        out.writeStringArray(new String[]{id, service, laundryStatus != null
                ? laundryStatus.name() : "", recurringInfo, entryInfo, extraEntryInfo, proNote,
                billedStatus, recurringId});

        out.writeIntArray(new int[]{isPast});
        out.writeFloatArray(new float[]{hours, price});
        out.writeLong(startDate.getTime());
        out.writeParcelable(address, 0);
        out.writeParcelable(provider, 0);
        out.writeTypedList(paymentInfo);
        out.writeTypedList(extrasInfo);
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

    public static final class Address implements Parcelable
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

        public final String getAddress1()
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

        public final String getZip()
        {
            return zip;
        }

        final void setZip(final String zip)
        {
            this.zip = zip;
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
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeStringArray(new String[]{address1, address2, city, state, zip});
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

    public static final class Provider implements Parcelable
    {
        @SerializedName("status")
        private int status;
        @SerializedName("first_name")
        private String firstName;
        @SerializedName("last_name")
        private String lastName;
        @SerializedName("phone")
        private String phone;

        public final int getStatus()
        {
            return status;
        }

        final void setStatus(final int status)
        {
            this.status = status;
        }

        public final String getFirstName()
        {
            return firstName;
        }

        final void setFirstName(final String firstName)
        {
            this.firstName = firstName;
        }

        public final String getLastName()
        {
            return lastName;
        }

        final void setLastName(final String lastName)
        {
            this.lastName = lastName;
        }

        public final String getPhone()
        {
            return phone;
        }

        final void setPhone(final String phone)
        {
            this.phone = phone;
        }

        private Provider(final Parcel in)
        {
            final int[] intData = new int[1];
            in.readIntArray(intData);
            status = intData[0];

            final String[] stringData = new String[3];
            in.readStringArray(stringData);
            firstName = stringData[0];
            lastName = stringData[1];
            phone = stringData[2];
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeIntArray(new int[]{status});
            out.writeStringArray(new String[]{firstName, lastName, phone});
        }

        @Override
        public final int describeContents()
        {
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
        {
            public Provider createFromParcel(final Parcel in)
            {
                return new Provider(in);
            }

            public Provider[] newArray(final int size)
            {
                return new Provider[size];
            }
        };

        public static final int PROVIDER_STATUS_ASSIGNED = 3; //TODO: Not sure what this is, just conjecturing based on code

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

    public static final class ExtraInfo implements Parcelable
    {
        @SerializedName("label")
        private String label;
        @SerializedName("image_name")
        private String image;

        public final String getLabel()
        {
            return label;
        }

        public final String getImage()
        {
            return image;
        }

        private ExtraInfo(final Parcel in)
        {
            final String[] stringData = new String[2];
            in.readStringArray(stringData);
            label = stringData[0];
            image = stringData[1];
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags)
        {
            out.writeStringArray(new String[]{label, image});
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
        @SerializedName("skipped")SKIPPED
    }
}
