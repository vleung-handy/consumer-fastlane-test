package com.handybook.handybook;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

final class BookingOption implements Parcelable {
    @SerializedName("uniq") private String uniq;
    @SerializedName("type") private String type;
    @SerializedName("title") private String title;
    @SerializedName("default_value") private String defaultValue;
    @SerializedName("options") private String[] options;
    @SerializedName("warnings") private String[][] warnings;
    @SerializedName("child_elements") private String[][] children;
    @SerializedName("page") private int page;

    final String getUniq() {
        return uniq;
    }

    final void setUniq(final String uniq) {
        this.uniq = uniq;
    }

    final String getType() {
        return type;
    }

    final void setType(final String type) {
        this.type = type;
    }

    final String getTitle() {
        return title;
    }

    final void setTitle(final String title) {
        this.title = title;
    }

    final String getDefaultValue() {
        return defaultValue;
    }

    final void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    final String[] getOptions() {
        return options;
    }

    final void setOptions(final String[] options) {
        this.options = options;
    }

    final String[][] getWarnings() {
        return warnings;
    }

    final void setWarnings(final String[][] warnings) {
        this.warnings = warnings;
    }

    final String[][] getChildren() {
        return children;
    }

    final void setChildren(final String[][] children) {
        this.children = children;
    }

    final int getPage() {
        return page;
    }

    final void setPage(final int page) {
        this.page = page;
    }

    private BookingOption(final Parcel in) {
        final String[] stringData = new String[4];
        in.readStringArray(stringData);
        uniq = stringData[0];
        type = stringData[1];
        title = stringData[1];
        defaultValue = stringData[1];

        final int[] intData = new int[1];
        in.readIntArray(intData);
        page = intData[0];

        options = in.createStringArray();
        warnings = (String[][]) in.readSerializable();
        children = (String[][]) in.readSerializable();
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags) {
        out.writeStringArray(new String[]{ uniq, type, title, defaultValue });
        out.writeIntArray(new int[]{page});
        out.writeStringArray(options);
        out.writeSerializable(warnings);
        out.writeSerializable(children);
    }

    @Override
    public final int describeContents(){
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        public BookingOption createFromParcel(final Parcel in) {
            return new BookingOption(in);
        }
        public BookingOption[] newArray(final int size) {
            return new BookingOption[size];
        }
    };
}
