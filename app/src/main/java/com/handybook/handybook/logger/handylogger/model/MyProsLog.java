package com.handybook.handybook.logger.handylogger.model;

public abstract class MyProsLog extends EventLog {

    private static final String EVENT_CONTEXT = "my_pros";

    private MyProsLog(final String eventType) {
        super(eventType, EVENT_CONTEXT);
    }

    public static class ProTeamQuestionMarkButtonTapped extends MyProsLog {

        private static final String EVENT_TYPE = "pro_team_question_mark_tapped";

        public ProTeamQuestionMarkButtonTapped() {
            super(EVENT_TYPE);
        }
    }

    public static class ChooseFavoriteProButtonTapped extends MyProsLog {

        private static final String EVENT_TYPE = "choose_a_favorite_pro_tapped";

        public ChooseFavoriteProButtonTapped() {
            super(EVENT_TYPE);
        }
    }
}
