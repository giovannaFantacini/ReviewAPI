package com.Review.ReviewAPI.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    @Column(nullable = true)
    private String text;

    @Temporal(TemporalType.DATE)
    @Column(nullable = true)
    private Date date;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String skuProduct;

    @Column(nullable = true)
    private int rating;

    public Review() {
    }

    public Review(String text, String status, String skuProduct, int rating) {
        this.text = text;
        this.status = status;
        this.skuProduct = skuProduct;
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSkuProduct() {
        return skuProduct;
    }

    public void setSkuProduct(String skuProduct) {
        this.skuProduct = skuProduct;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public int getRating(){
        return rating;
    }

    public void setRating(int rating){
        if (rating<0 || rating>5 ){
            throw new IllegalArgumentException("Rating out of range");
        }
        this.rating = rating;
    }

}
