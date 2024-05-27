package com.example.musicreviews.repository;

import com.example.musicreviews.model.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findAll();

    Optional<Album> findById(Long id);

    List<Album> findAllByOrderByReleaseYearDesc();
}