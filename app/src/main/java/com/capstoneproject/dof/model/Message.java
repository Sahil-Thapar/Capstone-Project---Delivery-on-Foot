package com.capstoneproject.dof.model;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.name;

/**
 * Created by mandeepkaur on 2017-11-25.
 */

public class Message {

    private String receiverName;
    private String receiverPhone;
    private String pickupAddress;
    private String deliveryAddress;
    private String deliveryDateTime;
    private String posterContact;
    private String status;


    public Message(){}

    public Message(String receiverName, String receiverPhone, String pickupAddress, String deliveryAddress, String deliveryDateTime, String posterContact, String status) {
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.pickupAddress = pickupAddress;
        this.deliveryAddress = deliveryAddress;
        this.deliveryDateTime = deliveryDateTime;
        this.posterContact = posterContact;
        this.status = status;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getPickupAddress() {
        return pickupAddress;
    }

    public void setPickupAddress(String pickupAddress) {
        this.pickupAddress = pickupAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDeliveryDateTime() {
        return deliveryDateTime;
    }

    public void setDeliveryDateTime(String deliveryDateTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public String getPosterContact() {
        return posterContact;
    }

    public void setPosterContact(String posterContact) {
        this.posterContact = posterContact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("receiverName",receiverName);
        result.put("receiverPhone", receiverPhone);
        result.put("pickupAddress", pickupAddress);
        result.put("deliveryAddress", deliveryAddress);
        result.put("deliveryDateTime", deliveryDateTime);
        result.put("posterContact", posterContact);


        return result;
    }
}
