package com.handybook.handybook.core.model.bill;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Example Bill Payload:
 * {
 *   "bill":{
 *     "header_title":"Cleaning Plan: Every 2 weeks",
 *     "header_text":"Starting Wed, Jan 10/n8:00 am",
 *     "final_price_value":5100,
 *     "currency_symbol":"$",
 *     "sections":[
 *       {
 *         "line_items":[
 *           {
 *             "label":"Cleaning + 1 extra",
 *             "amount":7500
 *           },
 *           {
 *           "label":"Coupon",
 *           "amount":-1750
 *           },
 *           {
 *             "label":"Trust & Support Fee",
 *             "amount":3,
 *             "help_text":"It has to be done..."
 *           },
 *           {
 *             "label":"Credits",
 *             "amount":-1000
 *           }
 *         ]
 *       },
 *       {
 *         "line_items":[
 *           {
 *             "type":"TOTAL_PRICE",
 *             "label":"Today's Total",
 *             "amount":5100
 *           },
 *           {
 *             "label":"Credits",
 *             "amount":-5100
 *           }
 *         ]
 *       }
 *     ]
 *   }
 * }
 */
public class Bill
{

    @SerializedName("header_title")
    private String mHeaderTitle;
    @SerializedName("header_text")
    private String mHeaderText;
    @SerializedName("final_amount")
    private Long mFinalPriceValueCents;
    @SerializedName("currency_symbol")
    private String mCurrencySymbol;
    @SerializedName("sections")
    private ArrayList<BillSection> mSections;

    public static Bill fromJson(@NonNull CharSequence input)
    {
        return new Gson().fromJson(input.toString(), Bill.class);
    }

    @NonNull
    public String getHeaderTitle()
    {
        return mHeaderTitle;
    }

    @NonNull
    public String getHeaderText()
    {
        return mHeaderText;
    }

    @NonNull
    public Long getFinalPriceValueCents()
    {
        return mFinalPriceValueCents;
    }

    @NonNull
    public String getCurrencySymbol()
    {
        return mCurrencySymbol;
    }

    @NonNull
    public ArrayList<BillSection> getSections()
    {
        return mSections == null ? new ArrayList<BillSection>() : mSections;
    }

    /**
     * Example BillSection payload:
     * {
     *   {
     *     "type":"ITEMIZED_BILL",
     *     "line_items":[
     *   {
     *     "label":"Cleaning + 1 extra",
     *     "amount":7500
     *   },
     *   {
     *     "label":"Coupon",
     *     "amount":-1750
     *   },
     *   {
     *     "label":"Trust & Support Fee",
     *     "amount": 3,
     *     "help_text": "It has to be done..."
     *   },
     *   {
     *     "label":"Credits",
     *     "amount":-1000
     *   },
     *   {
     *     "type":"LARGE_PRICE",
     *     "label":"Today's Total",
     *     "amount":5600
     *   }
     * }
     */
    public static class BillSection
    {
        public enum BillSectionType
        {
            @SerializedName("DEFAULT")
            DEFAULT,
            @SerializedName("ITEMIZED_BILL")
            ITEMIZED_BILL
        }


        @SerializedName("type")
        private BillSectionType mType;
        @SerializedName("line_items")
        private ArrayList<BillLineItem> mLineItems;

        @Nullable
        public BillSectionType getType()
        {
            return mType != null ? mType : BillSectionType.DEFAULT;
        }

        @Nullable
        public ArrayList<BillLineItem> getLineItems()
        {
            return mLineItems;
        }

        public boolean isEmpty()
        {
            return mLineItems == null || mLineItems.isEmpty();
        }
    }


    /**
     * Example BillLineItem payload:
     * {
     *   "type":"DEFAULT",
     *   "label":"Trust & Support Fee",
     *   "amount":0,
     *   "amount_text":"free",
     *   "help_text":"It has to be done..."
     * }
     */
    public static class BillLineItem
    {
        public enum ItemType
        {
            @SerializedName("DEFAULT")
            DEFAULT,
            @SerializedName("LARGE_PRICE")
            LARGE_PRICE
        }


        /** One of valid types, otherwise defaults to DEFAULT **/
        @SerializedName("type")
        private ItemType mType;
        /** Amount In Cents **/
        @SerializedName("label")
        private String mLabel;
        /** Amount In Cents **/
        @SerializedName("amount")
        private Long mAmountCents;
        /** Amount as text, if provided supersedes cent amount **/
        @SerializedName("amount_text")
        private String mAmountText;
        /** Help text, if provided we display clickable question mark next to the label **/
        @SerializedName("help_text")
        private String mHelpText;

        @NonNull
        public ItemType getType()
        {
            return mType != null ? mType : ItemType.DEFAULT;
        }

        @Nullable
        public String getLabel()
        {
            return mLabel;
        }

        @Nullable
        public Long getAmountCents()
        {
            return mAmountCents;
        }

        @Nullable
        public String getAmountText()
        {
            return mAmountText;
        }

        @Nullable
        public String getHelpText()
        {
            return mHelpText;
        }

        public boolean hasHelpText()
        {
            return mHelpText == null || mHelpText.isEmpty();
        }

    }


    public static String EXAMPLE_JSON = ""
            + "{"
            + "  \"bill\":{"
            + "    \"header_title\":\"Cleaning Plan: Every 2 weeks\","
            + "    \"header_text\":\"Starting Wed, Jan 10/n8:00 am\","
            + "    \"final_price_value\":5100,"
            + "    \"currency_symbol\":\"$\","
            + "    \"sections\":["
            + "      {"
            + "        \"line_items\":["
            + "          {"
            + "            \"label\":\"Cleaning + 1 extra\","
            + "            \"amount\":7500"
            + "          },"
            + "          {"
            + "          \"label\":\"Coupon\","
            + "          \"amount\":-1750"
            + "          },"
            + "          {"
            + "            \"label\":\"Trust & Support Fee\","
            + "            \"amount\":3,"
            + "            \"help_text\":\"It has to be done...\""
            + "          },"
            + "          {"
            + "            \"label\":\"Credits\","
            + "            \"amount\":-1000"
            + "          }"
            + "        ]"
            + "      },"
            + "      {"
            + "        \"line_items\":["
            + "          {"
            + "            \"type\":\"TOTAL_PRICE\","
            + "            \"label\":\"Today's Total\","
            + "            \"amount\":5100"
            + "          },"
            + "          {"
            + "            \"label\":\"Credits\","
            + "            \"amount\":-5100"
            + "          }"
            + "        ]"
            + "      }"
            + "    ]"
            + "  }"
            + "}";

}
