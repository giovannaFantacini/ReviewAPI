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

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewService service;

    @GetMapping(value = "/{reviewId}")
    public ResponseEntity<Review> findOne(@PathVariable("reviewId") final Long reviewId) throws IOException, InterruptedException {
        Review review = service.getReviewById(reviewId);
        if (review==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found");
        }
        return ResponseEntity.ok().body(review);
    }

    @GetMapping(value = "/internal/{reviewId}")
    public ResponseEntity<Review> internalFindOne(@PathVariable("reviewId") final Long reviewId){
        Review review = service.internalGetReviewById(reviewId);
        if (review==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found");
        }
        return ResponseEntity.ok().body(review);
    }

    @GetMapping(value = "/")
    public List<Review> getAllReviews() throws IOException, InterruptedException {
        return service.getAllReviews();
    }

    @GetMapping(value = "/myReviews")
    public Iterable<Review> getAllMyReviews() {
        return service.getAllMyReviews();
    }

    @GetMapping(value = "/{sku}/product")
    public List<Review> getAllReviewsBySku(@PathVariable("sku")final String sku) throws IOException, InterruptedException {
        return service.getAllReviewsBySku(sku);
    }

    @GetMapping(value = "/internal/{sku}/product")
    public List<Review> internalGetAllReviewsBySku(@PathVariable("sku")final String sku){
        return service.internalGetAllReviewsBySku(sku);
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
    public Iterable<Review> getAllPendingReviews() throws IOException, InterruptedException {
        return service.getAllPendingReviews();
    }

    @PutMapping(value = "/{reviewId}/approve/{reviewStatus}")
    public ResponseEntity<String> approveRejectReview(@PathVariable("reviewId") final Long reviewId, @PathVariable ("reviewStatus") final Boolean reviewStatus) throws IOException, InterruptedException {
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
    public RatingFrequency getRatingFrequency(@PathVariable("sku") final String sku) throws IOException, InterruptedException {
        return service.getRatingFrequencyOfProduct(sku);
    }

    @GetMapping(value = "/status/{reviewId}")
    public String getStatus(@PathVariable ("reviewId") final Long reviewId) throws IOException, InterruptedException {
        return service.getStatus(reviewId);
    }
}
