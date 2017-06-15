package com.handybook.handybook.library.ui.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.handybook.handybook.R;

import butterknife.BindView;

/**
 * A horizontal linear layout with two columns, which display the label and value of a field
 * respectively
 */
public class LabelValueView extends InjectedLinearLayout //TODO: rename this to something better
{

    @BindView(R.id.label_value_view_label_text)
    TextView mLabelText;
    @BindView(R.id.label_value_view_value_text)
    TextView mValueText;

    public LabelValueView(final Context context) {
        super(context);
    }

    public LabelValueView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public LabelValueView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    public static LabelValueView newInstance(
            Activity activity,
            final String label,
            final String value
    ) {
        LabelValueView labelValueView =
                (LabelValueView) activity.getLayoutInflater().inflate(
                        R.layout.element_label_value_view, null);
        labelValueView.setLabelAndValueText(label, value);
        return labelValueView;
    }

    public void setLabelText(final String labelString) {
        mLabelText.setText(labelString);
    }

    public void setValueText(final String valueString) {
        mValueText.setText(valueString);
    }

    public void setLabelAndValueText(final String labelString, final String valueString) {
        setLabelText(labelString);
        setValueText(valueString);
    }
}
