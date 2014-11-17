package com.handybook.handybook;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

abstract class BookingOptionsIndexView extends BookingOptionsView {
    protected HashMap<Integer, ArrayList<String>> warningsMap;
    protected HashMap<Integer, ArrayList<String>> childMap;
    protected String[] optionsList;
    private ArrayList<String> prevChildList;
    private TextView titleText;
    private TextView warningText;

    BookingOptionsIndexView(final Context context, final int layout, final BookingOption option,
                            final OnUpdatedListener updateListener) {
        super(context, layout, option, updateListener);
        init();
    }

    BookingOptionsIndexView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    BookingOptionsIndexView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        warningsMap = new HashMap<>();
        childMap = new HashMap<>();

        titleText = (TextView)this.findViewById(R.id.title_text);
        final TextView infoText = (TextView)this.findViewById(R.id.info_text);
        warningText = (TextView)this.findViewById(R.id.warning_text);

        titleText.setText(option.getTitle());

        final String info = option.getInfo();
        if (info != null) {
            infoText.setText(info);
            infoText.setVisibility(VISIBLE);
        }

        if (option.getType().equals("quantity")) {
            final String[] opts = option.getOptions();
            final ArrayList<String> list = new ArrayList<>();
            final int min = Integer.parseInt(opts[0]);
            final int max = Integer.parseInt(opts[1]);
            for (int i = min; i <= max; i++) list.add(Integer.toString(i));
            optionsList = list.toArray(new String[list.size()]);
        }
        else optionsList = option.getOptions();

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
        }
    }

    protected void hideTitle() {
        titleText.setVisibility(GONE);
        invalidate();
        requestLayout();
    }

    protected void handleWarnings(final int item) {
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

    protected void handleChildren(final int item) {
        final ArrayList<String> childList = childMap.get(item);

        if (childList != null && childList.size() > 0) {
            prevChildList = childList;
            if (updateListener != null) updateListener.onShowChildren(this, childList
                    .toArray(new String[childList.size()]));
        }
        else if (prevChildList != null) {
            if (updateListener != null) updateListener.onHideChildren(this, prevChildList
                    .toArray(new String[prevChildList.size()]));
        }
    }

    abstract void setCurrentIndex(int index);

    abstract int getCurrentIndex();
}
