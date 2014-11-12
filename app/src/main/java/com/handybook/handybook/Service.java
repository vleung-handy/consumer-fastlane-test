package com.handybook.handybook;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

final class Service implements Parcelable {
    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("uniq") private String uniq;
    @SerializedName("order") private int order;
    @SerializedName("parent") private int parentId;
    @SerializedName("services") private List<Service> services;

    Service() {}

    final int getId() {
        return id;
    }

    final void setId(final int id) {
        this.id = id;
    }

    final String getName() {
        return name;
    }

    final void setName(final String name) {
        this.name = name;
    }

    final String getUniq() {
        return uniq;
    }

    final void setUniq(final String uniq) {
        this.uniq = uniq;
    }

    final int getOrder() {
        return order;
    }

    final void setOrder(final int order) {
        this.order = order;
    }

    final int getParentId() {
        return parentId;
    }

    final void setParentId(final int parentId) {
        this.parentId = parentId;
    }

    final List<Service> getServices() {
        if (services == null) services = new ArrayList<>();
        return services;
    }

    final void setServices(final List<Service> services) {
        this.services = services;
    }

    private Service(final Parcel in) {
        final String[] stringData = new String[2];
        in.readStringArray(stringData);
        name = stringData[0];
        uniq = stringData[1];

        final int[] intData = new int[3];
        in.readIntArray(intData);
        order = intData[0];
        parentId = intData[1];
        id = intData[2];

        services = new ArrayList<>();
        in.readTypedList(services, Service.CREATOR);
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{ name, uniq });
        out.writeIntArray(new int[]{ order, parentId, id });
        out.writeTypedList(services);
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