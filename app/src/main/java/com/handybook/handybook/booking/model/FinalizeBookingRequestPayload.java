package com.handybook.handybook.booking.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Body of POST request to /api/v3/bookings/:id/finalize_booking
 */
public class FinalizeBookingRequestPayload extends Observable
{
    @SerializedName("password")
    private String mPassword;
    @SerializedName("apply_to_all")
    private Boolean mShouldApplyToAll;
    @SerializedName("booking_instructions")
    private List<BookingInstruction> mBookingInstructions;

    public String getPassword()
    {
        return mPassword;
    }

    public void setPassword(final String password)
    {
        mPassword = password;
        triggerObservers();
    }

    public Boolean getShouldApplyToAll()
    {
        return mShouldApplyToAll;
    }

    public void setShouldApplyToAll(final Boolean shouldApplyToAll)
    {
        mShouldApplyToAll = shouldApplyToAll;
        triggerObservers();
    }

    public List<BookingInstruction> getBookingInstructions()
    {
        return mBookingInstructions;
    }

    public void setBookingInstructions(final List<BookingInstruction> bookingInstructions)
    {
        //filter out all previous instructions of
        if (mBookingInstructions == null)
        {
            mBookingInstructions = new ArrayList<>();
        }
        ArrayList<BookingInstruction> filteredBookingInstructions = new ArrayList<>();
        for (BookingInstruction eBookingInstruction : mBookingInstructions)
        {
            if (eBookingInstruction.getMachineName() == null
                    || eBookingInstruction.getMachineName() == ""
                    || eBookingInstruction.getMachineName() == "preference")
            {
                continue;
            }
            filteredBookingInstructions.add(eBookingInstruction);
        }
        filteredBookingInstructions.addAll(bookingInstructions);
        mBookingInstructions = filteredBookingInstructions;
        triggerObservers();
    }

    public static FinalizeBookingRequestPayload fromJson(final String jsonIn)
    {
        return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create()
                .fromJson(jsonIn, FinalizeBookingRequestPayload.class);
    }

    public String toJson()
    {
        final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        return gson.toJson(this);
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    public void setEntryInfo(final int entryMethodIndex, final String getInText)
    {
        /*{
            "id": 5, #optional if machine_name included
            "machine_name": "entry_method", #optional if id included
            "instruction_type": "at_home"
        },
        {
            "id": 3, #optional if machine_name included
            "machine_name": "key_location", #optional if id included
            "description": "I'll be home so just ring the buzzer!"
        }*/
        if (mBookingInstructions == null)
        {
            mBookingInstructions = new ArrayList<>();
        }
        String entryMethodInstructionType;
        switch (entryMethodIndex)
        {
            case 0:
                entryMethodInstructionType = "at_home";
                break;
            case 1:
                entryMethodInstructionType = "doorman";
                break;
            case 2:
                entryMethodInstructionType = "hide_key";
                break;
            default:
                entryMethodInstructionType = "at_home";
        }
        BookingInstruction entryInfoTypeInstruction = new BookingInstruction(
                null,
                "entry_method",
                entryMethodInstructionType,
                null,
                null
        );
        BookingInstruction entryInfoMessageInstruction = new BookingInstruction(
                null,
                "key_location",
                null,
                getInText,
                null
        );
        mBookingInstructions.add(entryInfoTypeInstruction);
        mBookingInstructions.add(entryInfoMessageInstruction);
        notifyObservers();
    }

    public void setNoteToPro(final String noteToPro)
    {
        /*{
            machine_name: 'note_to_pro',
            description:  'Please remember to take out my trash',
          }*/
        if (mBookingInstructions == null)
        {
            mBookingInstructions = new ArrayList<>();
        }
        BookingInstruction noteToProInstruction = new BookingInstruction(
                null,
                "note_to_pro",
                null,
                noteToPro,
                null
        );
        mBookingInstructions.add(noteToProInstruction);
        notifyObservers();
    }
}
