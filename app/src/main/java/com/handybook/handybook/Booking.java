package com.handybook.handybook;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

final class Booking implements Parcelable {
    @SerializedName("id") private String id;
    @SerializedName("booking_status") private int isPast;
    @SerializedName("service_name") private String service;
    @SerializedName("date_start") private Date startDate;
    @SerializedName("hours") private float hours;
    @SerializedName("price") private float price;
    @SerializedName("address") private Address address;
    @SerializedName("provider") private Provider provider;

    final String getId() {
        return id;
    }

    final void setId(final String id) {
        this.id = id;
    }

    final boolean isPast() {
        return isPast == 1;
    }

    final void setIsPast(final boolean isPast) {
        if (isPast) this.isPast = 1;
        else this.isPast = 0;
    }

    final String getService() {
        return service;
    }

    final void setService(final String service) {
        this.service = service;
    }

    final Date getStartDate() {
        return startDate;
    }

    final void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    final float getHours() {
        return hours;
    }

    final void setHours(float hours) {
        this.hours = hours;
    }

    final float getPrice() {
        return price;
    }

    final void setPrice(float price) {
        this.price = price;
    }

    final Address getAddress() {
        return address;
    }

    final void setAddress(final Address address) {
        this.address = address;
    }

    final Provider getProvider() {
        return provider;
    }

    final void setProvider(final Provider provider) {
        this.provider = provider;
    }

    private Booking(final Parcel in) {
        final String[] stringData = new String[2];
        in.readStringArray(stringData);
        id = stringData[0];
        service = stringData[1];

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
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{ id, service});
        out.writeIntArray(new int[]{ isPast });
        out.writeFloatArray(new float[]{ hours, price });
        out.writeLong(startDate.getTime());
        out.writeParcelable(address, 0);
        out.writeParcelable(provider, 0);
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Booking createFromParcel(final Parcel in) {
            return new Booking(in);
        }
        public Booking[] newArray(final int size) {
            return new Booking[size];
        }
    };

    static final class Address implements Parcelable {
        @SerializedName("address1") private String address1;
        @SerializedName("address2") private String address2;
        @SerializedName("city") private String city;
        @SerializedName("state") private String state;
        @SerializedName("zipcode") private String zip;

        final String getAddress1() {
            return address1;
        }

        final void setAddress1(final String address1) {
            this.address1 = address1;
        }

        final String getAddress2() {
            return address2;
        }

        final void setAddress2(final String address2) {
            this.address2 = address2;
        }

        final String getCity() {
            return city;
        }

        final void setCity(final String city) {
            this.city = city;
        }

        final String getState() {
            return state;
        }

        final void setState(final String state) {
            this.state = state;
        }

        final String getZip() {
            return zip;
        }

        final void setZip(final String zip) {
            this.zip = zip;
        }

        private Address(final Parcel in) {
            final String[] stringData = new String[5];
            in.readStringArray(stringData);
            address1 = stringData[0];
            address2 = stringData[1];
            city = stringData[2];
            state = stringData[3];
            zip = stringData[4];
        }

        @Override
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeStringArray(new String[]{ address1, address2, city, state, zip });
        }

        @Override
        public final int describeContents(){
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Address createFromParcel(final Parcel in) {
                return new Address(in);
            }
            public Address[] newArray(final int size) {
                return new Address[size];
            }
        };
    }

    static final class Provider implements Parcelable {
        @SerializedName("status") private int status;
        @SerializedName("first_name") private String firstName;
        @SerializedName("last_name") private String lastName;
        @SerializedName("phone") private String phone;

        final int getStatus() {
            return status;
        }

        final void setStatus(final int status) {
            this.status = status;
        }

        final String getFirstName() {
            return firstName;
        }

        final void setFirstName(final String firstName) {
            this.firstName = firstName;
        }

        final String getLastName() {
            return lastName;
        }

        final void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        final String getPhone() {
            return phone;
        }

        final void setPhone(final String phone) {
            this.phone = phone;
        }

        private Provider(final Parcel in) {
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
        public final void writeToParcel(final Parcel out, final int flags) {
            out.writeIntArray(new int[]{ status });
            out.writeStringArray(new String[]{ firstName, lastName, phone });
        }

        @Override
        public final int describeContents(){
            return 0;
        }

        public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
            public Provider createFromParcel(final Parcel in) {
                return new Provider(in);
            }
            public Provider[] newArray(final int size) {
                return new Provider[size];
            }
        };
    }
}