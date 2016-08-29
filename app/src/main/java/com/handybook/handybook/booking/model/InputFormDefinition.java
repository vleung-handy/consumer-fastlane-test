package com.handybook.handybook.booking.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * model from server that defines an input form
 *
 * see {@link EntryMethodOption}
 */
public class InputFormDefinition implements Serializable
{
    //more fields may be added at this level, like title

    @SerializedName("input_fields")
    private List<InputFormField> mFieldDefinitions;

    public List<InputFormField> getFieldDefinitions()
    {
        return mFieldDefinitions;
    }

    public static class InputFormField implements Serializable
    {
        public static class SupportedMachineName
        {
            public static final String LOCKBOX_ACCESS_CODE = "lockbox_code";
            public static final String LOCKBOX_LOCATION = "key_location";
            public static final String DESCRIPTION = "key_location";
        }
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
         * whether input is required for this field
         */
        @SerializedName("required")
        private boolean mRequired;

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
    }
}
