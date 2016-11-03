package com.handybook.handybook.handylayer;


import android.support.annotation.Nullable;

import com.layer.sdk.messaging.Conversation;
import com.layer.sdk.messaging.Identity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

public class Util {
    public static String streamToString(InputStream stream) throws IOException {
        int n = 0;
        char[] buffer = new char[1024 * 4];
        InputStreamReader reader = new InputStreamReader(stream, "UTF8");
        StringWriter writer = new StringWriter();
        while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
        return writer.toString();
    }

    @Nullable
    public static Identity getOpposingParticipant(Conversation conversation)
    {
        Identity me = HandyLayer.getInstance().mLayerHelper.getLayerClient().getAuthenticatedUser();

        if (conversation == null || me == null)
        {
            return null;
        }

        for (Identity participant : conversation.getParticipants())
        {
            if (!participant.getUserId().equals(me.getUserId()))
            {
                //return the first user that is not "me", assuming that is the person we're chatting with
                return participant;
            }
        }

        return null;
    }
}
