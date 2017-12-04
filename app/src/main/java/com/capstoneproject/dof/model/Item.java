package com.capstoneproject.dof.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeepkaur on 2017-11-18.
 */

public class Item {
    private String id;
    private String imageUrl;
    private String name;
    private double value;
    private String description;
    private String pickUpAddress;
    private String deliveryAddress;
    private String date;
    private String time;
    private String status;
    private String userId;
    private HashMap<String,Item> bidder;

    public Item(){

    }

    public Item(String id, String imageUrl, String name, double value, String description, String pickUpAddress, String deliveryAddress,
                String date, String time,String status) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.value = value;
        this.description = description;
        this.pickUpAddress = pickUpAddress;
        this.deliveryAddress = deliveryAddress;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<String, Item> getBidder() {
        return bidder;
    }

    public void setBidder(HashMap<String, Item> bidder) {
        this.bidder = bidder;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("imageUrl",imageUrl);
        result.put("name", name);
        result.put("value", value);
        result.put("description", description);
        result.put("pickUpAddress", pickUpAddress);
        result.put("deliveryAddress", deliveryAddress);
        result.put("date", date);
        result.put("time", time);
        result.put("status", status);


        return result;
    }
}
