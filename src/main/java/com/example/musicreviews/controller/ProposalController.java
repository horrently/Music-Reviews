package com.example.musicreviews.controller;

import com.example.musicreviews.model.Album;
import com.example.musicreviews.model.AlbumProposal;
import com.example.musicreviews.security.UserDetailsImpl;
import com.example.musicreviews.service.AlbumProposalService;
import com.example.musicreviews.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;

@Controller
public class ProposalController {
    @Autowired
    private AlbumProposalService albumProposalService;

    @Autowired
    private AlbumService albumService;

    @GetMapping("/proposals")
    public String showProposals(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN_ROLE"))) {
                return "redirect:/";
            }
            model.addAttribute("user", userDetails);
            List<AlbumProposal> proposals = albumProposalService.getAllProposals();
            model.addAttribute("proposals", proposals);
        }
        return "proposals";
    }


    @GetMapping("/proposals/new")
    public String showProposalForm(Model model) {
        model.addAttribute("proposal", new AlbumProposal());
        return "album_form";
    }

    @PostMapping("/proposals/new")
    public String submitProposal(@ModelAttribute("proposal") AlbumProposal proposal) {
        albumProposalService.saveProposal(proposal);
        return "redirect:/";
    }

    @PostMapping("/proposals/{id}/approve")
    public String approveProposal(@PathVariable Long id) {
        Optional<AlbumProposal> proposalOpt = albumProposalService.getProposalById(id);
        if (proposalOpt.isPresent()) {
            AlbumProposal proposal = proposalOpt.get();
            Album album = new Album();
            album.setTitle(proposal.getTitle());
            album.setArtist(proposal.getArtist());
            album.setGenre(proposal.getGenre());
            album.setReleaseYear(proposal.getReleaseYear());
            albumService.saveAlbum(album);
            albumProposalService.deleteProposalById(id);
        }
        return "redirect:/proposals";
    }

    @PostMapping("/proposals/{id}/reject")
    public String rejectProposal(@PathVariable Long id) {
        albumProposalService.deleteProposalById(id);
        return "redirect:/proposals";
    }


}
