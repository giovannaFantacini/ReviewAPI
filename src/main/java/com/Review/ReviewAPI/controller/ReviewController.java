package com.Review.ReviewAPI.controller;

import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.model.ReviewDTO;
import com.Review.ReviewAPI.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService service;

    @GetMapping(value = "/{reviewId}")
    public Optional<Review> findOne(@PathVariable("reviewId") final Long reviewId) {
        return service.findOne(reviewId);
    }

    @GetMapping(value = "/")
    public List<Review> getAllReviews() {
        return service.getAllReviews();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Review> createReview(@RequestBody final ReviewDTO rev) throws IOException, InterruptedException {
        final Review review = service.create(rev);
        return ResponseEntity.ok(review);
    }

    @GetMapping(value = "/{skuProducts}/votes")
    public Iterable<Review> getReviewsByProductOrderByVotes(@PathVariable("skuProducts") final String skuProducts) throws IOException, InterruptedException {
        return service.getReviewsByProductOrderByVotes(skuProducts);
    }

    @GetMapping(value = "/pending")
    public Iterable<Review> getAllPendingReviews(@RequestParam("offset") final int offset ,@RequestParam("pageSize") final int size) {
        return service.getAllPendingReviews(offset,size);
    }

    @PutMapping(value = "/{reviewId}/approve/{reviewStatus}")
    public ResponseEntity<String> approveRejectReview(@PathVariable("reviewId") final Long reviewId, @PathVariable ("reviewStatus") final Boolean reviewStatus){
        Boolean status = service.approveRejectReview(reviewId,reviewStatus);
        if(!status){
            return ResponseEntity.ok("The review's id you gave it's not associated with a review or this is not in PENDING status");

        }else
            return ResponseEntity.ok("Review's status is changed");
    }

    @DeleteMapping(value = "/{reviewId}/remove")
    public ResponseEntity<String> deleteByReviewId(@PathVariable ("reviewId") final Long reviewId) throws IOException, InterruptedException {
        Boolean deleted = service.deleteReview(reviewId);
        if(deleted) {
            return ResponseEntity.ok("Review deleted");
        } else
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review can't be deleted because have votes or you are note de creator");
    }

    @GetMapping(value = "/{sku}/rating")
    public RatingFrequency getRatingFrequency(@PathVariable("sku") final String sku) {
        return service.getRatingFrequencyOfProduct(sku);
    }

    @GetMapping(value = "/status/{reviewId}")
    public String getStatus(@PathVariable ("reviewId") final Long reviewId) {
        return service.getStatus(reviewId);
    }
}
