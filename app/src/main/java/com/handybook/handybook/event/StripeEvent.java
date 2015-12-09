package com.handybook.handybook.event;


import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public class StripeEvent
{
    public static class RequestCreateToken
    {
        private final Card mCard;

        public RequestCreateToken(final Card card)
        {
            mCard = card;
        }

        public Card getCard()
        {
            return mCard;
        }
    }


    public static class ReceiveCreateTokenSuccess
    {
        private final Token mToken;

        public ReceiveCreateTokenSuccess(final Token token)
        {
            mToken = token;
        }

        public Token getToken()
        {
            return mToken;
        }
    }


    public static class ReceiveCreateTokenError
    {
        private final Exception mError;

        public ReceiveCreateTokenError(final Exception error)
        {
            mError = error;
        }

        public Exception getError()
        {
            return mError;
        }
    }
}
