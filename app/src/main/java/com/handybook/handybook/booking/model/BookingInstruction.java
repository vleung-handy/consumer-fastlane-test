package com.handybook.handybook.booking.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

public class BookingInstruction implements Parcelable
{
    @SerializedName("id")
    private Long mId;
    @SerializedName("machine_name")
    private String mMachineName;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("instruction_type")
    private String mInstructionType;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("requested")
    private Boolean mIsRequested;
    @SerializedName("lockbox_code")
    private String mLockboxCode;


    private static final Map<String, Integer> ICONS;

    static
    {
        ICONS = new HashMap<>();
        ICONS.put(InstructionType.KITCHEN, R.drawable.ic_instruction_kitchen);
        ICONS.put(InstructionType.BEDROOM, R.drawable.ic_instruction_bedroom);
        ICONS.put(InstructionType.BATHROOM, R.drawable.ic_instruction_bathroom);
        ICONS.put(InstructionType.FLOORS, R.drawable.ic_instruction_floor);
        ICONS.put(InstructionType.GENERAL, R.drawable.ic_instruction_general);
    }

    public BookingInstruction(
            @Nullable final Long id,
            @BookingInstructionMachineName final String machineName,
            final String title,
            @BookingInstructionType final String instructionType,
            final String description,
            final Boolean isRequested
    )
    {
        mId = id;
        mMachineName = machineName;
        mTitle = title;
        mInstructionType = instructionType;
        mDescription = description;
        mIsRequested = isRequested;
    }

    protected BookingInstruction(Parcel in)
    {
        mId = (Long) in.readValue(Long.class.getClassLoader());
        mMachineName = in.readString();
        mTitle = in.readString();
        mInstructionType = in.readString();
        mDescription = in.readString();
        mLockboxCode = in.readString();
        mIsRequested = (in.readInt() == 0) ? false : true;
    }

    public static final Creator<BookingInstruction> CREATOR = new Creator<BookingInstruction>()
    {
        @Override
        public BookingInstruction createFromParcel(Parcel in)
        {
            return new BookingInstruction(in);
        }

        @Override
        public BookingInstruction[] newArray(int size)
        {
            return new BookingInstruction[size];
        }
    };

    public Long getId()
    {
        return mId;
    }

    public String getMachineName()
    {
        return mMachineName;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public String getInstructionType()
    {
        return mInstructionType;
    }

    public boolean getIsRequested()
    {
        return mIsRequested != null && mIsRequested;
    }

    public void setId(final Long id)
    {
        mId = id;
    }

    public void setMachineName(final String machineName)
    {
        mMachineName = machineName;
    }

    public void setTitle(final String title)
    {
        mTitle = title;
    }

    public void setInstructionType(final String instructionType)
    {
        mInstructionType = instructionType;
    }

    public void setDescription(final String description)
    {
        mDescription = description;
    }

    public void setIsRequested(final Boolean isRequested)
    {
        mIsRequested = isRequested;
    }

    @DrawableRes
    public final int getImageResource()
    {
        if (ICONS.get(getInstructionType()) != null)
        {
            return ICONS.get(getInstructionType());
        }

        return ICONS.get("general");
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags)
    {
        dest.writeValue(mId);
        dest.writeString(mMachineName);
        dest.writeString(mTitle);
        dest.writeString(mInstructionType);
        dest.writeString(mDescription);
        dest.writeString(mLockboxCode);
        dest.writeInt((mIsRequested != null && mIsRequested) ? 1 : 0);
    }

    public boolean isOfMachineName(final String machineName)
    {
        return mMachineName != null && mMachineName.equals(machineName);
    }


    public static final class MachineName
    {
        public static final String PREFERENCE = "preference";
        public static final String NOTE_TO_PRO = "note_to_pro";
        public static final String ENTRY_METHOD = "entry_method";
        public static final String KEY_LOCATION = "key_location";
        public static final String LOCKBOX_CODE = "lockbox_code";
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            MachineName.PREFERENCE,
            MachineName.NOTE_TO_PRO,
            MachineName.ENTRY_METHOD,
            MachineName.KEY_LOCATION,
            MachineName.LOCKBOX_CODE
    })
    public @interface BookingInstructionMachineName
    {
    }

    public static final class InstructionType
    {
        public static final String KITCHEN = "kitchen";
        public static final String BEDROOM = "bedroom";
        public static final String BATHROOM = "bathroom";
        public static final String FLOORS = "floors";
        public static final String GENERAL = "general";

        public static final class EntryMethod
        {
            public static final String DOORMAN = "doorman";
            public static final String AT_HOME = "at_home";
            public static final String HIDE_KEY = "hide_key";
            public static final String LOCKBOX = "lockbox";
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            InstructionType.KITCHEN,
            InstructionType.BEDROOM,
            InstructionType.BATHROOM,
            InstructionType.FLOORS,
            InstructionType.GENERAL,

            InstructionType.EntryMethod.AT_HOME,
            InstructionType.EntryMethod.DOORMAN,
            InstructionType.EntryMethod.HIDE_KEY,
            InstructionType.EntryMethod.LOCKBOX,
    })
    public @interface BookingInstructionType
    {
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            InstructionType.EntryMethod.AT_HOME,
            InstructionType.EntryMethod.DOORMAN,
            InstructionType.EntryMethod.HIDE_KEY,
            InstructionType.EntryMethod.LOCKBOX,
    })
    public @interface EntryMethodType
    {
    }


}
