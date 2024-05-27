package com.example.musicreviews.repository;

import com.example.musicreviews.model.AlbumProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumProposalRepository extends JpaRepository<AlbumProposal, Long> {

    Optional<AlbumProposal> findById(Long id);

}
