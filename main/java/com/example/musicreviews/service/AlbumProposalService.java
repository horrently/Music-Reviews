package com.example.musicreviews.service;

import com.example.musicreviews.model.AlbumProposal;
import com.example.musicreviews.repository.AlbumProposalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumProposalService {
    @Autowired
    private AlbumProposalRepository albumProposalRepository;

    public List<AlbumProposal> getAllProposals() {
        return albumProposalRepository.findAll();
    }

    public AlbumProposal saveProposal(AlbumProposal proposal) {
        return albumProposalRepository.save(proposal);
    }

    public Optional<AlbumProposal> getProposalById(Long id) {
        return albumProposalRepository.findById(id);
    }

    public void deleteProposalById(Long id) {
        albumProposalRepository.deleteById(id);
    }
}
