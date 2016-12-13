package com.handybook.handybook.model.bill;


import com.google.android.gms.wallet.LineItem;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Example Bill Payload:
 * {
 *   "bill":{
 *     "header_title":"Cleaning Plan: Every 2 weeks",
 *     "header_text":"Starting Wed, Jan 10/n8:00 am",
 *     "final_line_item":{
 *       "type":"FINAL_PRICE",
 *       "label":"Today's Total",
 *       "amount":5100
 *     },
 *     "currency_symbol":"$",
 *     "sections":[
 *       {
 *         "type":"ITEMIZED_BILL",
 *         "line_items":[
 *           {
 *             "label":"Cleaning + 1 extra",
 *             "amount":7500
 *
 *           },
 *           {
 *             "label":"Coupon",
 *             "amount":-1750
 *
 *           },
 *           {
 *             "label":"Trust & Support Fee",
 *           "amount": 3,
 *           "help_text": "It has to be done..."
 *           },
 *           {
 *             "label":"Credits",
 *             "amount":-1000
 *           }
 *          ]
 *        }
 *     ]
 *   }
 * }
 * */
public class Bill
{

    @SerializedName("header_title")
    private String mHeaderTitle;
    @SerializedName("header_text")
    private String mHeaderText;
    @SerializedName("final_line_item")
    private LineItem mFinalLineItem;
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;
    @SerializedName("sections")
    private ArrayList<BillSection> mSections;

    public String getHeaderTitle()
    {
        return mHeaderTitle;
    }

    public String getHeaderText()
    {
        return mHeaderText;
    }

    public LineItem getFinalLineItem()
    {
        return mFinalLineItem;
    }

    public String getCurrencySymbol()
    {
        return mCurrencySymbol;
    }

    public ArrayList<BillSection> getSections()
    {
        return mSections;
    }

    /**
     * Example BillSection payload:
     * {
     *   "type":"ITEMIZED_BILL",
     *   "line_items":[
     *     {
     *       "label":"Cleaning + 1 extra",
     *       "amount":7500
     *     },
     *     {
     *       "label":"Coupon",
     *       "amount":-1750
     *     },
     *     {
     *       "label":"Trust & Support Fee",
     *     "amount": 3,
     *     "help_text": "It has to be done..."
     *     },
     *     {
     *       "label":"Credits",
     *       "amount":-1000
     *     }
     * }
     */
    public static class BillSection
    {
        @SerializedName("type")
        private String mType;
        @SerializedName("line_items")
        private ArrayList<BillLineItem> mLineItems;

        public String getType()
        {
            return mType;
        }

        public ArrayList<BillLineItem> getLineItems()
        {
            return mLineItems;
        }
    }


    /**
     * Example BillLineItem payload:
     * {
     *   "type":"DEFAULT",
     *   "label":"Trust & Support Fee",
     *   "amount":0,
     *   "amount_text":""free,
     *   "help_text":"It has to be done..."
     * }
     */
    public static class BillLineItem
    {
        public enum ItemType
        {
            @SerializedName("DEFAULT")
            DEFAULT,
            @SerializedName("FINAL_PRICE")
            FINAL_PRICE
        }


        /** One of valid types, otherwise defaults to DEFAULT **/
        @SerializedName("type")
        private ItemType mType;
        /** Amount In Cents **/
        @SerializedName("label")
        private String mLabel;
        /** Amount In Cents **/
        @SerializedName("amount")
        private int mAmount;
        /** Amount as text, if provided supersedes cent amount **/
        @SerializedName("amount_text")
        private String mAmountText;
        /** Help text, if provided we display clickable question mark next to the label **/
        @SerializedName("help_text")
        private String mHelpText;

        public ItemType getType()
        {
            return mType != null ? mType : ItemType.DEFAULT;
        }

        public String getLabel()
        {
            return mLabel;
        }

        public int getAmount()
        {
            return mAmount;
        }

        public String getAmountText()
        {
            return mAmountText;
        }

        public String getHelpText()
        {
            return mHelpText;
        }

        public boolean hasHelpText()
        {
            return mHelpText != null && !mHelpText.isEmpty();
        }
    }
}
