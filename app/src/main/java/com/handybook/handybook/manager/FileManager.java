package com.handybook.handybook.manager;

import android.content.Context;
import android.util.Log;

import com.handybook.handybook.core.BaseApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sng on 10/4/16.
 */

public class FileManager
{
    private static final String TAG = FileManager.class.getSimpleName();
    private static final String LOG_PATH = "handylogs";
    private static final File FILES_DIRECTORY = BaseApplication.getContext().getFilesDir();
    private final File mLogDirectory;

    public FileManager()
    {
        mLogDirectory = new File(FILES_DIRECTORY, LOG_PATH);
        if (!mLogDirectory.exists())
        { mLogDirectory.mkdirs(); }
    }

    public File[] getLogFileList()
    {
        return mLogDirectory.listFiles();
    }

    public String readLogFile(String fileName)
    {
        return readFile(LOG_PATH + fileName);
    }

    public boolean saveLogFile(String fileName, String fileContent)
    {
        //This was simplest way to save in sub directory
        return saveFile(new File(mLogDirectory, fileName), fileContent);
    }

    public void deleteLogFile(String fileName)
    {
        new File(mLogDirectory, fileName).delete();
    }

    public String readFile(File file)
    {
        StringBuffer buffer = null;
        BufferedReader input = null;
        try
        {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            buffer = new StringBuffer();
            while ((line = input.readLine()) != null)
            {
                buffer.append(line);
            }

            Log.d(TAG, buffer.toString());
        }
        catch (IOException e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return buffer == null ? "" : buffer.toString();
    }

    public String readFile(String fileName)
    {
        return readFile(new File(
                FILES_DIRECTORY,
                fileName
        )); // Pass getFilesDir() and "MyFile" to read file
    }

    public boolean saveFile(File file, String fileContent)
    {
        FileOutputStream outputStream = null;

        try
        {
            if (!file.exists())
            {
                file.createNewFile();  // if file already exists will do nothing
            }

            outputStream = new FileOutputStream(file);
            outputStream.write(fileContent.getBytes());
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }
        finally
        {
            try
            {
                if (outputStream != null)
                { outputStream.close(); }
            }
            catch (IOException e)
            {
                //ignore
            }
        }

        return false;
    }

    /**
     * @param fileName
     * @param fileContent
     * @return
     */
    public boolean saveFile(String fileName, String fileContent)
    {
        FileOutputStream outputStream;

        try
        {
            outputStream = BaseApplication.getContext()
                                          .openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileContent.getBytes());
            outputStream.close();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return false;
    }
}
