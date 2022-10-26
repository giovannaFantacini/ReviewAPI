package com.Review.ReviewAPI.services;

import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ReviewService {

    Optional<Review> findOne(Long reviewId);

    Review create(Review rev) throws IOException, InterruptedException;

    List<Review> getAllReviews();

    List<Review> getReviewsByProductOrderByVotes(String sku) throws IOException, InterruptedException;

}
