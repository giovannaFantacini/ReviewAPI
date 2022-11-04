package com.Review.ReviewAPI.services;


import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.model.ReviewDTO;
import java.io.IOException;
import java.util.List;

public interface ReviewService {

    Review getReviewById(Long reviewId) throws IOException, InterruptedException;

    Review create(ReviewDTO rev) throws IOException, InterruptedException;

    List<Review> getAllReviews() throws IOException, InterruptedException;

    List<Review> getAllReviewsBySku(String sku) throws IOException, InterruptedException;

    List<Review> getReviewsByProductOrderByVotes(String sku) throws IOException, InterruptedException;

    List<Review> getAllPendingReviews() throws IOException, InterruptedException;
    Boolean approveRejectReview(Long reviewId, Boolean status) throws IOException, InterruptedException;

    Boolean deleteReview(Long reviewId) throws IOException, InterruptedException;

    RatingFrequency getRatingFrequencyOfProduct(String sku) throws IOException, InterruptedException;

    String getStatus(Long reviewId);

}
