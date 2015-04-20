package com.handybook.handybook.ui.widget;

import com.handybook.handybook.core.HelpNode;

import org.json.JSONObject;

import java.util.Dictionary;

public final class CTANavigationData
{
    public String nodeContentWebUrl;
    public String navigationActionId;

    public CTANavigationData()
    {
        initData("", "");
    }

    public CTANavigationData(String nodeContentWebUrl, String navigationActionId)
    {
        initData(nodeContentWebUrl, navigationActionId);
    }

    public CTANavigationData(HelpNode node)
    {
        JSONObject nodeContentData;
        try {
            nodeContentData = new JSONObject(node.getContent());
            initData(nodeContentData.getString("web_url"), nodeContentData.getString("action"));
        }
        catch (Exception e)
        {
            System.err.println("initFromHelpNode : Could not parse node content into JSON");
        }
    }

    private void initData(String nodeContentWebUrl, String navigationActionId)
    {
        this.nodeContentWebUrl = nodeContentWebUrl;
        this.navigationActionId = navigationActionId;
    }
}