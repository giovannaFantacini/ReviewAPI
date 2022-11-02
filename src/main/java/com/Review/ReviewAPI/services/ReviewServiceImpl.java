package com.Review.ReviewAPI.services;

import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.model.ReviewDTO;
import com.Review.ReviewAPI.repository.ProductRepository;
import com.Review.ReviewAPI.repository.ReviewRepository;
import com.Review.ReviewAPI.repository.VoteRepository;
import com.Review.ReviewAPI.security.JwtUtils;
import com.sun.jdi.LongValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Optional<Review> findOne(Long reviewId) {
        return repository.findById(reviewId);
    }

    @Override
    public Review create(ReviewDTO rev) throws IOException, InterruptedException {
        boolean isPresent = productRepository.isPresent(rev.getSku());
        if(isPresent){
            Long userId = Long.valueOf(jwtUtils.getUserFromJwtToken(jwtUtils.getJwt()));
            final Review obj = Review.newFrom(rev,userId);
            return repository.save(obj);
        }else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Product doesn't exist");
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
        for(int i=0; i<sizeList; i++){
            var votes = voteRepository.getVotesByReviewId(reviewsProduct.get(i).getReviewId());
            votesByReview.put(reviewsProduct.get(i).getReviewId(), votes);
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

        var votes = voteRepository.getVotesByReviewId(reviewId);
        Long userId = Long.valueOf(jwtUtils.getUserFromJwtToken(jwtUtils.getJwt()));
        Review review = repository.getReviewById(reviewId);
        if (votes == 0 && Objects.equals(review.getUserId(), userId)) {
            repository.delete(review);
            return true;
        }else
            return false;
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

    @Override
    public String getStatus(Long reviewId){
        Review review = repository.getReviewById(reviewId);
        return review.getStatus();
    }

}
