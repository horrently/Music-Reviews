package com.example.musicreviews.repository;

import com.example.musicreviews.model.AlbumReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumReviewRepository extends JpaRepository<AlbumReview, Long> {
    List<AlbumReview> findAll();

    Optional<AlbumReview> findById(Long id);

    List<AlbumReview> findByAlbumId(Long albumId);

    List<AlbumReview> findByUserId(Long userId);

    List<AlbumReview> findByUserIdAndAlbumId(Long userId, Long albumId);
}
