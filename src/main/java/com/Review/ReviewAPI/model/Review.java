package com.Review.ReviewAPI.model;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import javax.validation.constraints.Size;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reviewId;

    @Column(nullable = true)
    @Size(max = 2048)
    private String text;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date date;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String skuProduct;

    @Column(nullable = true)
    private int rating;

    @Column(nullable = false)
    private String funFact;

    @Column(nullable = true)
    private Long userId;

    public Review() {
    }

    private Review(final String skuProduct,final String status,final Date date, final String text) {
        setStatus(status);
        setDate(date);
        setText(text);
        setSkuProduct(skuProduct);
    }

    private Review(final String skuProduct,final String status,final Date date, final String text, final int rating, final String funFact, final Long userId) {
        setStatus(status);
        setDate(date);
        setText(text);
        setSkuProduct(skuProduct);
        setRating(rating);
        getFunFactResponse(date);
        setUserId(userId);
    }


    public String getText() {
        return text;
    }

    public Long getReviewId() {
        return reviewId;
    }

    public String getSkuProduct() {
        return skuProduct;
    }

    public void setSkuProduct(String skuProduct) {
        this.skuProduct = skuProduct;
    }

    public void setText(final String text) {
        if (text.length()>2048){
            throw new IllegalArgumentException("Review Text Length is too big");
        }
        if (text.trim().length()==0){
            throw new IllegalArgumentException("Review Text cannot be white spaces");
        }
        this.text = text;
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

    public Date getDate(){return date;}

    public void setDate(Date date) {
        if (date == null){
            throw new IllegalArgumentException("Date is a mandatory attribute");
        }
        this.date = date;
    }

    public String getStatus(){return status;}

    public void setStatus(final String status){
        if(status == null || status.isEmpty()){
            throw new IllegalArgumentException("Status is a mandatory attribute");
        }
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFunFact() {
        return funFact;
    }

    public void setFunFact(String funFact) {
        if (funFact == null || funFact.isEmpty()){
            throw new IllegalArgumentException("Fun Fact Empty");
        }
        this.funFact = funFact;
    }

    public void getFunFactResponse(Date date) {
        String response;
        String finalResponse;
        String[] parts;
        String part1;
        int dot;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        String header = "Basic XXXXX";
        try {
            URL url = new URL("http://numbersapi.com/" + month + "/" + day + "/date");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("authorization",header);
            connection.setRequestProperty("Content-Type", "application/json");
            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bf.readLine()) != null )
            {
                stringBuilder.append(line);
            }
            response = stringBuilder.toString();
            finalResponse = response.substring(11);
            parts = finalResponse.split("\\.\",");
            part1 = parts[0];
            setFunFact(part1);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Review newFrom(final ReviewDTO rev, final Long userId) {
        final Review obj = new Review();
        long millis = System.currentTimeMillis();
        if(!rev.getText().isEmpty() || rev.getRating() != 0){
            obj.skuProduct = rev.sku;
            obj.status = "PENDING";
            obj.date = new Date(millis);
            obj.rating = rev.rating;
            obj.text = rev.text;
            obj.getFunFactResponse(obj.date);
            return new Review(obj.skuProduct, obj.status, obj.date, obj.text, obj.rating, obj.funFact, userId);
        }
        else{
            return obj;
        }
    }



}
