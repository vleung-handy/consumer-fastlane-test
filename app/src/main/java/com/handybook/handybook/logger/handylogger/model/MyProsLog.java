package com.handybook.handybook.logger.handylogger.model;

import com.handybook.handybook.logger.handylogger.constants.EventContext;
import com.handybook.handybook.logger.handylogger.constants.EventType;

public abstract class MyProsLog extends EventLog {

    private MyProsLog(final String eventType) {
        super(eventType, EventContext.MY_PROS);
    }

    public static class ProTeamQuestionMarkButtonTapped extends MyProsLog {

        public ProTeamQuestionMarkButtonTapped() {
            super(EventType.PRO_TEAM_QUESTION_MARK_TAPPED);
        }
    }

    public static class ChooseFavoriteProButtonTapped extends MyProsLog {

        public ChooseFavoriteProButtonTapped() {
            super(EventType.CHOOSE_FAVORITE_PRO_BUTTON_TAPPED);
        }
    }
}
