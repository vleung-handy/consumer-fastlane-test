package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import antistatic.spinnerwheel.AbstractWheel;
import antistatic.spinnerwheel.OnWheelChangedListener;
import antistatic.spinnerwheel.WheelHorizontalView;

final class BookingOptionsSpinnerView extends FrameLayout {
    private WheelHorizontalView optionsSpinner;
    private TextView warningText;
    private HashMap<Integer, ArrayList<String>> warningsMap;
    private HashMap<Integer, ArrayList<String>> childMap;
    private OnItemUpdatedListener itemUpdateListener;
    private String[] optionsList;
    private ArrayList<String> prevChildList;

    BookingOptionsSpinnerView(final Context context, final BookingOption option,
                              final OnItemUpdatedListener itemUpdateListener) {
        super(context);
        init(context, option, itemUpdateListener);
    }

    BookingOptionsSpinnerView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init(context, null, null);
    }

    BookingOptionsSpinnerView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        init(context, null, null);
    }

    private void init(final Context context, final BookingOption option,
                      final OnItemUpdatedListener itemUpdateListener) {
        this.itemUpdateListener = itemUpdateListener;

        LayoutInflater.from(context).inflate(R.layout.view_booking_options, this);

        final TextView titleText = (TextView)this.findViewById(R.id.title_text);
        final TextView infoText = (TextView)this.findViewById(R.id.info_text);

        optionsSpinner = (WheelHorizontalView)this.findViewById(R.id.options_spinner);
        warningText = (TextView)this.findViewById(R.id.warning_text);
        warningsMap = new HashMap<>();
        childMap = new HashMap<>();

        final String type = option.getType();
        if (!type.equals("quantity") && !type.equals("option_picker")) return;

        titleText.setText(option.getTitle());

        final String info = option.getInfo();
        if (info != null) {
            infoText.setText(info);
            infoText.setVisibility(VISIBLE);
        }

        if (type.equals("quantity")) {
            final String[] opts = option.getOptions();
            final ArrayList<String> list = new ArrayList<>();
            final int min = Integer.parseInt(opts[0]);
            final int max = Integer.parseInt(opts[1]);
            for (int i = min; i <= max; i++) list.add(Integer.toString(i));
            optionsList = list.toArray(new String[list.size()]);
        }
        else optionsList = option.getOptions();

        optionsSpinner.setViewAdapter(new OptionsAdapter<>(context, optionsList,
                R.layout.view_spinner_option, R.id.text));

        optionsSpinner.setCurrentItem(Integer.parseInt(option.getDefaultValue()));
        optionsSpinner.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                if (itemUpdateListener != null) itemUpdateListener
                        .onUpdate(BookingOptionsSpinnerView.this);
            }
        });

        String[][] warnings;
        if ((warnings = option.getWarnings()) != null) {
            for (final String[] warningList : warnings) {
                if (warningList.length < 2) continue;

                String warningText = warningList[0];
                for (int i = 1; i < warningList.length; i ++) {
                    final int index = Integer.parseInt(warningList[i]);
                    ArrayList<String> list = warningsMap.get(index);

                    if (list == null) list = new ArrayList<>();
                    list.add(warningText);
                    warningsMap.put(index, list);
                }
            }

            optionsSpinner.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(final AbstractWheel wheel, final int oldValue,
                                      final int newValue) {
                    handleWarnings(optionsSpinner.getCurrentItem());
                }
            });
            handleWarnings(optionsSpinner.getCurrentItem());
        }

        String[][] children = option.getChildren();
        if (children != null) {
            for (final String[] childrenList : children) {
                if (childrenList.length < 2) continue;

                final String child = childrenList[0];
                for (int i = 1; i < childrenList.length; i ++) {
                    final int index = Integer.parseInt(childrenList[i]);
                    ArrayList<String> list = childMap.get(index);

                    if (list == null) list = new ArrayList<>();
                    list.add(child);
                    childMap.put(Integer.parseInt(childrenList[i]), list);
                }
            }

            optionsSpinner.addChangingListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(AbstractWheel wheel, int oldValue, int newValue) {
                    handleChildren(optionsSpinner.getCurrentItem());
                }
            });
            handleChildren(optionsSpinner.getCurrentItem());
        }
    }

    final String getCurrentItem() {
        return optionsList[optionsSpinner.getCurrentItem()];
    }

    final void setCurrentIndex(final int index) {
        optionsSpinner.setCurrentItem(index);
        invalidate();
        requestLayout();
    }

    final int getCurrentIndex() {
        return optionsSpinner.getCurrentItem();
    }

    final void hideSeperator() {
        final View view = this.findViewById(R.id.layout);
        view.setBackgroundResource((R.drawable.booking_cell_last));
        invalidate();
        requestLayout();
    }

    final void handleWarnings(final int item) {
        final ArrayList<String> warningList = warningsMap.get(item);

        if (warningList != null && warningList.size() > 0) {
            String warnings = "";
            final int size = warningList.size();
            for (int i = 0; i < size; i++) {
                warnings += warningList.get(i) + (i < size- 1 ? "\n\n" : "");
            }

            warningText.setText(warnings);
            warningText.setVisibility(VISIBLE);
        }
        else warningText.setVisibility(GONE);

        invalidate();
        requestLayout();
    }

    final void handleChildren(final int item) {
        final ArrayList<String> childList = childMap.get(item);

        if (childList != null && childList.size() > 0) {
            prevChildList = childList;
            if (itemUpdateListener != null) itemUpdateListener.onShowChildren(this, childList
                    .toArray(new String[childList.size()]));
        }
        else if (prevChildList != null) {
            if (itemUpdateListener != null) itemUpdateListener.onHideChildren(this, prevChildList
                    .toArray(new String[prevChildList.size()]));
        }
    }

    static interface OnItemUpdatedListener {
        void onUpdate (BookingOptionsSpinnerView view);
        void onShowChildren (BookingOptionsSpinnerView view,  String[] items);
        void onHideChildren (BookingOptionsSpinnerView view, String[] items);
    }
}
