package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Service implements Parcelable {

    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("order") private int order;
    @SerializedName("parent") private int parentId;
    @SerializedName("machine_name") private String mMachineName;
    @SerializedName("no_show") private boolean isNoShow;
    @SerializedName("ignore") private boolean isIgnore;

    //This is used only for the service/common endpoint response
    @SerializedName("uniq")
    private String mUniq;

    private List<Service> services;
    public static String PREFIX_CLEAN_CONSTANT = "clean";

    public Service() {}

    public final int getId() {
        return id;
    }

    public final void setId(final int id) {
        this.id = id;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * Will return machine name if uniq was null
     * @return
     */
    @NonNull
    public String getUniq() {
        return mUniq == null ? mMachineName : mUniq;
    }

    public final void setUniq(final String uniq) {
        this.mUniq = uniq;
    }

    public final int getOrder() {
        return order;
    }

    public final void setOrder(final int order) {
        this.order = order;
    }

    public final int getParentId() {
        return parentId;
    }

    public final void setParentId(final int parentId) {
        this.parentId = parentId;
    }

    public boolean isNoShow() {
        return isNoShow;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public void addChildService(Service service) {
        services = getChildServices();
        services.add(service);
    }

    public List<Service> getChildServices() {
        if (services == null) { services = new ArrayList<>(); }
        return services;
    }

    public boolean isCleaning() {
        if (mMachineName != null) {
            return mMachineName.toLowerCase().contains(PREFIX_CLEAN_CONSTANT);
        }

        if (name != null) {
            return name.toLowerCase().contains(PREFIX_CLEAN_CONSTANT);
        }

        return false;
    }

    public final void setServices(final List<Service> services) {
        this.services = services;
    }

    private Service(final Parcel in) {
        final String[] stringData = new String[3];
        in.readStringArray(stringData);
        name = stringData[0];
        mUniq = stringData[1];
        mMachineName = stringData[2];

        final int[] intData = new int[3];
        in.readIntArray(intData);
        order = intData[0];
        parentId = intData[1];
        id = intData[2];

        final boolean[] boolData = new boolean[2];
        in.readBooleanArray(boolData);
        isNoShow = boolData[0];
        isIgnore = boolData[1];

        services = new ArrayList<>();
        in.readTypedList(services, Service.CREATOR);
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{name, mUniq, mMachineName});
        out.writeIntArray(new int[]{order, parentId, id});
        out.writeBooleanArray(new boolean[]{isNoShow, isIgnore});
        out.writeTypedList(services);
    }

    @Override
    public final int describeContents() {
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
