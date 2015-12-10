package com.handybook.handybook.helpcenter;

import com.google.gson.annotations.SerializedName;

public final class HelpNodeWrapper
{
    @SerializedName("node")
    private HelpNode helpNode;

    public HelpNodeWrapper()
    {
    }

    public final HelpNode getHelpNode()
    {
        return helpNode;
    }
}
