package com.handybook.handybook.core.model.bill;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Example Bill Payload:
 * {
 *   "header_title":"Cleaning Plan: Every 2 weeks",
 *   "header_text":"Starting Wed, Jan 10/n8:00 am",
 *   "final_amount_cents":5100,
 *   "currency_symbol":"$",
 *   "sections":[
 *     {
 *     "line_items":[
 *       {
 *         "label":"Cleaning + 1 extra",
 *         "amount_cents":7500
 *       },
 *       {
 *         "label":"Coupon",
 *         "amount_cents":-1750
 *       },
 *       {
 *         "label":"Trust & Support Fee",
 *         "amount_cents":3,
 *         "help_text":"It has to be done..."
 *       },
 *       {
 *         "label":"Credits",
 *         "amount_cents":-1000
 *       }
 *     ]
 *     },
 *     {
 *       "line_items":[
 *         {
 *           "type":"TOTAL_PRICE",
 *           "label":"Today's Total",
 *           "amount_cents":5100
 *         },
 *         {
 *           "label":"Credits",
 *           "amount_cents":-5100
 *         }
 *       ]
 *     }
 *   ]
 * }
 */
public class Bill implements Serializable
{

    @SerializedName("header_title")
    private String mHeaderTitle;
    @SerializedName("header_text")
    private String mHeaderText;
    @SerializedName("final_amount_cents")
    private Integer mFinalPriceValueCents;
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
    public Integer getFinalPriceValueCents()
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

    @Override
    public String toString()
    {
        return new Gson().toJson(this);
    }

    /**
     * Example BillSection payload:
     * {
     *   "type":"ITEMIZED_BILL",
     *   "line_items":[
     *     {
     *       "label":"Cleaning + 1 extra",
     *       "amount_cents":7500
     *     },
     *     {
     *       "label":"Coupon",
     *        "amount_cents":-1750
     *     },
     *     {
     *       "label":"Trust & Support Fee",
     *       "amount_cents": 3,
     *       "help_text": "It has to be done..."
     *     },
     *     {
     *       "label":"Credits",
     *       "amount_cents":-1000
     *     },
     *     {
     *       "type":"LARGE_PRICE",
     *       "label":"Today's Total",
     *       "amount_cents":5600
     *     }
     *   ]
     * }
     */
    public static class BillSection implements Serializable
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

        @NonNull
        public ArrayList<BillLineItem> getLineItems()
        {
            return mLineItems == null ? new ArrayList<BillLineItem>() : mLineItems;
        }

        public boolean isEmpty()
        {
            return mLineItems == null || mLineItems.isEmpty();
        }

        @Override
        public String toString()
        {
            return new Gson().toJson(this);
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
    public static class BillLineItem implements Serializable
    {
        public enum ItemType
        {
            @SerializedName("DEFAULT")
            DEFAULT,
            @SerializedName("LARGE_PRICE")
            LARGE_PRICE;

        }


        /**
         * One of valid types, otherwise defaults to DEFAULT
         **/
        @SerializedName("type")
        private ItemType mType;

        /**
         * Line item label
         **/
        @SerializedName("label")
        private String mLabel;
        /**
         * Amount In Cents
         **/
        @SerializedName("amount_cents")
        private Integer mAmountCents;
        /**
         * Amount as text, if provided supersedes cent amount
         **/
        @SerializedName("amount_text")
        private String mAmountText;
        /**
         * Help text, if provided we display clickable question mark next to the label
         **/
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
        public Integer getAmountCents()
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
            return mHelpText != null;
        }

        public boolean hasAmountText()
        {
            return mAmountText != null;
        }

        @Override
        public String toString()
        {
            return new Gson().toJson(this);
        }
    }


    public static String TEST_PAYLOAD_JSON = ""
            + "{\n"
            + "  \"header_title\":\"Cleaning Plan: Every 2 weeks\",\n"
            + "  \"header_text\":\"Starting Wed, Jan 10\\n8:00 am\",\n"
            + "  \"final_amount_cents\":12345,\n"
            + "  \"currency_symbol\":\"$\",\n"
            + "  \"sections\":[\n"
            + "    {\n"
            + "      \"line_items\":[\n"
            + "        {\n"
            + "          \"label\":\"Cleaning + 1 extra\",\n"
            + "          \"amount_cents\":7500\n"
            + "        },\n"
            + "        {\n"
            + "        \"label\":\"Coupon\",\n"
            + "        \"amount_cents\":-1740,\n"
            + "        \"help_text\":\"Coupons are good,\\nMkay?!...\"\n"
            + "        },\n"
            + "        {\n"
            + "          \"label\":\"Trust & Support Fee\",\n"
            + "          \"amount_cents\":300\n"
            + "        },\n"
            + "        {\n"
            + "          \"label\":\"Overriden default\",\n"
            + "          \"amount_text\":\"OvErRiDe\",\n"
            + "          \"help_text\":\"It has to be done...\"\n"
            + "        },\n"
            + "        {\n"
            + "          \"label\":\"Credits\",\n"
            + "          \"amount_cents\":-1020\n"
            + "        }\n"
            + "      ]\n"
            + "    },\n"
            + "    {\n"
            + "      \"line_items\":[\n"
            + "        {\n"
            + "          \"label\":\"Empty section below, beware!\",\n"
            + "          \"amount_text\":\"\"\n"
            + "        }\n"
            + "      ]\n"
            + "    },\n"
            + "    {\n"
            + "      \"line_items\":[]\n"
            + "    },\n"
            + "    {\n"
            + "      \"line_items\":[\n"
            + "        {\n"
            + "          \"type\":\"LARGE_PRICE\",\n"
            + "          \"label\":\"Today's Total\",\n"
            + "          \"amount_cents\":2030\n"
            + "        },\n"
            + "        {\n"
            + "          \"label\":\"Other credits\",\n"
            + "          \"amount_cents\":-3050\n"
            + "        },\n"
            + "        {\n"
            + "          \"type\":\"LARGE_PRICE\",\n"
            + "          \"label\":\"Overridden Total\",\n"
            + "          \"amount_text\":\"OvErRiDe\"\n"
            + "        }\n"
            + "      ]\n"
            + "    },\n"
            + "    {},\n"
            + "    {},\n"
            + "    {},\n"
            + "    {},\n"
            + "    {\n"
            + "      \"line_items\":[\n"
            + "        {\n"
            + "          \"label\":\"Number of empty sections above:\",\n"
            + "          \"amount_text\":\"4\"\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    public static String MOCK_PAYLOAD_JSON = ""
            + "{\n"
            + "  \"header_title\":\"Cleaning Plan: Every 2 weeks\",\n"
            + "  \"header_text\":\"Starting Wed, Jan 10\\n8:00 am\",\n"
            + "  \"final_amount_cents\":6050,\n"
            + "  \"currency_symbol\":\"$\",\n"
            + "  \"sections\":[\n"
            + "    {\n"
            + "      \"line_items\":[\n"
            + "        {\n"
            + "          \"label\":\"Cleaning + 1 extra\",\n"
            + "          \"amount_cents\":8500\n"
            + "        },\n"
            + "        {\n"
            + "        \"label\":\"Coupon\",\n"
            + "        \"amount_cents\":-1750\n"
            + "        },\n"
            + "        {\n"
            + "          \"label\":\"Trust & Support Fee\",\n"
            + "          \"amount_cents\":300,\n"
            + "          \"help_text\":\"We gots to charge you.\"\n"
            + "        },\n"
            + "        {\n"
            + "          \"label\":\"Credits\",\n"
            + "          \"amount_cents\":-1000\n"
            + "        }\n"
            + "      ]\n"
            + "    },\n"
            + "    {\n"
            + "      \"line_items\":[\n"
            + "        {\n"
            + "          \"type\":\"LARGE_PRICE\",\n"
            + "          \"label\":\"Today's Total\",\n"
            + "          \"amount_cents\":6050\n"
            + "        }\n"
            + "      ]\n"
            + "    }\n"
            + "  ]\n"
            + "}";

}
