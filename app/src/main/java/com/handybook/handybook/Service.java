package com.handybook.handybook;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

final class Service implements Parcelable {
    @SerializedName("name") private String name;
    @SerializedName("order") private int order;

    Service() {}

    final String getName() {
        return name;
    }

    final void setName(final String name) {
        this.name = name;
    }

    final int getOrder() {
        return order;
    }

    final void setOrder(final int order) {
        this.order = order;
    }

    private Service(final Parcel in) {
        final String[] stringData = new String[1];
        in.readStringArray(stringData);
        name = stringData[0];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        order = intData[0];
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{ name });
        out.writeIntArray(new int[]{ order });
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public Service createFromParcel(final Parcel in) {
            return new Service(in);
        }
        public Service[] newArray(final int size) {
            return new Service[size];
        }
    };
}
