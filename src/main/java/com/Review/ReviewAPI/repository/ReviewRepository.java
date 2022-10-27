package com.Review.ReviewAPI.repository;

import com.Review.ReviewAPI.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId")
    Review getReviewById(@Param("reviewId") Long reviewId);
    @Query("SELECT r FROM Review r")
    List<Review> getAllReviews();

    @Query("SELECT r FROM Review r WHERE r.skuProduct = :skuProduct")
    List<Review> getReviewsByProduct(@Param("skuProduct") String skuProduct);

    @Query("SELECT r FROM Review r WHERE r.status ='PENDING'")
    Page <Review> getAllPendingReviews(Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.userId = :userId")
    Page <Review> getAllMyReviews(@Param("userId") Long userId,Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.skuProduct = :sku")
    Float getAggregatedRating(@Param("sku") String sku);

}

