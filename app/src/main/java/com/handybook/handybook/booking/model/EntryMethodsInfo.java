package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * model from server that determines how to display the entry info page to the user
 */
public class EntryMethodsInfo implements Serializable
{
    /**
     * The instructions text for the entry methods
     *
     * ex. "Let your professional know how they will get in:"
     */
    @SerializedName("instructions_text")
    private String mInstructionText;
    @SerializedName("entry_method_options")
    private List<EntryMethodOption> mEntryMethodOptions;

    /**
     * create booking flow: this is the default option
     * edit flow: this is the option previously specified by the user
     */
    @SerializedName("selected_option_machine_name")
    private String mSelectedOptionMachineName;

    /**
     * TODO for testing only, remove
     * @param instructionText
     * @param entryMethodOptions
     */
    public EntryMethodsInfo(String instructionText, List<EntryMethodOption> entryMethodOptions)
    {
        mInstructionText = instructionText;
        mEntryMethodOptions = entryMethodOptions;
    }

    public List<EntryMethodOption> getEntryMethodOptions()
    {
        return mEntryMethodOptions;
    }

    public String getInstructionText()
    {
        return mInstructionText;
    }

    /**
     * the default option to be selected
     *
     * should be one of BookingInstruction.InstructionType.EntryMethod
     * @return
     */
    public String getSelectedOptionMachineName()
    {
        return mSelectedOptionMachineName;
    }
}
