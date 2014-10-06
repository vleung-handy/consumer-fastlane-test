package com.handybook.handybook;

public final class BaseDataManager implements DataManager {
    @Override
    public final String[] getServices() {
        return new String[]{"Category 1", "Category 2", "Category 3", "Category 4"};
    }
}
