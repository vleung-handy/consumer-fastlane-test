package com.handybook.handybook.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public final class HelpNode implements Parcelable {
    @SerializedName("id") private int id;
    @SerializedName("type") private String type;
    @SerializedName("label") private String label;
    @SerializedName("content") private ArrayList<HelpNode> content;

    public HelpNode(){}

    public final int getId() {
        return id;
    }

    public final String getType() {
        return type;
    }

    public final String getLabel() {
        return label;
    }

    public final ArrayList<HelpNode> getContent() {
        return content;
    }

    private HelpNode(final Parcel in) {
        final int[] intData = new int[1];
        in.readIntArray(intData);
        id = intData[0];

        final String[] stringData = new String[2];
        in.readStringArray(stringData);
        type = stringData[0];
        label = stringData[1];

        content = new ArrayList<HelpNode>();
        in.readTypedList(content, HelpNode.CREATOR);
    }

    public static HelpNode fromJson(final String json) {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(json, HelpNode.class);
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeIntArray(new int[]{id});
        out.writeStringArray(new String[]{type, label});
        out.writeTypedList(content);
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public HelpNode createFromParcel(final Parcel in) {
            return new HelpNode(in);
        }
        public HelpNode[] newArray(final int size) {
            return new HelpNode[size];
        }
    };
}
