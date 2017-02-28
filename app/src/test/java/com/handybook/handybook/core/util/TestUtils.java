package com.handybook.handybook.core.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.List;

public class TestUtils {

    public static Fragment getScreenFragment(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();
        for (int i = fragments.size() - 1; i >= 0; --i) {
            if (fragments.get(i) != null) { return fragments.get(i); }
        }
        return null;
    }
}
