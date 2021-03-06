package com.handybook.handybook.helpcenter.model;

import com.handybook.handybook.core.data.DataManager;
import com.handybook.handybook.core.event.HandyEvent;
import com.handybook.handybook.core.model.response.HelpCenterResponse;

import retrofit.mime.TypedInput;

public abstract class HelpEvent {

    //Help Self Service Center
    public static class RequestHelpNode extends HandyEvent {

        public String nodeId;
        public String bookingId;

        public RequestHelpNode(String nodeId, String bookingId) {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpNodeSuccess extends HandyEvent.ReceiveSuccessEvent {

        public HelpNode helpNode;

        public ReceiveHelpNodeSuccess(HelpNode helpNode) {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpNodeError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveHelpNodeError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    //Help Booking Node - help node associated with a particular booking
    public static class RequestHelpBookingNode extends HandyEvent {

        public String nodeId;
        public String bookingId;

        public RequestHelpBookingNode(String nodeId, String bookingId) {
            this.nodeId = nodeId;
            this.bookingId = bookingId;
        }
    }


    public static class ReceiveHelpBookingNodeSuccess extends HandyEvent.ReceiveSuccessEvent {

        public HelpNode helpNode;

        public ReceiveHelpBookingNodeSuccess(HelpNode helpNode) {
            this.helpNode = helpNode;
        }
    }


    public static class ReceiveHelpBookingNodeError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveHelpBookingNodeError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    //Help Contact Message
    public static class RequestNotifyHelpContact extends HandyEvent {

        public TypedInput body;

        public RequestNotifyHelpContact(TypedInput body) {
            this.body = body;
        }
    }


    public static class ReceiveNotifyHelpContactSuccess extends HandyEvent.ReceiveSuccessEvent {

        public ReceiveNotifyHelpContactSuccess() {
        }
    }


    public static class ReceiveNotifyHelpContactError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveNotifyHelpContactError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }


    //Help Center
    public static class RequestHelpCenter extends HandyEvent {

        public RequestHelpCenter() { }
    }


    public static class ReceiveHelpCenterSuccess extends HandyEvent.ReceiveSuccessEvent {

        public HelpCenterResponse helpCenterResponse;

        public ReceiveHelpCenterSuccess(HelpCenterResponse helpCenterResponse) {
            this.helpCenterResponse = helpCenterResponse;
        }
    }


    public static class ReceiveHelpCenterError extends HandyEvent.ReceiveErrorEvent {

        public ReceiveHelpCenterError(DataManager.DataManagerError error) {
            this.error = error;
        }
    }
}
