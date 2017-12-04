package com.capstoneproject.dof.model;

import java.util.HashMap;
import java.util.Map;

import static android.R.attr.name;

/**
 * Created by mandeepkaur on 2017-11-21.
 */

public class Bid {

    private String id;
    private String userId;
    private String itemId;
    private double bidAmount;
    private String status;
    private String posterId;


    public Bid(){

    }
    public Bid(String id, String userId, String itemId, double bidAmount, String status) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.status = status;
    }

    public Bid(String id, String itemId, double bidAmount, String posterId, String status) {
        this.id = id;
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.posterId = posterId;
        this.status = status;
    }

    public Bid(String id, String userId, String itemId, double bidAmount, String status, String posterId) {
        this.id = id;
        this.userId = userId;
        this.itemId = itemId;
        this.bidAmount = bidAmount;
        this.status = status;
        this.posterId = posterId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(double bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPosterId() {
        return posterId;
    }

    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", status);

//        result.put("password", password);
//        result.put("listing", listing);

        return result;
    }
}
