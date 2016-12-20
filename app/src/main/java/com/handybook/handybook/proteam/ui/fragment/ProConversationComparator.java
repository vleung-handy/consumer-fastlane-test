package com.handybook.handybook.proteam.ui.fragment;

import android.support.annotation.NonNull;

import com.handybook.handybook.proteam.viewmodel.ProTeamProViewModel;
import com.layer.sdk.messaging.Message;

import java.util.Comparator;
import java.util.Date;

/**
 * A comparator for conversations. The ordering is as follows: model with a conversation always
 * comes before a model without a conversation model with a date always comes before a model without
 * a date smaller dates come before bigger dates
 */
public class ProConversationComparator implements Comparator<ProTeamProViewModel>
{
    @Override
    public int compare(
            final ProTeamProViewModel o1, final ProTeamProViewModel o2
    )
    {
        if (o2.getConversation() == null && o1.getConversation() == null)
        {
            //if both have no conversations, then we don't need to sort.
            return 0;
        }
        else
        {
            //a conversation is always ahead of no conversation
            if (o1.getConversation() == null)
            {
                return 1;
            }
            else if (o2.getConversation() == null)
            {
                return -1;
            }
        }

        //if we get here, that means both have conversations
        Date date1 = getMostRecentDate(o1.getConversation().getLastMessage());
        Date date2 = getMostRecentDate(o2.getConversation().getLastMessage());

        if (date1 == null && date2 == null)
        {
            return 0;
        }
        else
        {
            if (date1 == null)
            {
                return 1;
            }
            else if (date2 == null)
            {
                return -1;
            }
        }

        //both dates are not null;

        //the bigger date is the one that will be ahead. i.e., if date 1 = 11/1, date 2 = 1/2
        //date2.compareTo(date1) will return 1 for this comparator, hence making date 1 appearing
        //after date 2 on the sort
        return date2.compareTo(date1);
    }

    /**
     * Returns the greater of the received at and sent at
     *
     * @param message
     * @return
     */
    private Date getMostRecentDate(@NonNull final Message message)
    {
        if (message != null)
        {
            if (message.getReceivedAt() != null)
            {
                return message.getReceivedAt();
            }
            else if (message.getSentAt() != null)
            {
                return message.getSentAt();
            }
        }

        return null;
    }
}
