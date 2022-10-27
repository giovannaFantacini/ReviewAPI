package com.Review.ReviewAPI.services;

import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.model.ReviewDTO;
import com.Review.ReviewAPI.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository repository;

    @Override
    public Optional<Review> findOne(Long reviewId) {
        return repository.findById(reviewId);
    }

    @Override
    public Review create(ReviewDTO rev) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:8080/products?sku=" + rev.getSku()))
                                .build();

        HttpResponse response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        var code = response.statusCode();
        if(code == 200){
            Long userId = Long.valueOf(123456);
            final Review obj = Review.newFrom(rev,userId);
            return repository.save(obj);
        }else{
            return (Review) response.body();
        }

    }

    @Override
    public List<Review> getAllReviews() {
        return repository.getAllReviews();
    }

    @Override
    public Page <Review> getAllPendingReviews(int offset, int pageSize){
        return repository.getAllPendingReviews(PageRequest.of(offset,pageSize));
    }

    @Override
    public List<Review> getReviewsByProductOrderByVotes(String sku) throws IOException, InterruptedException {
        List<Review> reviewsProduct = repository.getReviewsByProduct(sku);
        List<Review> reviewsOrderByVote = new ArrayList<>();
        int sizeList = reviewsProduct.size();
        Map<Long,Integer> votesByReview = new HashMap<Long,Integer>();
        HttpClient client = HttpClient.newHttpClient();
        for(int i=0; i<sizeList; i++){
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:8082/votes/" + reviewsProduct.get(i).getReviewId()))
                    .build();

            HttpResponse response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                var votes = Integer.parseInt(response.body().toString());
                votesByReview.put(reviewsProduct.get(i).getReviewId(), votes);
            }

        }

        votesByReview = votesByReview.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        Long higherVotes;
        Long ReviewId;
       for(Map.Entry mapElement : votesByReview.entrySet()){
           higherVotes = (Long) mapElement.getKey();
           for(int j=0; j<sizeList; j++){
               ReviewId = reviewsProduct.get(j).getReviewId();
               if(higherVotes == ReviewId){
                   reviewsOrderByVote.add(reviewsProduct.get(j));
                   break;
               }
           }
       }

        return reviewsOrderByVote;
    }

    @Override
    public Boolean approveRejectReview(Long reviewId, Boolean status){
        Review review = repository.getReviewById(reviewId);
        try {
            if (Objects.equals(review.getStatus(), "PENDING")) {
                if (status) {
                    review.setStatus("APPROVED");
                } else {
                    review.setStatus("REJECTED");
                }
                repository.save(review);
                return true;
            }else {
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }

    }

    public Boolean deleteReview(Long reviewId) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8082/votes/" + reviewId))
                .build();

        HttpResponse response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        if(response.statusCode() == 200){
            var votes = Integer.parseInt(response.body().toString());
            Long userId = Long.valueOf(123456);//Brute forced code for testing
            Review review = repository.getReviewById(reviewId);
            if(votes == 0 && Objects.equals(review.getUserId(), userId)){
                repository.delete(review);
                return true;
            }
        }
        return  false;
    }

    @Override
    public RatingFrequency getRatingFrequencyOfProduct(String sku){

        List<Review> reviews = repository.getReviewsByProduct(sku);
        int rating;
        int one=0, two=0, three=0, four=0, five=0;
        RatingFrequency freq = new RatingFrequency();
        for (int i=0; i< reviews.size(); i++){
            rating=reviews.get(i).getRating();
            if (rating == 1){
                one = one + 1;
            }
            else if (rating == 2){
                two = two + 1;
            }
            else if (rating == 3){
                three = three + 1;
            }
            else if (rating == 4){
                four = four + 1;
            }
            else if (rating == 5){
                five = five + 1;
            }
        }
        float globalRating = repository.getAggregatedRating(sku);
        return new RatingFrequency(one, two, three, four, five, globalRating);
    }

}
