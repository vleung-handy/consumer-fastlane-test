package com.handybook.handybook.ui.widget;

import com.handybook.handybook.core.HelpNode;

import org.json.JSONObject;

import java.util.Dictionary;

public final class CTANavigationData
{
    private static final String NODE_CONTENT_DATA_ACTION = "action";
    private static final String NODE_CONTENT_DATA_WEB_URL = "web_url";


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
        try
        {
            nodeContentData = new JSONObject(node.getContent());

            String action = "";
            if(nodeContentData.has(NODE_CONTENT_DATA_ACTION))
            {
                action = nodeContentData.getString(NODE_CONTENT_DATA_ACTION);
            }

            String webUrl = "";
            if(nodeContentData.has(NODE_CONTENT_DATA_WEB_URL))
            {
                webUrl = nodeContentData.getString(NODE_CONTENT_DATA_WEB_URL);
            }

            initData(action, webUrl);
        }
        catch (Exception e)
        {
            System.err.println("initFromHelpNode : Could not parse node content into JSON : " + e);
        }
    }

    private void initData(String navigationActionId, String nodeContentWebUrl)
    {
        this.navigationActionId = navigationActionId;
        this.nodeContentWebUrl = nodeContentWebUrl;
    }
}