package com.Review.ReviewAPI.services;


import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.model.ReviewDTO;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Optional<Review> findOne(Long reviewId);

    Review create(ReviewDTO rev) throws IOException, InterruptedException;

    List<Review> getAllReviews();

    List<Review> getReviewsByProductOrderByVotes(String sku) throws IOException, InterruptedException;

    Page<Review> getAllPendingReviews(int offset, int pageSize);
    Boolean approveRejectReview(Long reviewId, Boolean status);

    Boolean deleteReview(Long reviewId) throws IOException, InterruptedException;

    RatingFrequency getRatingFrequencyOfProduct(String sku);

    String getStatus(Long reviewId);

}
