package com.example.musicreviews.service;

import com.example.musicreviews.model.AlbumReview;
import com.example.musicreviews.repository.AlbumReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumReviewService {
    @Autowired
    private AlbumReviewRepository albumReviewRepository;

    public List<AlbumReview> getAllReviews() {
        return albumReviewRepository.findAll();
    }

    public Optional<AlbumReview> getReviewById(Long id) {
        return albumReviewRepository.findById(id);
    }

    public List<AlbumReview> getReviewsByAlbumId(Long albumId) {
        return albumReviewRepository.findByAlbumId(albumId);
    }

    public AlbumReview saveReview(AlbumReview review) {
        return albumReviewRepository.save(review);
    }

    public List<AlbumReview> getReviewsByUserId(Long userId) {
        return albumReviewRepository.findByUserId(userId);
    }

    public void deleteReview(Long reviewId) {
        albumReviewRepository.deleteById(reviewId);
    }

    public List<AlbumReview> getReviewsByUserIdAndAlbumId(Long userId, Long albumId) {
        return albumReviewRepository.findByUserIdAndAlbumId(userId, albumId);
    }


}
