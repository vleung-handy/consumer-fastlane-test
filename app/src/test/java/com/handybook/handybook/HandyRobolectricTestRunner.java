package com.handybook.handybook;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

/**
 * Created by jwilliams on 3/2/15.
 */
public class HandyRobolectricTestRunner extends RobolectricTestRunner {

    public HandyRobolectricTestRunner(final Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {

        String myAppPath = HandyRobolectricTestRunner.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String manifestPath = myAppPath + "../../../manifests/full/prod/debug/AndroidManifest.xml";
        String resPath = myAppPath + "../../../res/prod/debug";
//        String assetPath = myAppPath + "../../../assets/debug";
        return new AndroidManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath)) {
            @Override
            public int getTargetSdkVersion() {
                return 18;
            }
        };
    }
}
