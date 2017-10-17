package com.emos.vcm.model;

import java.util.Date;

public class Cargo {
    private double maxVolume;
    private double maxWeight;
    private Date pickupTime;
    private Date deliveryTime;
    private double pickupLat;
    private double pickupLng;
    private double deliveryLat;
    private double deliveryLng;

    public Cargo(double maxVolume, double maxWeight, Date pickupTime, Date deliveryTime, double pickupLat, double pickupLng, double deliveryLat, double deliveryLng) {
        this.maxVolume = maxVolume;
        this.maxWeight = maxWeight;
        this.pickupTime = pickupTime;
        this.deliveryTime = deliveryTime;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.deliveryLat = deliveryLat;
        this.deliveryLng = deliveryLng;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Date getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(Date pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public double getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(double pickupLat) {
        this.pickupLat = pickupLat;
    }

    public double getPickupLng() {
        return pickupLng;
    }

    public void setPickupLng(double pickupLng) {
        this.pickupLng = pickupLng;
    }

    public double getDeliveryLat() {
        return deliveryLat;
    }

    public void setDeliveryLat(double deliveryLat) {
        this.deliveryLat = deliveryLat;
    }

    public double getDeliveryLng() {
        return deliveryLng;
    }

    public void setDeliveryLng(double deliveryLng) {
        this.deliveryLng = deliveryLng;
    }


}
