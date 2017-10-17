package com.emos.vcm.model;

import java.util.Date;

public class Vehicle {
    private int rank;
    private Date gpsUpTime;
    private double lat;
    private double lng;
    private double maxWeight;
    private double maxVolume;
    private double speed;
    private boolean isAvailable;
    private Date pickupTime;    // 到达取货地点时间
    private Date deliveryTime;  // 到达送货地点时间
    private Date leaveTime;     // 从取货点出发时间

    public Vehicle(int rank, Date gpsUpTime, double lat, double lng, double maxWeight, double maxVolume) {
        this.rank = rank;
        this.gpsUpTime = gpsUpTime;
        this.lat = lat;
        this.lng = lng;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.speed = 60;    // 默认速度60km/h
        this.isAvailable = true;
    }

    public Vehicle(int rank, Date gpsUpTime, double lat, double lng, double maxWeight, double maxVolume, double speed) {
        this.rank = rank;
        this.gpsUpTime = gpsUpTime;
        this.lat = lat;
        this.lng = lng;
        this.maxWeight = maxWeight;
        this.maxVolume = maxVolume;
        this.speed = speed;
        this.isAvailable = true;
    }

    // getter and setter
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Date getGpsUpTime() {
        return gpsUpTime;
    }

    public void setGpsUpTime(Date gpsUpTime) {
        this.gpsUpTime = gpsUpTime;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getMaxVolume() {
        return maxVolume;
    }

    public void setMaxVolume(double maxVolume) {
        this.maxVolume = maxVolume;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
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

    public Date getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(Date leaveTime) {
        this.leaveTime = leaveTime;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
