package com.Review.ReviewAPI.services;

import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.repository.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository repository;

    @Override
    public Optional<Review> findOne(Long reviewId) {
        return repository.findById(reviewId);
    }

    @Override
    public Review create(Review rev) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:8080/products?sku=" + rev.getSkuProduct()))
                                .build();

        HttpResponse response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        var code = response.statusCode();
        if(code == 200){
            return repository.save(rev);
        }else{
            return (Review) response.body();
        }

    }

    @Override
    public Page<Review> getAllReviews() {
        return (Page<Review>) repository.getAllReviews();
    }


}
