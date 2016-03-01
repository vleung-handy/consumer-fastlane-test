package com.handybook.handybook.event;


import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

public abstract class StripeEvent
{
    public static class RequestCreateToken
    {
        private final Card mCard;
        private final String mStripeKey;

        public RequestCreateToken(final Card card, final String stripeKey)
        {
            mCard = card;
            mStripeKey = stripeKey;
        }

        public Card getCard()
        {
            return mCard;
        }

        public String getStripeKey()
        {
            return mStripeKey;
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
