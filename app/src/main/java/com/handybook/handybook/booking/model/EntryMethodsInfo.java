package com.handybook.handybook.booking.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.module.configuration.model.Configuration;
import com.handybook.handybook.util.IOUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

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
    @SerializedName("default_option_machine_name")
    private String mDefaultOptionMachineName;

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

    /**
     * TODO TEMPORARY HACK, this is getting removed ASAP
     * @return
     */
    public static EntryMethodsInfo getEntryMethodInfo_HACK(Configuration configuration, @NonNull Context context)
    {
        String path = "editentry_hack/entry_method_info_hack.json";

        try
        {
            String fileContents = IOUtils.loadJSONFromAsset(context, path);
            try
            {
                EntryMethodsInfo entryMethodsInfo =  (new Gson()).fromJson(fileContents, EntryMethodsInfo.class);//TODO: add exception handling
                boolean lockboxEntryMethodEnabled = configuration != null && configuration.isLockboxEntryMethodEnabled();
                if(!lockboxEntryMethodEnabled && entryMethodsInfo != null)
                {
                    //remove lockbox option if config says no lockbox
                    List<EntryMethodOption> entryMethodOptions = entryMethodsInfo.getEntryMethodOptions();
                    ListIterator listIterator = entryMethodOptions.listIterator();
                    while(listIterator.hasNext())
                    {
                        EntryMethodOption entryMethodOption = (EntryMethodOption) listIterator.next();
                        if(BookingInstruction.InstructionType.EntryMethod.LOCKBOX.equals(
                                entryMethodOption.getMachineName()))
                        {
                            listIterator.remove();
                            break;
                        }
                    }
                }
                return entryMethodsInfo;


            }
            catch (JsonSyntaxException ex)
            {
                Crashlytics.logException(ex);
            }

        }
        catch (IOException e)
        {
            Crashlytics.logException(e);
        }

        //should never be null since we're getting this from the assets folder
        return null;
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
    public String getDefaultOptionMachineName()
    {
        return mDefaultOptionMachineName;
    }
}
