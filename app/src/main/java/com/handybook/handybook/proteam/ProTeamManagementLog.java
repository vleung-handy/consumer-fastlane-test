package com.handybook.handybook.proteam;

import android.support.annotation.StringDef;

import com.google.gson.annotations.SerializedName;
import com.handybook.handybook.logger.handylogger.model.EventLog;

import java.util.List;

import static com.handybook.handybook.logger.handylogger.constants.EventContext.PROTEAM_MANAGEMENT;
import static com.handybook.handybook.logger.handylogger.constants.EventType.REFERRAL_PAGE_SHOWN;

public class ProTeamManagementLog extends EventLog {

    @StringDef({PROTEAM_MANAGEMENT})
    @interface EventContext {}


    @StringDef(REFERRAL_PAGE_SHOWN)
    @interface EventType {}


    @SerializedName("provider_ids")
    private List<Integer> mProviderIds;

    public ProTeamManagementLog(
            @EventType String eventType,
            @EventContext String eventContext,
            List<Integer> providerIds
    ) {
        super(eventType, eventContext);

        mProviderIds = providerIds;
    }

}
