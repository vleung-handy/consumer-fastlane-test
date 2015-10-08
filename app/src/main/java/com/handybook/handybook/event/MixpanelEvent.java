package com.handybook.handybook.event;

public abstract class MixpanelEvent
{
    public enum Token
    {

        APP_TRACK_INSTALL("App Track Install"),
        APP_TRACK_LOCATION("App Track Location"),
        APP_TRACK_DETAILS("App Track Details"),
        APP_TRACK_TIME("App Track Time"),
        APP_TRACK_CONTACT("App Track Contact"),
        APP_TRACK_LOG_IN("App Track Log In"),
        APP_TRACK_REQUEST_PRO("App Track Request Pro"),
        APP_TRACK_COMMENTS("App Track Comments"),
        APP_TRACK_FREQUENCY("App Track Frequency"),
        APP_TRACK_EXTRAS("App Track Extras"),
        APP_TRACK_ADDRESS("App Track Address"),
        APP_TRACK_PAYMENT("App Track Payment"),
        APP_TRACK_CONFIRMATION("App Track Confirmation");

        private String eventToken;

        Token(final String eventString)
        {
            this.eventToken = eventString;
        }


        @Override
        public String toString()
        {
            return this.eventToken;
        }
    }

    private MixpanelEvent(final String eventType)
    {
    }


    public MixpanelEvent get(final Token token)
    {
        return new MixpanelEvent(token.toString())
        {
            @Override
            public MixpanelEvent get(Token token)
            {
                return super.get(token);
            }
        };
    }

}
