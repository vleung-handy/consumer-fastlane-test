package com.handybook.handybook.constant;

public enum PrefsKey
{
    USER("USER_OBJ");

    private String key;

    PrefsKey(String key)
    {
        this.key = key;
    }

    public String getKey()
    {
        return this.key;
    }

    @Override
    public String toString()
    {
        return this.getKey();
    }
}
