package com.handybook.handybook.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 */
public class IoUtils
{
    public static String getJsonString(String filename) throws Exception
    {
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(filename);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}
