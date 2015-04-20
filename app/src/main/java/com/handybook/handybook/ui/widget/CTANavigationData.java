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

    public CTANavigationData(String navigationActionId, String nodeContentWebUrl)
    {
        initData(navigationActionId, nodeContentWebUrl);
    }

    public CTANavigationData(HelpNode node)
    {
        JSONObject nodeContentData;
        try {
            nodeContentData = new JSONObject(node.getContent());
            initData(nodeContentData.getString("action"), nodeContentData.getString("web_url"));
        }
        catch (Exception e)
        {
            System.err.println("initFromHelpNode : Could not parse node content into JSON");
        }
    }

    private void initData(String navigationActionId, String nodeContentWebUrl)
    {
        this.navigationActionId = navigationActionId;
        this.nodeContentWebUrl = nodeContentWebUrl;
    }
}