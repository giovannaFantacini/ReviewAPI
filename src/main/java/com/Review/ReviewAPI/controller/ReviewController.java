package com.Review.ReviewAPI.controller;

import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public Iterable<Review> getAllReviews() {
        return service.getAllReviews();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Review> createReview(@RequestBody final Review rev) throws IOException, InterruptedException {
        final Review review = service.create(rev);
        return ResponseEntity.ok(review);
    }
}
