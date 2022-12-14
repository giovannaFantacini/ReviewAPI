package com.Review.ReviewAPI.services;

import com.Review.ReviewAPI.model.RatingFrequency;
import com.Review.ReviewAPI.model.Review;
import com.Review.ReviewAPI.model.ReviewDTO;
import com.Review.ReviewAPI.repository.ProductRepository;
import com.Review.ReviewAPI.repository.Review2Repository;
import com.Review.ReviewAPI.repository.ReviewRepository;
import com.Review.ReviewAPI.repository.VoteRepository;
import com.Review.ReviewAPI.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReviewServiceImpl implements ReviewService {
    @Autowired
    private ReviewRepository repository;

    @Autowired
    private Review2Repository repository2;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Review getReviewById(Long reviewId) throws IOException, InterruptedException {
        Review review = repository.getReviewById(reviewId);
        if(review == null){
            return repository2.getReviewbyId(reviewId);
        }
        return review;
    }

    @Override
    public Review internalGetReviewById(Long reviewId) {
        return repository.getReviewById(reviewId);
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
    public List<Review> getAllReviewsBySku(String sku) throws IOException, InterruptedException {
        List<Review> reviews = repository.getReviewsByProduct(sku);
        if (reviews.isEmpty()){
            return repository2.getReviewsBySku(sku);
        }
        return reviews;
    }

    @Override
    public List<Review> internalGetAllReviewsBySku(String sku){
        return repository.getReviewsByProduct(sku);
    }

    @Override
    public List<Review> getAllReviews(){
        return repository.getAllReviews();
    }

    @Override
    public List<Review> getAllPendingReviews(){
        return repository.getAllPendingReviews();
    }

    @Override
    public List<Review> getAllMyReviews(){
        Long userId = Long.valueOf(jwtUtils.getUserFromJwtToken(jwtUtils.getJwt()));
        return repository.getAllMyReviews(userId);
    }

    @Override
    public List<Review> getReviewsByProductOrderByVotes(String sku) throws IOException, InterruptedException {
        List<Review> reviewsProduct = repository.getReviewsByProduct(sku);
        if (reviewsProduct.isEmpty()){
            reviewsProduct = repository2.getReviewsBySku(sku);
        }
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
    public RatingFrequency getRatingFrequencyOfProduct(String sku) throws IOException, InterruptedException {

        List<Review> reviews = Stream.concat(repository.getReviewsByProduct(sku).stream(), repository2.getReviewsBySku(sku).stream()).collect(Collectors.toList());
        int rating;
        int one=0, two=0, three=0, four=0, five=0;
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
        int some = one+two+three+four+five;
        if (some != 0) {
            float globalRating = (one * 1 + two * 2 + three * 3 + four * 4 + five * 5) / some;
            return new RatingFrequency(one, two, three, four, five, globalRating);
        }else{
            float globalRating = 0;
            return new RatingFrequency(one, two, three, four, five, globalRating);
        }
    }

    @Override
    public String getStatus(Long reviewId) throws IOException, InterruptedException {
        Review review = repository.getReviewById(reviewId);
        if(review == null){
           review = repository2.getReviewbyId(reviewId);
        }
        return review.getStatus();
    }

}
