package com.Review.ReviewAPI.model;

public class ReviewDTO {

    String text;
    int rating;

    String sku;

    public ReviewDTO(String sku, String text, int rating) {
        this.text = text;
        this.rating = rating;
        this.sku = sku;
    }

    public String getText() {
        return text;
    }

    public int getRating() {
        return rating;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
