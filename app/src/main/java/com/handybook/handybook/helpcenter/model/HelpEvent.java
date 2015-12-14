package com.handybook.handybook.helpcenter.model;

import com.handybook.handybook.annotation.Track;
import com.handybook.handybook.data.DataManager;
import com.handybook.handybook.event.HandyEvent;

import retrofit.mime.TypedInput;

public class HelpEvent
{
    //Help Self Service Center
    public static class RequestHelpNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpNodeSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpNodeError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveHelpNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Help Booking Node - help node associated with a particular booking
    public static class RequestHelpBookingNode extends HandyEvent
    {
        public String nodeId;
        public String bookingId;

        public RequestHelpBookingNode(String nodeId, String bookingId)
        {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpBookingNodeSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public HelpNode helpNode;

        public ReceiveHelpBookingNodeSuccess(HelpNode helpNode)
        {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpBookingNodeError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveHelpBookingNodeError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }


    //Help Contact Message
    @Track("pro help contact form submitted")
    public static class RequestNotifyHelpContact extends HandyEvent
    {
        public TypedInput body;

        public RequestNotifyHelpContact(TypedInput body)
        {
            this.body = body;
        }
    }


    public static class ReceiveNotifyHelpContactSuccess extends HandyEvent.ReceiveSuccessEvent
    {
        public ReceiveNotifyHelpContactSuccess()
        {
        }
    }


    public static class ReceiveNotifyHelpContactError extends HandyEvent.ReceiveErrorEvent
    {
        public ReceiveNotifyHelpContactError(DataManager.DataManagerError error)
        {
            this.error = error;
        }
    }
}
