package com.handybook.handybook;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

import java.io.File;
import java.util.Properties;

/**
 * Created by jwilliams on 3/2/15.
 */
public class HandyRobolectricTestRunner extends RobolectricTestRunner {

    public HandyRobolectricTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestPath = "../app/src/main/AndroidManifest.xml";
        String resPath = "../../build/intermediates/res/prod/"+BuildConfig.BUILD_TYPE;

        // android studio has a different execution root for tests than pure gradle
        // so we avoid here manual effort to get them running inside android studio
        if (!new File(manifestPath).exists()) {
            manifestPath = "app/" + manifestPath;
        }

        config = overwriteConfig(config, "manifest", manifestPath);
        config = overwriteConfig(config, "resourceDir", resPath);
        AndroidManifest manifest = super.getAppManifest(config);
        manifest.setPackageName("com.handybook.handybook");
        return manifest;
    }

    private Config.Implementation overwriteConfig(
            Config config, String key, String value) {
        Properties properties = new Properties();
        properties.setProperty(key, value);
        return new Config.Implementation(config,
                Config.Implementation.fromProperties(properties));
    }
}
