package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * model from server that determines how to display an entry option to the user
 */
public class EntryMethodOption implements Serializable, Option
{
    @SerializedName("recommended")
    private boolean mRecommended;
    /**
     * true if this option should be selected by default
     */
    @SerializedName("default")
    private boolean mDefault;

    /**
     * the underlying value of this option that will be sent to the server
     */
    @SerializedName("machine_name")
    private String mMachineName;
    /**
     * the title text of this option
     */
    @SerializedName("title")
    private String mTitleText;

    /**
     * the text below the title text
     */
    @SerializedName("subtitle")
    private String mSubtitleText;

    @SerializedName("input_form")
    private InputFormDefinition mInputFormDefinition;

    public InputFormDefinition getInputFormDefinition()
    {
        return mInputFormDefinition;
    }

    public boolean isRecommended()
    {
        return mRecommended;
    }

    @Override
    public boolean isDefault()
    {
        return mDefault;
    }

    @Override
    public String getTitleText()
    {
        return mTitleText;
    }

    @Override
    public String getSubtitleText()
    {
        return mSubtitleText;
    }

    @BookingInstruction.EntryMethodType
    public String getMachineName()
    {
        return mMachineName;
    }


    /*TODO temporarily putting the below methods here now for hack*/
    //TODO the edit entry endpoint expects an index for the selected entry method
    public static int getEntryMethodGetInIdForMachineName(String entryMethodMachineName)
    {
        switch (entryMethodMachineName)
        {
            case BookingInstruction.InstructionType.EntryMethod.AT_HOME:
                return 0;
            case BookingInstruction.InstructionType.EntryMethod.DOORMAN:
                return 1;
            case BookingInstruction.InstructionType.EntryMethod.HIDE_KEY:
                return 2;
            case BookingInstruction.InstructionType.EntryMethod.LOCKBOX:
                return 3;
            default:
                return -1;
        }
    }

    //TODO the booking stores the selected entry method as an in index but the entry methods model only knows machine name
    @BookingInstruction.EntryMethodType
    public static String getEntryMethodMachineNameForGetInId(int getInId)
    {
        switch(getInId)
        {
            case 0:
                return BookingInstruction.InstructionType.EntryMethod.AT_HOME;
            case 1:
                return BookingInstruction.InstructionType.EntryMethod.DOORMAN;
            case 2:
                return BookingInstruction.InstructionType.EntryMethod.HIDE_KEY;
            case 3:
                return BookingInstruction.InstructionType.EntryMethod.LOCKBOX;
            default:
                return null;
        }
    }
}
