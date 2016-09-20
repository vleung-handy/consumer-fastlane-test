package com.handybook.handybook.booking.model;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

/**
 * Body of POST request to /api/v3/bookings/:id/finalize_booking
 *
 */
public class FinalizeBookingRequestPayload extends Observable
{
    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
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
        if (mBookingInstructions == null)
        {
            mBookingInstructions = new ArrayList<>();
        }
        //filter out all previous instructions of
        ArrayList<BookingInstruction> preferenceInstructions = new ArrayList<>();
        for (BookingInstruction eBookingInstruction : mBookingInstructions)
        {
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.PREFERENCE))
            {
                preferenceInstructions.add(eBookingInstruction);
            }
        }
        mBookingInstructions.removeAll(preferenceInstructions);
        mBookingInstructions.addAll(bookingInstructions);
        triggerObservers();
    }

    public static FinalizeBookingRequestPayload fromJson(final String jsonIn)
    {
        return GSON.fromJson(jsonIn, FinalizeBookingRequestPayload.class);
    }

    public String toJson()
    {
        final Gson gson = GSON;
        return gson.toJson(this);
    }

    private void triggerObservers()
    {
        setChanged();
        notifyObservers();
    }

    /**
     * adds to or modifies the booking instructions list based on the given user-specified entry
     * info
     *
     * @param selectedEntryMethodMachineName
     * @param selectedEntryMethodInputFormValues
     */
    public void setEntryInfo(
            @BookingInstruction.EntryMethodType String selectedEntryMethodMachineName,
            @NonNull Map<String, String> selectedEntryMethodInputFormValues
    )
    {
        BookingInstruction entryInfoTypeInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.ENTRY_METHOD,
                null,
                selectedEntryMethodMachineName,
                null,
                null
        );

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
        /*
        a map of booking instruction machine name -> booking instruction object
        (candidates to add to global booking instructions list)
        using a map instead of list so we can easily check if these are present
        in the current booking instructions
         */
        Map<String, BookingInstruction> entryInfoBookingInstructionsMap = new HashMap<>();//TODO rename
        entryInfoBookingInstructionsMap.put(
                BookingInstruction.MachineName.ENTRY_METHOD,
                entryInfoTypeInstruction
        );

        //create booking instructions from the selected entry method input form values
        for (String inputFormFieldMachineName : selectedEntryMethodInputFormValues.keySet())
        {
            BookingInstruction entryInfoMessageInstruction = new BookingInstruction(
                    null,
                    inputFormFieldMachineName,
                    null,
                    null,
                    selectedEntryMethodInputFormValues.get(inputFormFieldMachineName),
                    null
            );
            entryInfoBookingInstructionsMap.put(
                    inputFormFieldMachineName,
                    entryInfoMessageInstruction
            );
        }

        /*
        check if these instructions are already present in the global booking instructions list.
        if so, update and remove from list to be added
         */
        for(BookingInstruction bookingInstruction : mBookingInstructions)
        {
            String bookingInstructionMachineName = bookingInstruction.getMachineName();
            BookingInstruction entryMethodBookingInstruction =
                    entryInfoBookingInstructionsMap.get(bookingInstructionMachineName);
            if (entryMethodBookingInstruction != null) //global booking instructions list already has this entry method booking instruction
            {
                /*
                update the booking instruction in the global list with the entry method info,
                and remove from the entry methods to be added later to the global list
                 */
                if (BookingInstruction.MachineName.ENTRY_METHOD.equals(bookingInstructionMachineName))
                {
                    /*
                    booking instructions has weird structure so have to do this
                     */
                    bookingInstruction.setInstructionType(entryMethodBookingInstruction.getInstructionType());
                }
                else
                {
                    bookingInstruction.setDescription(entryMethodBookingInstruction.getDescription());
                }
                entryInfoBookingInstructionsMap.remove(bookingInstructionMachineName);
            }
        }

        //add the entry info instructions that were not already in the global booking instructions list
        mBookingInstructions.addAll(entryInfoBookingInstructionsMap.values());
        triggerObservers();
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
                BookingInstruction.MachineName.NOTE_TO_PRO,
                null,
                null,
                noteToPro,
                null);

        // Find it and if it exists update it
        for (BookingInstruction eBookingInstruction : mBookingInstructions)
        {
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.NOTE_TO_PRO))
            {
                eBookingInstruction.setDescription(noteToPro);
                notifyObservers();
                return;
            }
        }
        // Didn't exist yet, create it.
        mBookingInstructions.add(noteToProInstruction);
        triggerObservers();
    }

    /**
     * Returns content of first note to pro instruction, or null if none found
     *
     * @return
     */
    public String getNoteToPro()
    {
        if (mBookingInstructions == null)
        {
            return null;
        }
        for (BookingInstruction eBookingInstruction : mBookingInstructions)
        {
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.NOTE_TO_PRO))
            {
                return eBookingInstruction.getDescription();
            }
        }
        return null;
    }
}
