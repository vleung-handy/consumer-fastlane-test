package com.handybook.handybook;

import android.os.Build;
import android.view.View;
import android.view.animation.Animation;

import com.handybook.handybook.core.TestBaseApplication;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

@Ignore
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.JELLY_BEAN,
        constants = BuildConfig.class,
        application = TestBaseApplication.class,
        packageName = "com.handybook.handybook")
public class RobolectricGradleTestWrapper
{
    protected void preventAnimationStart(final Object parent, final String viewFieldName)
    {
        try
        {
            final Field viewField = parent.getClass().getDeclaredField(viewFieldName);
            viewField.setAccessible(true);
            final View view = (View) viewField.get(parent);
            final View spy = spy(view);
            doNothing().when(spy).startAnimation(any(Animation.class));
            viewField.set(parent, spy);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
