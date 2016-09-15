package com.handybook.handybook.booking.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
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
     * TODO TEMP HACK - mostly a dupe of the other setEntryInfo method
     * @param entryMethodInstructionType
     * @param lockboxCode
     * @param lockboxLocation
     */
    public void setEntryInfo(
            @BookingInstruction.EntryMethodType String entryMethodInstructionType,
            final String lockboxCode, final String lockboxLocation)
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

        BookingInstruction entryInfoTypeInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.ENTRY_METHOD,
                null,
                entryMethodInstructionType,
                null,
                null
        );

        BookingInstruction lockboxCodeInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.LOCKBOX_CODE,
                null,
                null,
                lockboxCode,
                null
        );

        BookingInstruction lockboxLocationInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.KEY_LOCATION,
                null,
                null,
                lockboxLocation,
                null
        );
        // Find it and if it exists update it, or create the new one.
        int entryMethodPosition = -1, keyLocationPosition = -1, lockboxCodePosition = -1;
        for (int i = 0; i < mBookingInstructions.size(); i++)
        {
            BookingInstruction eBookingInstruction = mBookingInstructions.get(i);
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.ENTRY_METHOD))
            {
                entryMethodPosition = i;
                continue;
            }
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.KEY_LOCATION))
            {
                keyLocationPosition = i;
                continue;
            }
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.LOCKBOX_CODE))
            {
                lockboxCodePosition = i;
            }
        }
        if (entryMethodPosition >= 0)
        {
            mBookingInstructions.get(entryMethodPosition).setInstructionType(
                    entryInfoTypeInstruction.getInstructionType()
            );
        }
        else
        {
            mBookingInstructions.add(entryInfoTypeInstruction);
        }

        if (lockboxCodePosition >= 0)//set lockbox code
        {
            mBookingInstructions.get(lockboxCodePosition).setDescription(
                    lockboxCodeInstruction.getDescription()
            );
        }
        else
        {
            mBookingInstructions.add(lockboxCodeInstruction);
        }

        if (keyLocationPosition >= 0)//TODO HACK - USING AS LOCKBOX LOCATION
        {
            mBookingInstructions.get(keyLocationPosition).setDescription(
                    lockboxLocationInstruction.getDescription()
            );
        }
        else
        {
            mBookingInstructions.add(lockboxLocationInstruction);
        }
        triggerObservers();
    }
    public void setEntryInfo(
            @BookingInstruction.EntryMethodType String entryMethodInstructionType,
                             final String getInText)
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

        BookingInstruction entryInfoTypeInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.ENTRY_METHOD,
                null,
                entryMethodInstructionType,
                null,
                null
        );
        BookingInstruction entryInfoMessageInstruction = new BookingInstruction(
                null,
                BookingInstruction.MachineName.KEY_LOCATION,
                null,
                null,
                getInText,
                null
        );
        // Find it and if it exists update it, or create the new one.
        int entryMethodPosition = -1, keyLocationPosition = -1;
        for (int i = 0; i < mBookingInstructions.size(); i++)
        {
            BookingInstruction eBookingInstruction = mBookingInstructions.get(i);
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.ENTRY_METHOD))
            {
                entryMethodPosition = i;
                continue;
            }
            if (eBookingInstruction.isOfMachineName(BookingInstruction.MachineName.KEY_LOCATION))
            {
                keyLocationPosition = i;
            }
        }
        if (entryMethodPosition >= 0)
        {
            mBookingInstructions.get(entryMethodPosition).setInstructionType(
                    entryInfoTypeInstruction.getInstructionType()
            );
        }
        else
        {
            mBookingInstructions.add(entryInfoTypeInstruction);
        }
        if (keyLocationPosition >= 0)//TODO HACK - USING AS LOCKBOX LOCATION
        {
            mBookingInstructions.get(keyLocationPosition).setDescription(
                    entryInfoMessageInstruction.getDescription()
            );
        }
        else
        {
            mBookingInstructions.add(entryInfoMessageInstruction);
        }
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
