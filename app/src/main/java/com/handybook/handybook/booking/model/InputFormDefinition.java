package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * model from server that defines an input form
 * <p/>
 * see {@link EntryMethodOption}
 */
public class InputFormDefinition implements Serializable
{
    //more fields may be added at this level, like title

    @SerializedName("input_form_fields")
    private List<InputFormField> mFieldDefinitions;

    public List<InputFormField> getFieldDefinitions()
    {
        return mFieldDefinitions;
    }

    public static class InputFormField implements Serializable
    {
        @SerializedName("title")
        private String mTitle;

        @SerializedName("hint_text")
        private String mHintText;

        /**
         * the number of lines that the input field should take up
         */
        @SerializedName("num_lines")
        private Integer mNumLines;

        /**
         * used to map to field values when submitting an input form
         */
        @SerializedName("machine_name")
        private String mMachineName;

        /**
         * validation pattern in regex
         * <p/>
         * just using "required" for now as server won't be sending this yet
         */
        @SerializedName("validation_pattern")
        private String mValidationPattern;

        /**
         * the value of this field
         */
        @SerializedName("value")
        private String mValue;
        /**
         * whether input is required for this field
         */
        @SerializedName("required")
        private boolean mRequired;

        public String getTitle()
        {
            return mTitle;
        }

        public boolean isRequired()
        {
            return mRequired;
        }

        public String getMachineName()
        {
            return mMachineName;
        }

        public String getHintText()
        {
            return mHintText;
        }

        public int getNumLines()
        {
            return mNumLines == null ? 1 : mNumLines;
        }

        public String getValue()
        {
            return mValue;
        }
    }
}
