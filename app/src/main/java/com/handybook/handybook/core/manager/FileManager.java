package com.handybook.handybook.core.manager;

import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by sng on 10/4/16.
 */

public class FileManager {

    private static final String TAG = FileManager.class.getSimpleName();
    private static final String LOG_PATH = "handylogs";
    private final File mFilesDirectory;
    private final File mLogDirectory;

    public FileManager(Context context) {
        mFilesDirectory = context.getFilesDir();
        mLogDirectory = new File(mFilesDirectory, LOG_PATH);
        makeLogsDirectoryIfNotExist();
    }

    /**
     * exception handled, won't cause a crash
     *
     * @return number of free bytes in internal storage directory. -1 if unable to get
     */
    public long getInternalStorageDirectoryFreeSpaceBytes() {
        try {
            return mFilesDirectory.getFreeSpace();
        }
        catch (Exception e) {
            Crashlytics.logException(e);
        }
        return -1L;
    }

    public File[] getLogFileList() {
        makeLogsDirectoryIfNotExist();
        return mLogDirectory.listFiles();
    }

    public boolean saveLogFile(String fileName, String fileContent) {
        makeLogsDirectoryIfNotExist();
        //This was simplest way to save in sub directory
        return saveFile(new File(mLogDirectory, fileName), fileContent);
    }

    public void deleteLogFile(String fileName) {
        makeLogsDirectoryIfNotExist();
        new File(mLogDirectory, fileName).delete();
    }

    public String readFile(File file) {
        StringBuffer buffer = null;
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            buffer = new StringBuffer();
            while ((line = input.readLine()) != null) {
                buffer.append(line);
            }

            Log.d(TAG, buffer.toString());
        }
        catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return buffer == null ? "" : buffer.toString();
    }

    public boolean saveFile(File file, String fileContent) {
        FileOutputStream outputStream = null;

        try {
            if (!file.exists()) {
                file.createNewFile();  // if file already exists will do nothing
            }

            outputStream = new FileOutputStream(file);
            outputStream.write(fileContent.getBytes());
            return true;
        }
        catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            Crashlytics.log(e.getLocalizedMessage());
        }
        finally {
            try {
                if (outputStream != null) { outputStream.close(); }
            }
            catch (IOException e) {
                //ignore
            }
        }

        return false;
    }

    private void makeLogsDirectoryIfNotExist() {
        if (!mLogDirectory.exists()) {
            //if log directory isn't created, log
            if (!mLogDirectory.mkdirs()) {
                Crashlytics.log("couldn't make log directory: " + mLogDirectory.getAbsolutePath());
            }
        }
    }
}
