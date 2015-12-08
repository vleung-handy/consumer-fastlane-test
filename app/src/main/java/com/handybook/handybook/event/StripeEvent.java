package com.handybook.handybook.event;


import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class StripeEvent
{
    public static class RequestCreateToken
    {
        public final Card card;

        public RequestCreateToken(final Card card)
        {
            this.card = card;
        }
    }


    public static class ReceiveCreateTokenSuccess
    {
        public final Token token;

        public ReceiveCreateTokenSuccess(final Token token)
        {
            this.token = token;
        }
    }


    public static class ReceiveCreateTokenError
    {
        public final Exception error;

        public ReceiveCreateTokenError(final Exception error)
        {
            this.error = error;
        }
    }
}
