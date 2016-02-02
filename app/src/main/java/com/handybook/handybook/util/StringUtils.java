package com.handybook.handybook.util;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

public class StringUtils
{
    public static String capitalizeFirstCharacter(String s)
    {
        if(ValidationUtils.isNullOrEmpty(s)) return s;
        String returnValue = Character.toString(Character.toUpperCase(s.charAt(0)));
        if(s.length() > 1)
        {
            returnValue += s.substring(1);
        }
        return returnValue;
    }

    public static String readAssetFile(final Context ctx, final String filename)
    {
        InputStream input = null;
        try
        {
            input = ctx.getAssets().open(filename);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Reader reader = null;
        try
        {
            reader = new InputStreamReader(input, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        char buffer[] = new char[16384];  // read 16k blocks
        int len; // how much content was read?
        try
        {
            while ((len = reader.read(buffer)) > 0)
            {
                sb.append(buffer, 0, len);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        try
        {
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
