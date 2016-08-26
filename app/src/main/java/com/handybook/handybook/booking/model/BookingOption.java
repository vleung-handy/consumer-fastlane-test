package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

//TODO this class and associates need major refactoring
public class BookingOption implements Parcelable
{

    //todo: auto serialized enums for type see : Booking.LaundryStatus
    public final static String TYPE_OPTION = "option";
    public final static String TYPE_QUANTITY = "quantity"; //what does this look like
    public final static String TYPE_TEXT = "text";
    public final static String TYPE_CHECKLIST = "checklist";
    public final static String TYPE_OPTION_PICKER = "option_picker"; //TODO: what's the difference between this and "option"?

    @SerializedName("uniq")
    private String uniq;
    @SerializedName("type")
    private String type;
    @SerializedName("title")
    private String title;
    @SerializedName("info")
    private String info;
    @SerializedName("default_value")
    private String defaultValue;
    @SerializedName("options")
    private String[] options; //TODO making the option attributes in separate arrays is ugly, needs refactoring
    @SerializedName("options_sub_text")
    private String[] optionsSubText;
    @SerializedName("options_super_text")
    private String[] mOptionsSuperText; //TODO hacky, refactor
    @SerializedName("options_right_title_text")
    private String[] optionsRightTitleText;
    @SerializedName("options_left_text")
    private String[] optionsRightSubText;
    @SerializedName("options_images")
    private String[][] optionsImages;
    @SerializedName("options_hidden")
    private boolean[] optionsHidden; //determines which options are initially hidden
    @SerializedName("options_left_strip_indicator_visible")
    private boolean[] mLeftStripIndicatorVisible; //TODO hacky, refactor
    @SerializedName("hour_info")
    private float[] hoursInfo;
    @SerializedName("warnings")
    private String[][] warnings;
    @SerializedName("child_elements")
    private String[][] children;
    @SerializedName("page")
    private int page;
    @SerializedName("post")
    private int post;

    //TODO: BookingOption is deserialized from a server response and used as a view model for the option view.
    //some fields in this class are not actually from the server and are set programmatically. how can we make this cleaner?
    private int[] mImageResourceIds;

    public BookingOption()
    {
    }

    public void setLeftStripIndicatorVisible(final boolean[] leftStripIndicatorVisible)
    {
        mLeftStripIndicatorVisible = leftStripIndicatorVisible;
    }

    public boolean[] getLeftStripIndicatorVisible()
    {
        return mLeftStripIndicatorVisible;
    }

    public void setOptionsSuperText(final String[] optionsSuperText)
    {
        mOptionsSuperText = optionsSuperText;
    }

    public String[] getOptionsSuperText()
    {
        return mOptionsSuperText;
    }

    public void setImageResourceIds(int[] imageResourceIds)
    {
        mImageResourceIds = imageResourceIds;
    }

    public void setOptionsHidden(final boolean[] optionsHidden)
    {
        this.optionsHidden = optionsHidden;
    }

    public boolean[] getOptionsHidden()
    {
        return optionsHidden;
    }

    public int[] getImageResourceIds()
    {
        return mImageResourceIds;
    }

    public String getUniq()
    {
        return uniq;
    }

    void setUniq(final String uniq)
    {
        this.uniq = uniq;
    }

    public String getType()
    {
        return type;
    }

    public void setType(final String type)
    {
        this.type = type;
    }

    public String getTitle()
    {
        return title;
    }

    void setTitle(final String title)
    {
        this.title = title;
    }

    public String getInfo()
    {
        return info;
    }

    void setInfo(final String info)
    {
        this.info = info;
    }

    public String getDefaultValue()
    {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public String[] getOptions()
    {
        return options;
    }

    public void setOptions(final String[] options)
    {
        this.options = options;
    }

    public String[] getOptionsSubText()
    {
        return optionsSubText;
    }

    public void setOptionsSubText(final String[] optionsSubText)
    {
        this.optionsSubText = optionsSubText;
    }

    public String[] getOptionsRightSubText()
    {
        return optionsRightSubText;
    }

    public String[] getOptionsRightTitleText()
    {
        return optionsRightTitleText;
    }

    public void setOptionsRightTitleText(String[] optionsRightTitleText)
    {
        this.optionsRightTitleText = optionsRightTitleText;
    }

    public void setOptionsRightSubText(final String[] optionsRightSubText)
    {
        this.optionsRightSubText = optionsRightSubText;
    }

    String[][] getOptionsImages()
    {
        return optionsImages;
    }

    void setOptionsImages(final String[][] optionsImages)
    {
        this.optionsImages = optionsImages;
    }

    public float[] getHoursInfo()
    {
        return hoursInfo;
    }

    void setHoursInfo(final float[] hoursInfo)
    {
        this.hoursInfo = hoursInfo;
    }

    public String[][] getWarnings()
    {
        return warnings;
    }

    void setWarnings(final String[][] warnings)
    {
        this.warnings = warnings;
    }

    public String[][] getChildren()
    {
        return children;
    }

    void setChildren(final String[][] children)
    {
        this.children = children;
    }

    public int getPage()
    {
        return page;
    }

    void setPage(final int page)
    {
        this.page = page;
    }

    public boolean isPost()
    {
        return post == 1;
    }

    void setPost(final boolean post)
    {
        this.post = post ? 1 : 0;
    }

    private BookingOption(final Parcel in)
    {
        final String[] stringData = new String[5];
        in.readStringArray(stringData);
        uniq = stringData[0];
        type = stringData[1];
        title = stringData[2];
        defaultValue = stringData[3];
        info = stringData[4];

        final int[] intData = new int[2];
        in.readIntArray(intData);
        page = intData[0];
        post = intData[1];

        options = in.createStringArray();
        optionsSubText = in.createStringArray();
        optionsRightTitleText = in.createStringArray();
        optionsRightSubText = in.createStringArray();
        optionsHidden = in.createBooleanArray();
        optionsImages = (String[][]) in.readSerializable();
        hoursInfo = in.createFloatArray();
        warnings = (String[][]) in.readSerializable();
        children = (String[][]) in.readSerializable();
    }

    @Override
    public final void writeToParcel(final Parcel out, final int flags)
    {
        out.writeStringArray(new String[]{uniq, type, title, defaultValue, info});
        out.writeIntArray(new int[]{page, post});
        out.writeStringArray(options);
        out.writeStringArray(optionsSubText);
        out.writeStringArray(optionsRightTitleText);
        out.writeStringArray(optionsRightSubText);
        out.writeBooleanArray(optionsHidden);
        out.writeSerializable(optionsImages);
        out.writeFloatArray(hoursInfo);
        out.writeSerializable(warnings);
        out.writeSerializable(children);
    }

    @Override
    public final int describeContents()
    {
        return 0;
    }

    public static final Creator CREATOR = new Creator()
    {
        public BookingOption createFromParcel(final Parcel in)
        {
            return new BookingOption(in);
        }

        public BookingOption[] newArray(final int size)
        {
            return new BookingOption[size];
        }
    };
}
