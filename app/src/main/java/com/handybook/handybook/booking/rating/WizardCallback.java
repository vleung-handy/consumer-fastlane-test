package com.handybook.handybook.booking.rating;

import java.io.Serializable;

/**
 * Created by jtse on 3/30/16.
 */
public interface WizardCallback extends Serializable
{
    void done(BaseWizardFragment callerFragment);
}
