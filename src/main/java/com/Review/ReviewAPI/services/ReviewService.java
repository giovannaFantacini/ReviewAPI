package com.Review.ReviewAPI.services;

import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.Optional;

public interface ReviewService {

    Optional<Review> findOne(Long reviewId);

    Review create(Review rev) throws IOException, InterruptedException;

    Page <Review> getAllReviews();

}
